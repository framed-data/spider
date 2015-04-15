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
  (let [r1 {:body (->reader "{\"hello\":\"world\"}")}
        r2 {:body (->reader "{\"hello\"}")}] ; Invalid JSON
    (is (= {"hello" "world"} (request/read-json-body r1)))
    (is (= nil (request/read-edn-body r2)))))

(deftest test-read-edn-body
  (let [r1 {:body (->reader "{:hello \"world\"}")}
        r2 {:body (->reader "{:hello}")}] ; Invalid EDN
    (is (= {:hello "world"} (request/read-edn-body r1)))
    (is (= nil (request/read-edn-body r2)))))
