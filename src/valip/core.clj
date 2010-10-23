(ns valip.core
  "Add validations to records.")

(defprotocol Validatable
  (validate [record]
    "Returns a map of the record's keys mapped to a list of any validation
    errors against them."))

(defn valid?
  "True if the record has no validation errors, false otherwise."
  [record]
  (nil? (validate record)))

(defmacro validations
  "Add validations to an existing type. The type will be extended with the
  Validatable protocol."
  [type & validations]
  (let [record (gensym "record")]
    `(extend-type ~type
       Validatable
       (~'validate [~record]
         (merge-with concat
           ~@(for [[k & vs] validations, v vs]
               `(if-not (-> (~k ~record) ~v)
                  {~k '(~v)})))))))
