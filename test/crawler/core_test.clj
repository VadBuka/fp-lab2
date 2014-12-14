(ns crawler.core-test
  (:require [clojure.test :refer :all]
            [crawler.core :refer :all]))

(deftest test-parse-urls
  (let [body "<body><a href=\"https://google.com/page1.html\">link</a>
    <h1>Header</h1><a href=\"http://ya.ru/page1.html\">link</a>
    <br><a href=\"/about.html\">link</a>"]

    (is (=
      (parse-urls body)
      '("https://google.com/page1.html" "http://ya.ru/page1.html")))))

(deftest test-visit-url
  (testing "404"
    (with-redefs [get-response (fn [_] {:status 404 :body ""})]
      (is (=
        (visit-url "url" 1 1 (atom #{}))
        404)))))
