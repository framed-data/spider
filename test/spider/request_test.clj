(ns spider.request-test
  (:require [clojure.test :refer :all]
            [spider.request :as request]))

(defn- ->reader [s]
  (java.io.StringReader. s))

(deftest test-uri-parts
  (let [uri "/one/two?hello=world"]
    (is (= ["one" "two?hello=world"]
           (request/uri-parts {:uri uri})))))

(deftest test-form-params
  (let [raw-params {"foo" "bar"
                    "quux" 3}]
    (is (= {:foo "bar" :quux 3}
           (request/form-params {:form-params raw-params})))))

(deftest test-read-json-body
  (let [req {:body (->reader "{\"hello\":\"world\"}")}]
    (is (= {"hello" "world"} (request/read-json-body req)))))

(deftest test-read-edn-body
  (let [req {:body (->reader "{:hello \"world\"}")}]
    (is (= {:hello "world"} (request/read-edn-body req)))))
