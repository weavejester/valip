(ns valip.predicates
  "Predicates useful for validating input strings, such as ones from a HTML
  form."
  (:require [clojure.string :as string])
  (:import
    [org.apache.commons.validator.routines IntegerValidator
                                           DoubleValidator]))

(defn present?
  "Returns false if x is nil or blank, true otherwise."
  [x]
  (not (string/blank? x)))

(defn matches
  "Returns a predicate that returns true if the supplied regular expression
  matches its argument."
  [re]
  (fn [s] (boolean (re-matches re s))))

(defn integer-string?
  "Returns true if the string represents an integer."
  [s]
  (boolean (. (IntegerValidator.) (validate s))))

(defn decimal-string?
  "Returns true if the string represents a decimal number."
  [s]
  (boolean (. (DoubleValidator.) (validate s))))

(defn- parse-number [x]
  (if (string? x)
    (. (DoubleValidator.) (validate x))
    x))

(defn gt
  "Creates a predicate function for checking if a value is numerically greater
  than the specified number."
  [n]
  (fn [x] (> (parse-number x) n)))

(defn lt
  "Creates a predicate function for checking if a value is numerically less
  than the specified number."
  [n]
  (fn [x] (< (parse-number x) n)))

(defn gte
  "Creates a predicate function for checking if a value is numerically greater
  than or equal to the specified number."
  [n]
  (fn [x] (>= (parse-number x) n)))

(defn lte
  "Creates a predicate function for checking if a value is numerically less
  than or equal to the specified number."
  [n]
  (fn [x] (<= (parse-number x) n)))

(def ^{:doc "Alias for gt"} over gt)

(def ^{:doc "Alias for lt"} under lt)

(defn between
  "Creates a predicate function for checking whether a number is between two
  values (inclusive)."
  [min max]
  (fn [x]
    (let [x (parse-number x)]
      (and (>= x min) (<= x max)))))
