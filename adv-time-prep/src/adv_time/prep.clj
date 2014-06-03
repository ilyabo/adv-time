(ns adv-time.prep
  (:require dt)
  (:require [clj-http.client :as client]))

(def questions-url
  "https://docs.google.com/spreadsheets/d/1ujunUo814pBMMCNxo4NTLrWM3Ph59R_KZI3wW4Ok7do/export?format=csv")



(dt/read-csv-input (:body (client/get  questions-url {:insecure? true})))


(defn -main [& args]
  (println questions-url))