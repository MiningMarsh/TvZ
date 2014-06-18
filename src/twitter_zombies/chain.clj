(ns twitter-zombies.chain
  (:require [twitter-zombies.twitter :as twitter]
            [twitter-zombies.graph :as mc]
            [twitter-zombies.resources :as res]))

(def random-text
  (mc/add-text {} (slurp (res/resource-file "stallman.txt"))))

(defn random-tweet [g n]
  (let [twt (atom (mc/random-walk g))]
    (while (or (= (count @twt) 0) (> (count @twt) n))
      (swap! twt (fn [x] (mc/random-walk g))))
    @twt))
