(ns twitter-zombies.twitter.callback
  (:require [clojure.core.async :as async]
            [clojure.data.json :as json]
            [twitter-zombies.twitter.json :as jparse]))

(defn callback-factory
  "Generates a callback function that will parse incoming packets and
  push them to the function f"
  [f]
  (let [input-chan (async/chan)
                                        ; Create the input channel
        output-chan (jparse/create-json-processor input-chan)]
                                        ; Parsed json out
    (async/go-loop [tweet (async/<! output-chan)]
                                        ; Bind the tweet to a paramter
      (f tweet)
                                        ; parse it
      (recur (async/<! output-chan)))
                                        ; recurse on the next tweet
    (fn [_ tweet-packet & args]
                                        ; The function that acts as a handler
      (async/>!! input-chan tweet-packet))))
                                        ; push the packet to the input-stream
