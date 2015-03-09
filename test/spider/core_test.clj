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
