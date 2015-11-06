(ns spider.middleware-test
  (:require [clojure.test :refer :all]
            [spider.middleware :as m]))

(deftest test-using-middleware
  (let [log (atom [])
        foo (fn [handler] (fn [request] (swap! log conj :foo) (handler request)))
        bar (fn [handler] (fn [request] (swap! log conj :bar) (handler request)))
        quux (fn [handler] (fn [request] (swap! log conj :quux) (handler request)))
        middleware [foo bar quux]

        handler (m/using-middleware middleware
                  (fn [request] (swap! log conj :final)))
        request {}]
    (is (empty? @log))
    (handler request)
    (is (= [:foo :bar :quux :final] @log))))
