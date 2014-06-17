(ns twitter-zombies.core
  (:require [twitter-zombies.twitter :as twitter]))

(defn -main
  [& args]
  (do (twitter/tweet "Twitter/tweet function call test.")
  (System/exit 0)))
