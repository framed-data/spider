(ns spider.request
  "Utilities for parsing/handling Ring requests"
  (:require (clojure
              [string :as string]
              [walk :as walk])
            [clj-json.core :as json]
            [ring.util.codec]
            [framed.std.core :as std])
  (:import (org.codehaus.jackson JsonParseException)))

(defn uri-parts
  "Split chunks of a request URI

   Ex:
     (uri-parts {:uri \"/one/two?hello=world\"})
     ; => (\"one\" \"two?hello=world\")"
  [{:keys [uri] :as request}]
  (let [parts-unfiltered
        (-> (ring.util.codec/url-decode uri)
            (string/split #"/"))]
    (filter (complement string/blank?) parts-unfiltered)))

(defn form-params
  "Parse form params from a request, converting keys to keywords"
  [{:keys [form-params] :as request}]
  (walk/keywordize-keys form-params))

(defn read-json-body
  "Parse the body of a request as JSON, or return nil if empty or failed"
  [{:keys [body] :as request}]
  (try (some-> body slurp json/parse-string)
    (catch JsonParseException ex nil)))

(defn read-edn-body
  "Parse the body of a request as EDN, or return nil if empty or failed"
  [{:keys [body] :as request}]
  (some-> body slurp std/from-edn))
