(ns valip.predicates
  "Predicates useful for validating input strings, such as ones from a HTML
  form."
  (:require [clojure.string :as string])
  (:import
    java.util.Hashtable
    javax.naming.NamingException
    javax.naming.directory.InitialDirContext
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

(defn email-address?
  "Returns true if the email address is valid, based on RFC 2822. Email
  addresses containing quotation marks or square brackets are considered
  invalid, as this syntax is not commonly supported in practise. The domain of
  the email address is not checked for validity."
  [email]
  (let [re (str "(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+"
                "(?:\\.[a-z0-9!#$%&'*+/=?" "^_`{|}~-]+)*"
                "@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+"
                "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")]
    (boolean (re-matches (re-pattern re) email))))

(defn- dns-lookup [^String hostname ^String type]
  (let [params {"java.naming.factory.initial"
                "com.sun.jndi.dns.DnsContextFactory"}]
    (try
      (.. (InitialDirContext. (Hashtable. params))
          (getAttributes hostname (into-array [type]))
          (get type))
      (catch NamingException _
        nil))))

(defn valid-email-domain?
  "Returns true if the domain of the supplied email address has a MX DNS entry."
  [email]
  (let [domain (second (re-matches #".*@(.*)" email))]
    (boolean (dns-lookup domain "MX"))))

(defn integer-string?
  "Returns true if the string represents an integer."
  [s]
  (boolean (.validate (IntegerValidator.) s)))

(defn decimal-string?
  "Returns true if the string represents a decimal number."
  [s]
  (boolean (.validate (DoubleValidator.) s)))

(defn- parse-number [x]
  (if (string? x)
    (.validate (DoubleValidator.) x)
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
