(ns crawler.core
  (:gen-class)
  (:require [org.httpkit.client :as http]))

(defn print-level
  [level & args]
  (apply println (apply str (repeat (* (- level 1) 4) " ")) args))

(defn parse-urls
  [body]
  (map #(% 1) (re-seq #"href=[\'\"]?((http|https)\:\/\/[^\'\" >]+)" body))) ; "

(defn visit-url
  [url current-level max-level]
  (cond
    (<= current-level max-level)
    (let [response @(http/get url {:follow-redirects false :throw-exceptions false})
          status (:status response)
          body (:body response)]
      (cond
        (= status 200)
        (let [urls (parse-urls body)]
          (print-level current-level url (count urls) "links")
            (doall (pmap #(visit-url % (+ current-level 1) max-level) urls)))

        (contains? #{301 302 307} status)
        (let [redirect-url (:location (:headers response))]
          (print-level current-level url "redirect" redirect-url)
          (visit-url redirect-url (+ current-level 1) max-level))

        :else
        (print-level current-level url "bad")))))

(defn -main
  [url depth]
  (visit-url url 1 (Integer/parseInt depth))
  (shutdown-agents))
