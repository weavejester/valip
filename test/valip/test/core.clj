(ns valip.test.core
  (:use valip.core :reload)
  (:use clojure.test))

(deftest validation-on-test
  (let [p? (fn [x] {:pre [(> x 0)]} false)
        v  (validation-on :x p? "error")]
    (is (= (v {:x 1}) {:x ["error"]}))
    (is (nil? (v {:x 0})))))

(deftest validate-test
  (is (= (validate {:x 17}
           [:x (complement nil?) "must be present"]
           [:y (complement nil?) "must be present"]
           [:x #(> % 18) "must be greater than 18"])
         {:x ["must be greater than 18"]
          :y ["must be present"]})))
