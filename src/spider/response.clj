(ns spider.response
  (:require [clj-json.core :as clj-json]
            [spider.http :as http]))

(defn edn
  ([body]
   (edn 200 body))
  ([status body]
   {:status status
    :headers {"Content-Type" http/edn}
    :body (pr-str body)}))

(defn json
  ([body]
   (json 200 body))
  ([status body]
   {:status status
    :headers {"Content-Type" http/json}
    :body (clj-json/generate-string body)}))

(defn html
  ([body]
   (html 200 body))
  ([status body]
   {:status status
    :headers {"Content-Type" http/html}
    :body body}))

;;

(defn render-body [content-type body]
  (condp = content-type
    http/edn (pr-str body)
    http/json (clj-json/generate-string body)
    body))

(defmacro defresponse [resp-name status default-body]
  `(defn ~resp-name
     ([]
      (~resp-name ~default-body))
     ([body#]
      (~resp-name http/plain body#))
     ([content-type# body#]
      {:status ~status
       :headers {"Content-Type" content-type#}
       :body (render-body content-type# body#)})))

(defresponse bad-request 400 "HTTP 400: Bad Request")

(defresponse not-authorized 401 "HTTP 401: Not Authorized")

(defresponse forbidden 403 "HTTP 403: Forbidden")

(defresponse not-found 404 "HTTP 404: Not Found")

(defresponse not-allowed 405 "HTTP 405: Method Not Allowed")

(defn not-acceptable [acceptable-type]
  {:status 406
   :headers {"Content-Type" http/plain}
   :body (str "HTTP 406: Not Acceptable; use " acceptable-type)})

(defresponse request-timeout 408 "HTTP 408: Request Timeout")

(defn unsupported-type [preferred-type]
  {:status 415
   :headers {"Content-Type" http/plain}
   :body (str "HTTP 415: Unsupported Media Type."
              (when preferred-type
                (str " Prefer " preferred-type)))})

(defresponse unprocessable 422 "HTTP 422: Unprocessable Entity")

(defresponse internal-server-error 500 "HTTP 500: Internal Server Error")

(defresponse not-implemented 501 "HTTP 501: Not Implemented")

(defresponse bad-gateway 502 "HTTP 502: Bad Gateway")

(defresponse bad-gateway 503 "HTTP 503: Service Unavailable")

(defresponse bad-gateway 504 "HTTP 504: Gateway Timeout")
