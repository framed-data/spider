(ns spider.request-test
  (:require [clojure.test :refer :all]
            [spider.request :as request]))

(deftest test-uri-parts
  (let [uri "https://subdomain.mysite.com/page?hello=world"]
    (is (= ["https:" "subdomain.mysite.com" "page?hello=world"]
           (request/uri-parts {:uri uri})))))

(defn ->reader [s]
  (java.io.StringReader. s))

(deftest test-read-json-body
  (let [req {:body (->reader "{\"hello\":\"world\"}")}]
    (is (= {"hello" "world"} (request/read-json-body req)))))

(deftest test-read-edn-body
  (let [req {:body (->reader "{:hello \"world\"}")}]
    (is (= {:hello "world"} (request/read-edn-body req)))))
