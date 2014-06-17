(ns twitter-zombies.core
  (:use (twitter-zombies [oauth])))

(defn -main
  [& args]
  (println (get-creds)))
