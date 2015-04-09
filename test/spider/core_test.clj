(ns spider.core-test
  (:require [clojure.test :refer :all]
            [spider.core :as spider]))

(deftest test-parse-media-type
  (is (= {:type "application"
          :subtype "x-www-form-urlencoded"
          :parameter nil}
         (spider/parse-media-type
           "application/x-www-form-urlencoded")))

  (is (= {:type "application"
          :subtype "x-www-form-urlencoded"
          :parameter "charset=UTF-8"}
         (spider/parse-media-type
           "application/x-www-form-urlencoded; charset=UTF-8"))))

(deftest test-accept-dispatcher
  (let [dispatcher
        (spider/accept-dispatcher {"text/html" (constantly :html)}
                                   (constantly :default))]
    (is (= :html (dispatcher {:headers {"accept" "text/html"}})))
    (is (= :html (dispatcher {:headers {"accept" "text/html,text/plain"}})))
    (is (= :default (dispatcher {:headers {"accept" "text/plain,text/html"}})))
    (is (= :default (dispatcher {:headers {}})))
    (is (= :default (dispatcher {:headers {"accept" ""}})))))

(deftest test-content-type-dispatcher
  (let [->request (fn [content-type] {:headers {"content-type" content-type}})]
    (let [dispatcher
          (spider/content-type-dispatcher {"application/edn" (fn [_] "correct")})

          bad-response (dispatcher (->request "text/foo"))]
      (is (= "correct" (dispatcher (->request "application/edn"))))
      (is (= "correct" (dispatcher (->request "application/edn;charset=utf-8"))))
      (is (= 415 (:status bad-response))))
    (testing "with charset dispatcher"
      (let [dispatcher
            (spider/content-type-dispatcher {"application/edn;charset=utf-8" (fn [_] "correct")})]
        (is (= "correct" (dispatcher (->request "application/edn"))))
        (is (= "correct" (dispatcher (->request "application/edn;charset=utf-8"))))))))
