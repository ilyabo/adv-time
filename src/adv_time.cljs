
(ns advtime
  (:require [reagent.core :as reagent :refer [atom]]))


(def app-state (reagent/atom
  {
   :questions
   [
    {
     :id "1"
     :text "Where was the old blue tram with the full number 1530 once heading?"
     :valid-answer #"(?i)Bellevue"
     }
    {
     :id "2"
     :text "There is a bad replica of the logo of a well known company.
            In this replica one letter is used with its own reflection. Which letter is it?"
     :valid-answer #"(?i)A"
     }
    ]

   :answered {}
   :attempts {}

   :selection nil

}))



(defn submit-answer [q a]
  (let [correct  (re-matches (:valid-answer q) a)]
    (.log js/console correct)
    (swap! app-state update-in [:attempts (:id q)] inc)
    (when correct
      (swap! app-state assoc-in [:answered (:id q)] a)
      ;(swap! app-state dissoc :selection)
      )))


(defn question-view [q]
  (let [answer        (reagent/atom "")]
    (fn []
      (let [answered      (get-in @app-state [:answered (:id q)])
            attempts      (get-in @app-state [:attempts (:id q)])]
        [:div
          [:button {:on-click #(swap! app-state dissoc :selection)} "<< back"]
          [:div {:className "question"} (:text q)]

          (when-not answered
            [:div
              [:input {:name "answer" :className "answer"
                       :value @answer
                       :on-change #(reset! answer (-> % .-target .-value .trim))} ]

              [:button {:on-click
                         #(if (> (.-length @answer) 0) (submit-answer q @answer))}
                        "Submit"]

              (if attempts
                [:div {:className "attempts"} (str "Attempts: " attempts)])])

          (when  answered
            [:div
              [:div {:className "answered"} (str "Answered: " answered)]
              [:div {:className "correct"} "The answer is correct!"]
             ]
          )]))))



(defn question-button [q]
  [:div {:className
          (str "question-button"
               (if (get-in @app-state [:answered (:id q)]) " is-answered" else ""))
         :on-click #(swap! app-state assoc :selection q) }
    (:id q)])

(defn question-buttons-list [questions]
  [:div (for [q questions] ^{:key (:id q)} [question-button q])])




(defn adv-time []
  [:div
   (if (:selection @app-state)
    [question-view (:selection @app-state)]
    [question-buttons-list (:questions @app-state)])])






(defn ^:export run []
  (reagent/render-component [adv-time]
                              (.-body js/document)))


