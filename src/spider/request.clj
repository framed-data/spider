(ns spider.request
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.walk :as walk]
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

(defn read-edn-body [request]
  (let [s (->> (:body request)
               io/reader
               slurp)]
    (try (edn/read-string s)
         (catch Exception ex nil))))
