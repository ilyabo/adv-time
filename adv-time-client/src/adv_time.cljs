
(ns advtime
  (:require [reagent.core :as reagent :refer [atom]]
            [alandipert.storage-atom :refer [local-storage]]))



(def questions [
   {
    :id "P0"
    :text "You should see a totem pole in which one particular animal is represented exactly 8 times.
           Which animal is it?"
    :valid-answer #"(?i)fish"
    }


   {
    :id "finish"
    :text "Here there will be the final directions."
    ;:text "Use the following link to import the finish waypoint: "
    ;:link "https://www.dropbox.com/meta_dl/eyJzdWJfcGF0aCI6ICIiLCAidGVzdF9saW5rIjogZmFsc2UsICJzZXJ2ZXIiOiAiZGwuZHJvcGJveHVzZXJjb250ZW50LmNvbSIsICJpdGVtX2lkIjogbnVsbCwgImlzX2RpciI6IGZhbHNlLCAidGtleSI6ICJ2MTR2MzNqOWpnM3hkamkifQ/AAK7xDZaAMi4tq4z_IJOWOdtO0OW-qtWzsMn3yTT7SIyGQ?dl=1"
    :need-lock      true
    :non-answerable true
    }
   ])


(def score-needed-to-unlock 0.5)

(def app-state
  (local-storage (reagent/atom {
     :score    0.0
     :answered {}
     :attempts {}
     :selected-question-id nil
  }) :app-state))


(defn is-locked [] (< (:score @app-state) score-needed-to-unlock))

(defn find-question [id]
  (first (filter #(= id (:id %)) questions)))

(defn answer-score [attempts]
  (/ 1.0 (Math/sqrt attempts)))

(defn submit-answer [qid a]
  (let [q        (find-question qid)
        correct  (re-matches (:valid-answer q) a)]

    (swap! app-state update-in [:attempts qid] inc)

    (when correct
      (swap! app-state assoc-in [:answered qid] a)
      (let [score (answer-score (get-in @app-state [:attempts qid]))]
            (swap! app-state update-in [:score] + score)))))


(defn question-view [qid]
  (let [answer        (reagent/atom "")]
    (fn []

      (let [q             (find-question qid)
            answered      (get-in @app-state [:answered qid])
            attempts      (get-in @app-state [:attempts qid])]

        [:div {:className "question-view"}
          [:button { :className "button back-button"
                     :on-click #(swap! app-state dissoc :selected-question-id)} "<< back"]
          [:div {:className "question"} (:text q)]
          (if (:non-answerable q)

            (if (:link q)
              [:a {:href (:link q)} "Link"])

            (if  answered
              [:div
                [:div {:className "answered"} (str "Answered: " answered)]
                [:div {:className "correct"} "The answer is correct!"]
               ]

               [:div
                  [:input {:name "answer" :className "answer-textfield"
                           :value @answer
                           :on-change #(reset! answer (-> % .-target .-value .trim))} ]

                  [:button {:className "button submit-button"
                            :on-click
                             #(if (> (.-length @answer) 0) (submit-answer qid @answer))}
                            "Submit"]

                  (if attempts
                    [:div {:className "attempts"} (str attempts " wrong attempt"
                                                       (if (> attempts 1) "s" ""))])]))]))))



(defn question-button [q]
  (let [disabled?  (and (:need-lock q) (is-locked))]
    [:button {:className
                (str "button question-button"
                     (if (get-in @app-state [:answered (:id q)]) " is-answered"  "")
                     (if (:need-lock q) " need-lock")
                     (if disabled? " is-disabled"))
              :disabled  disabled?
              :on-click #(swap! app-state assoc :selected-question-id (:id q)) }
     (if disabled?
       "LOCKED"
       (:id q)) ]))

(defn question-buttons-list [questions]
  [:div
    [:div {:className "score"} (str "Score: " (.toFixed (:score @app-state) 2))]
    [:div (for [q questions] ^{:key (:id q)} [question-button q])]
    (if (is-locked)
      [:div {:className "score-comment"}
       (str "You need at least " score-needed-to-unlock " points to unlock the locked items.")])
    ])




(defn adv-time []
  [:div {:className "content"}
   (if (:selected-question-id @app-state)
    [question-view (:selected-question-id @app-state)]
    [question-buttons-list questions])])






(defn ^:export run []
  (reagent/render-component [adv-time]
                              (.-body js/document)))

