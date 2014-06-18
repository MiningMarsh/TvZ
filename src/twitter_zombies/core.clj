(ns twitter-zombies.core
  (:require [twitter-zombies.twitter :as twitter]
            [twitter-zombies.graph :as mc]
            [twitter-zombies.resources :as res]))

(defn -main
  [& args]
  (do (twitter/tweet "Twitter/tweet function call test. 11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111")
      (System/exit 0)))

(def random-text 
  (mc/add-text {} (slurp (res/resource-file "stallman.txt"))))

(defn random-tweet [g]
  (let [twt (atom (mc/random-walk g))]
    (while (> (count @twt) 140)
      (swap! twt (fn [x] (mc/random-walk g))))
    @twt))
