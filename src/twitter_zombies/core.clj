(ns twitter-zombies.core
  (:use (twitter-zombies [config])))

(defn -main
  [& args]
  (println (get-consumer-secret))
  (println (get-consumer-secret)))
