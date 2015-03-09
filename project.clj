(defproject spider "0.1.0"
  :description "A tiny set of web utilities on top of Ring"
  :url "https://github.com/framed-data/spider"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-json "0.5.3"]
                 [ring "1.3.0"]]
  :profiles {:uberjar {:aot :all}})
