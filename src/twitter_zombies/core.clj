(ns twitter-zombies.core
  (:require [twitter-zombies.twitter :as twitter]
            [twitter-zombies.graph :as mc]
            [twitter-zombies.resources :as res]))

(defn -main
  [& args]
  (do (twitter/tweet "Twitter/tweet function call test.")
      (System/exit 0)))



(def random-text 
  (mc/add-text {} (slurp (res/resource-file "stallman.txt"))))
