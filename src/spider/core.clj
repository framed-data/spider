(ns spider.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [ring.util.codec]
            (spider
              [http :as http]
              [response :as response])))

(def edn http/edn)
(def json http/json)
(def html http/html)
(def plain http/plain)
(def form-encoded http/form-encoded)

(defn accept-dispatcher
  "Construct a handler function that will dispatch to one of several
   sub-handlers depending on the Accept header of the request

  (def my-endpoint
    (accept-dispatcher
      {\"application/edn\" (fn [request] ...)}
      my-not-acceptable-handler))"
  [ct-map default-handler]
  (fn [request]
    (if-let [handler
             (some-> (get-in request [:headers "accept"])
                     (string/split #",")
                     first
                     ct-map)]
      (handler request)
      (default-handler request))))

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

(defn content-type-dispatcher
  "Construct a handler function that will dispatch to one of several
   sub-handlers depending on the Content-Type of the request

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
            parse
            (fn [media-type-str]
              (let [{:keys [type subtype]} (parse-media-type media-type-str)]
                  (str type "/" subtype)))

            primary-accept (-> accept (string/split #",") first parse)

            primary-match (ct-map primary-accept)
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
  (fn [request]
    (if-let [handler (-> request :request-method method-handler-map)]
      (handler request)
      (response/not-allowed))))
