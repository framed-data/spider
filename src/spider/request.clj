(ns spider.request
  "Utilities for parsing/handling Ring requests"
  (:require (clojure
              [edn :as edn]
              [string :as string]
              [walk :as walk])
            [clj-json.core :as json]
            [ring.util.codec]))

(defn uri-parts
  "Split chunks of a request URI
   Ex:
     (uri-parts {:uri \"https://subdomain.mysite.com/page?hello=world\"})
     ; => (\"https:\" \"subdomain.mysite.com\" \"page?hello=world\")"
  [request]
  (let [parts-unfiltered
        (-> (:uri request)
            ring.util.codec/url-decode
            (string/split  #"/"))]
    (filter (complement string/blank?) parts-unfiltered)))

(defn form-params
  "Parse form params from a request, converting keys to keywords"
  [request]
  (->> (:form-params request)
       walk/keywordize-keys))

(defn read-json-body
  "Parse the body of a request as JSON"
  [request]
  (json/parse-string (slurp (:body request))))

(defn read-edn-body
  "Parse the body of a request as EDN"
  [request]
  (edn/read-string (slurp (:body request))))
