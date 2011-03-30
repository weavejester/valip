(ns valip.predicates
  "Predicates useful for validating input strings, such as ones from a HTML
  form."
  (:import java.text.NumberFormat)
  (:require [clojure.string :as string]))

(defn- parse-number [x]
  (if (string? x)
    (. (NumberFormat/getInstance) (parse x))
    x))

(defn present?
  "Returns false if x is nil or blank, true otherwise."
  [x]
  (not (string/blank? x)))

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

(defn between
  "Creates a predicate function for checking whether a number is between two
  values (inclusive)."
  [min max]
  (fn [x]
    (let [x (parse-number x)]
      (and (>= x min) (<= x max)))))
