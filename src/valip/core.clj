(ns valip.core
  "Add validations to records.")

(defprotocol Validatable
  (validation-errors [record]
    "Returns a map of the record's keys mapped to a list of any validation
    errors against them."))

(defn valid?
  "True if the record has no validation errors, false otherwise."
  [record]
  (empty? (validation-errors record)))

(defmacro validate-type
  "Add validations to an existing type. The type will be extended with the
  Validatable protocol."
  [type & validations]
  (let [record (gensym "record")]
    `(extend-type ~type
       Validatable
       (~'validation-errors [~record]
         (merge-with concat
           ~@(for [[k & vs] validations, v vs]
               `(if-not (-> (~k ~record) ~v)
                  {~k '(~v)})))))))
