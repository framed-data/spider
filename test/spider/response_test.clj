(ns spider.response-test
  (:require [clojure.test :refer :all]
            [spider.response :as response]))

(response/defresponse teapot 418 "HTTP 418: I'm a teapot")

(deftest test-defresponse
  (is (= {:status 418
          :headers {"Content-Type" "text/plain"}
          :body "HTTP 418: I'm a teapot"}
         (teapot)))
  (is (= {:status 418
          :headers {"Content-Type" "text/plain"}
          :body "short and stout"}
         (teapot "short and stout")))
  (is (= {:status 418
          :headers {"Content-Type" "application/edn"}
          :body "{:this-is-my-handle \"this is my spout\"}"}
         (teapot "application/edn" {:this-is-my-handle "this is my spout"}))))
