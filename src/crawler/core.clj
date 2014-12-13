(ns crawler.core
  (:gen-class)
  (:require [org.httpkit.client :as http]))

(defn print-level
  [level & args]
  (apply println (apply str (repeat (* (- level 1) 4) " ")) args))

(defn visit-url
  [url current-level max-level]
  (cond
    (<= current-level max-level)
    (let [response @(http/get url {:follow-redirects false :throw-exceptions false})
          status (:status response)
          body (:body response)]
      (cond
        (= status 200)
        (print-level current-level url "ok")

        (contains? #{301 302 307} status)
        (let [redirect-url (:location (:headers response))]
          (print-level current-level url "redirect" redirect-url)
          (visit-url redirect-url (+ current-level 1) max-level))

        :else
        (print-level current-level url "bad")))))

(defn -main
  [url depth]
  (visit-url url 1 (Integer/parseInt depth)))
