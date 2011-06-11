(ns valip.predicates
  "Predicates useful for validating input strings, such as ones from an :html
  form."
  (:require [clojure.string :as string]
            [clj-time.format :as time-format])
  (:import
    [java.net URL MalformedURLException]
    java.util.Hashtable
    javax.naming.NamingException
    javax.naming.directory.InitialDirContext
    [org.apache.commons.validator.routines IntegerValidator
                                           DoubleValidator]))

(defn
  #^{:html {:required "required"}}
  present?
  "Returns false if x is nil or blank, true otherwise."
  [x]
  (not (string/blank? x)))

(defn matches
  "Creates a predicate that returns true if the supplied regular expression
  matches its argument."
  [re]
  (with-meta (fn [s] (boolean (re-matches re s))) {:html {:pattern re}}))

(defn max-length
  "Creates a predicate that returns true if a string's length is less than or
  equal to the supplied maximum."
  [max]
  (with-meta (fn [s] (<= (count s) max)) {:html {:maxlength max}}))

(defn min-length
  "Creates a predicate that returns true if a string's length is greater than
  or equal to the supplied minimum."
  [min]
  (with-meta (fn [s] (>= (count s) min)) {:html {:pattern (str ".{" min ",}")}}))

(defn
  #^{:html {:type "email"}}
  email-address?
  "Returns true if the email address is valid, based on RFC 2822. Email
  addresses containing quotation marks or square brackets are considered
  invalid, as this syntax is not commonly supported in practise. The domain of
  the email address is not checked for validity."
  [email]
  {:pre [(present? email)]}
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
  {:pre [(email-address? email)]}
  (if-let [domain (second (re-matches #".*@(.*)" email))]
    (boolean (dns-lookup domain "MX"))))

(defn
  #^{:html {:type "url"}}
  url?
  "Returns true if the string is a valid URL."
  [s]
  {:pre [(present? s)]}
  (try
    (URL. s) true
    (catch MalformedURLException _ false)))

(defn
  #^{:html {:pattern "\\d+"}}
  digits?
  "Returns true if a string consists only of numerical digits."
  [s]
  {:pre [(present? s)]}
  (boolean (re-matches #"\d+" s)))

(defn
  #^{:html {:pattern "[A-Za-z0-9]+"}}
  alphanumeric?
  "Returns true if a string consists only of alphanumeric characters."
  [s]
  {:pre [(present? s)]}
  (boolean (re-matches #"[A-Za-z0-9]+")))

(defn integer-string?
  "Returns true if the string represents an integer."
  [s]
  {:pre [(present? s)]}
  (boolean (.validate (IntegerValidator.) s)))

(defn decimal-string?
  "Returns true if the string represents a decimal number."
  [s]
  {:pre [(present? s)]}
  (boolean (.validate (DoubleValidator.) s)))

(defn- parse-number [x]
  {:pre [(present? x)]}
  (if (string? x)
    (.validate (DoubleValidator.) x)
    x))

(defn gt
  "Creates a predicate function for checking if a value is numerically greater
  than the specified number."
  [n]
  (with-meta (fn [x] (> (parse-number x) n)) {:html {:min (inc n) :type "number"}}))

(defn lt
  "Creates a predicate function for checking if a value is numerically less
  than the specified number."
  [n]
  (with-meta (fn [x] (< (parse-number x) n)) {:html {:max (dec n) :type "number"}}))

(defn gte
  "Creates a predicate function for checking if a value is numerically greater
  than or equal to the specified number."
  [n]
  (with-meta (fn [x] (>= (parse-number x) n)) {:html {:min n :type "number"}}))

(defn lte
  "Creates a predicate function for checking if a value is numerically less
  than or equal to the specified number."
  [n]
  (with-meta (fn [x] (<= (parse-number x) n)) {:html {:max n :type "number"}}))

(def ^{:doc "Alias for gt"} over gt)

(def ^{:doc "Alias for lt"} under lt)

(def ^{:doc "Alias for gte"} at-least gte)

(def ^{:doc "Alias for lte"} at-most lte)

(defn between
  "Creates a predicate function for checking whether a number is between two
  values (inclusive)."
  [min max]
  (with-meta (fn [x]
    (let [x (parse-number x)]
      (and (>= x min) (<= x max)))) {:html {:min min :max max :type "number"}}))

(defn- parse-date-time [format input]
  {:pre [(present? input)]}
  (let [formatter (time-format/formatter format)]
    (try
      (time-format/parse formatter input)
      (catch IllegalArgumentException _ nil))))

(defn date-format
  "Creates a function for parsing a date using the supplied format string."
  [format]
  (partial parse-date-time format))

(defn
  #^{:html {:type "date"}}
  html5-date?
  "Returns true if the string is one that could be returned by an HTML5 date
  input element."
  [s]
  (boolean (parse-date-time "yyyy-MM-dd" s)))
