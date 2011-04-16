(ns valip.core
  "Functional validations.")

(defn validation-on
  "Performs a validation on a key in a map using the supplied predicate
  function. A {key [error]} map is returned if the predicate returns false;
  nil is returned if the predicate returns true, or if the supplied value does
  not match the predicates preconditions (i.e. throws an AssertionError)."
  [key pred? error]
  (fn [value-map]
    (let [value (value-map key)]
      (try
        (if-not (pred? value)
          {key [error]})
        (catch AssertionError _)))))

(defn merge-errors
  "Merge error maps returned by from the validation-on function."
  [& error-maps]
  (apply merge-with into error-maps))

(defn validate
  "Validate a map of values using the supplied validations. Each validation
  is represented as a vector containing [key predicate? error] values. A map
  is returned for all the keys that failed their predicates, in the form:
  {key [errors]}. If no predicates return false, nil is returned."
  [value-map & validations]
  (->> validations
       (map (partial apply validation-on))
       (map (fn [f] (f value-map)))
       (apply merge-errors)))
