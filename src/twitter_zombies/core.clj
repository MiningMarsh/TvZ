(ns twitter-zombies.core
  (:use [twitter-zombies.oauth :as zombie-auth]))

(defn -main
  [& args]
  (println (zombie-auth/get-creds)))
