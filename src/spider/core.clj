(ns spider.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [ring.util.codec]
            (spider
              [http :as http]
              [response :as response])))

(def edn "The EDN content-type string" http/edn)
(def json "The JSON content-type string" http/json)
(def html "The HTML content-type string" http/html)
(def plain "The text/plain content-type string" http/plain)
(def form-encoded "The form-urlencoded content-type string" http/form-encoded)

(defn parse-media-type
  "See RFC 2616 section 3.7 Media Types

   Ex:
     (spider/parse-media-type
       \"application/x-www-form-urlencoded; charset=UTF-8\")

     {:type \"application\"
      :subtype \"x-www-form-urlencoded\"
      :parameter \"charset=UTF-8\"}"
  [s]
  (let [re #"(\w+)/([\w-]+)(;[\s+]?(\S*))?"
        [_ type subtype _ parameter] (re-matches re s)]
    {:type type
     :subtype subtype
     :parameter parameter}))

(defn strip-params
  "Return just the type/subtype of a media type string
   See RFC 2616 section 3.7 Media Types

   Ex:
    (strip-params \"application/x-www-form-urlencoded; charset=UTF-8\")
    ; => \"application/x-www-form-urlencoded\""
  [media-type-str]
  (let [{:keys [type subtype]} (parse-media-type media-type-str)]
    (str type "/" subtype)))

(defn- find-match
  "All content-type strings will be matched using just the type and subtype
   parts. Other parameters will be ignored in both the `ct-map` and the
   content-type header."
  [ct-map content-type]
  (let [content-type' (strip-params content-type)]
    (some (fn [[ct handler]]
            (when (= content-type' (strip-params ct)) handler))
          ct-map)))

(defn accept-dispatcher
  "Construct a handler function that will dispatch to one of several
   sub-handlers depending on the Accept header of the request

   Ex:
     (def my-endpoint
       (accept-dispatcher
         {\"application/edn\" (fn [request] ...)}
         my-not-acceptable-handler))"
  [ct-map default-handler]
  (fn [request]
    (let [primary-accept
          (-> (get-in request [:headers "accept"])
              (or "")
              (string/split #",")
              first)
          handler (find-match ct-map primary-accept)]
      (if handler
        (handler request)
        (default-handler request)))))

(defn content-type-dispatcher
  "Construct a handler function that will dispatch to one of several
   sub-handlers depending on the Content-Type of the request.

   Ex:
     (def my-endpoint
       (content-type-dispatcher
         {\"application/x-www-form-urlencoded\" form-handler
          \"application/edn\" edn-handler}))

   Alternatively, you can use predefined aliases:

     (def my-endpoint
       (content-type-dispatcher
         {spider/form-encoded form-handler
          spider/edn edn-handler
          spider/json json-handler}))"
  [ct-map]
  (let [preferred-type (first (keys ct-map))
        unsupported-type-response
        (response/unsupported-type preferred-type)]
    (fn [request]
      (let [accept (get-in request [:headers "content-type"])
            primary-accept (-> accept (string/split #",") first)
            primary-match (find-match ct-map primary-accept)
            else-handler (ct-map :else)
            default-handler (fn [_] unsupported-type-response)]
        ((or primary-match else-handler default-handler) request)))))

(defn method-dispatcher
  "Construct a handler function that will dispatch to one of several
   sub-handlers depending on the type of the request

   Ex:
     (def my-endpoint
       (method-dispatcher
         {:get my-get-handler
          :post my-post-handler}))"
  [method-handler-map]
  (fn [{:keys [request-method] :as request}]
    (if-let [handler (get method-handler-map request-method)]
      (handler request)
      (response/not-allowed))))
