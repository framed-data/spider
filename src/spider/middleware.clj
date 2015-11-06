(ns spider.middleware)

(defn using-middleware
  "Construct a function that will run through seq of middleware functions
   in order (left-right) and ultimately invoke handler. Middleware functions
   are free to short-circuit as desired.

   See https://github.com/ring-clojure/ring/wiki/Concepts#middleware

   Ex:
     (defn wrap-foo [handler]
       (fn [request]
         (println \"foo\")
         (handler request)))

     (defn wrap-bar [handler]
       (fn [request]
         (println \"bar\")
         (when authorized?
           (handler request))))

     (defn wrap-quux [handler]
       (fn [request]
         (println \"quux\")
         (handler request)))

     (def middleware [wrap-foo
                      wrap-bar
                      wrap-quux])

     (def my-handler
       (using-middleware middleware
         (fn [request] (println \"my handler\"))))

     (my-handler request)
     ; => \"foo\"
     ; => \"bar\"
     ; => \"quux\"
     ; => \"my-handler\""
  [middleware handler]
  (loop [f handler
         ms (reverse middleware)]
    (if (seq ms)
      (recur ((first ms) f) (rest ms))
      f)))
