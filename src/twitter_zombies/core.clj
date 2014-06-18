(ns twitter-zombies.core
  (:require [twitter-zombies.twitter :as twitter]
            [twitter-zombies.graph :as mc]
            [twitter-zombies.resources :as res]))

(defn -main
  "Right now we just exit."
  [& args]
  (System/exit 0))

(def random-text 
  (mc/add-text {} (slurp (res/resource-file "stallman.txt"))))

(defn random-tweet [g n]
  (let [twt (atom (mc/random-walk g))]
    (while (> (count @twt) n)
      (swap! twt (fn [x] (mc/random-walk g))))
    @twt))
