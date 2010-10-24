(ns valip.test.core
  (:use valip.core :reload)
  (:use clojure.test))

(defrecord Natural [n])

(validate-type Natural
  (:n (> 0)))

(deftest test-valid?
  (is (not (valid? (Natural. 0))))
  (is (valid? (Natural. 10))))

(deftest test-validation-errors
  (is (= (validation-errors (Natural. 0))
         {:n '((> 0))})))
