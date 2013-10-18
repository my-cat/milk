(ns milk.core
	   (:require   [monger.core :as mg]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
(defn init! []
  (println "aaaaaaaaaaaaa")
    (println "aaaaaaaaaaaaa")
      (println "aaaaaaaaaaaaa")
        (println "aaaaaaaaaaaaa")

(mc/ensure-index "pastes" {:user 1 :date 1})
(mc/ensure-index "pastes" {:private 1})
(mc/ensure-index "pastes" {:id 1})
(mc/ensure-index "pastes" {:paste-id 1})
(mc/ensure-index "pastes" {:fork 1}))
