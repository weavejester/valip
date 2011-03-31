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

(deftest test-integer-string?
  (is (integer-string? "10"))
  (is (integer-string? "-9"))
  (is (integer-string? "0"))
  (is (integer-string? "  8  "))
  (is (integer-string? "10,000"))
  (is (not (integer-string? "foo")))
  (is (not (integer-string? "10x")))
  (is (not (integer-string? "1.1"))))

(deftest test-integer-string?
  (is (decimal-string? "10"))
  (is (decimal-string? "-9"))
  (is (decimal-string? "0"))
  (is (decimal-string? "  8  "))
  (is (decimal-string? "10,000"))
  (is (decimal-string? "1.1"))
  (is (decimal-string? "3.14159"))
  (is (not (decimal-string? "foo")))
  (is (not (decimal-string? "10x"))))

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
