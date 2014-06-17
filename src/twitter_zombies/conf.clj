(ns twitter-zombies.conf
  (:use (clojure-ini [core]) 
        (twitter-zombies [resources])))

(defn read-conf
  "Returns a nested map representing the configuration data of the TvZ bot."
  []
  (read-ini (resource-file "conf.ini") :keywordize? true))
