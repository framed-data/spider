(ns spider.request
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.walk :as walk]
            [clj-json.core :as json]
            [ring.util.codec]))

(defn uri-parts
  "Split chunks of a request URI, ex:

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
       (walk/keywordize-keys)))

(defn- slurp-body [request]
  (slurp (io/reader (:body request))))

(defn read-json-body [request]
  (try (json/parse-string (slurp-body request))
       (catch Exception ex nil)))

(defn read-edn-body [request]
  (try (edn/read-string (slurp-body request))
       (catch Exception ex nil)))
