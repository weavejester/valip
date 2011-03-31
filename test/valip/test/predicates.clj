(ns valip.test.predicates
  (:use valip.predicates :reload)
  (:use clojure.test))

(deftest test-present?
  (is (not (present? nil)))
  (is (not (present? "")))
  (is (not (present? " ")))
  (is (present? "foo")))

(deftest test-matches
  (is ((matches #"...") "foo"))
  (is (not ((matches #"...") "foobar"))))

(deftest test-gt
  (is ((gt 10) "11"))
  (is (not ((gt 10) "9")))
  (is (not ((gt 10) "10"))))

(deftest test-gte
  (is ((gte 10) "11"))
  (is ((gte 10) "10"))
  (is (not ((gte 10) "9"))))

(deftest test-lt
  (is ((lt 10) "9"))
  (is (not ((lt 10) "11")))
  (is (not ((lt 10) "10"))))

(deftest test-lte
  (is ((lte 10) "9"))
  (is ((lte 10) "10"))
  (is (not ((lte 10) "11"))))

(deftest test-over
  (is (= over gt)))

(deftest test-under
  (is (= under lt)))

(deftest test-between
  (is ((between 1 10) "5"))
  (is ((between 1 10) "1"))
  (is ((between 1 10) "10"))
  (is (not ((between 1 10) "0")))
  (is (not ((between 1 10) "11"))))
