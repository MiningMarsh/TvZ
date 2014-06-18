(ns twitter-zombies.core
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
        [twitter-zombies.oauth]
        [twitter-zombies.twitter.callback])
  (:require [twitter-zombies.twitter :as twitter])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))

(defn -main
  "Print all incoming tweets from search."
  [& args]
  (twitter/reduce-tweets
    (fn [acc x]
      (print (:screen_name (:user x)))
      (print ": ")
      (println (:text x))
      (println "--------------------------------------------")
      [true true])
    true
    (first args)))
