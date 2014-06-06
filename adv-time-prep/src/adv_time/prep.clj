(ns adv-time.prep
  (:require dt)
  (:require [clj-http.client :as client]))

(def questions-url
  ;complete list
  "https://docs.google.com/spreadsheets/d/1ujunUo814pBMMCNxo4NTLrWM3Ph59R_KZI3wW4Ok7do/export?format=csv")

  ;test
  ;"https://docs.google.com/spreadsheets/d/1ujunUo814pBMMCNxo4NTLrWM3Ph59R_KZI3wW4Ok7do/export?format=csv&gid=662795327")

(def waypoints-outfile "../adv-time-client/waypoints.spyglass")
(def questions-outfile "../adv-time-client/src/questions.cljs")


(defn prepare-row [row]
  {
    :id              (:id row)
    :text            (:text row)
    :valid-answer    (:valid-answer row)
    :need-lock       (read-string (:need-lock row))
    :non-answerable  (read-string (:non-answerable row))
  })


(defn save-questions [data outfile]
  ;(dt/save-to-csv data "../adv-time-client/questions.csv"
  ;                :columns [:id :text :valid-answer])

  (println "Writing" (count data) "questions to" outfile)
  (spit outfile
        (str
          '(ns advtime.questions)
          "(def questions ["
            (apply str
                   (for [s (map prepare-row data)] (str s "\n")))
          "])")))





(defn save-waypoints [data outfile]
  (println "Writing" (count data) "waypoints to" outfile)
  (spit outfile
    (apply str
      (for [row data
            :when (not= (:need-lock row) "true")]
        (str
          "location: " (:id row) " "
          (:latitude row) " "
          (:longitude row) " "
          "(0.000000) "
          "spyglass://location?n=" (:id row)
          "&lat=" (:latitude row)
          "&lon=" (:longitude row)
          "&alt=0.000000&data=track"
          "\n"
    )))))





(defn -main [& args]
  (println "Loading questions from " questions-url)
  (let [data (dt/read-csv-input (:body (client/get  questions-url {:insecure? true})))]
    (save-questions data questions-outfile)
    (save-waypoints data waypoints-outfile))
  )