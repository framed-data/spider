(ns spider.response-test
  (:require [clojure.test :refer :all]
            [spider.response :as response]))

(response/defresponse teapot 418 "HTTP 418: I'm a teapot")

(deftest test-edn
  (is (= {:status 200
          :headers {"Content-Type" "application/edn;charset=utf-8"}
          :body "{:my-key \"my val\"}"}
         (response/edn {:my-key "my val"}))))

(deftest test-defresponse
  (is (= {:status 418
          :headers {"Content-Type" "text/plain;charset=utf-8"}
          :body "HTTP 418: I'm a teapot"}
         (teapot)))
  (is (= {:status 418
          :headers {"Content-Type" "text/plain;charset=utf-8"}
          :body "short and stout"}
         (teapot "short and stout")))
  (is (= {:status 418
          :headers {"Content-Type" "application/edn;charset=utf-8"}
          :body "{:this-is-my-handle \"this is my spout\"}"}
         (teapot "application/edn;charset=utf-8" {:this-is-my-handle "this is my spout"}))))
