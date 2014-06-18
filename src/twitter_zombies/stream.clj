(ns zombie-twitter.stream
  (:require [clojure.core.async :as async]
            [clojure.data.json :as json])
  (:import
   [java.io PipedWriter PipedReader]))


(defn create-stream []
  (let [output (PipedReader.)        
                                        ; PipedReader reads input
        input (PipedWriter. output)]
                                        ; written ro PipedWriter.
    {:input input :output output}))

(defn create-json-processor 
  "Takes an input channel of strings and parses the json incrementally
  pushing to output-chan" 
  [input-chan]
  (let [{input :input output :output} (create-stream) 
                                        ; Bind input and output to the stream endpoints
        output-chan (async/chan 100)]  
                                        ; Create the output channel with a buffer of 100 json objects
    (async/go-loop [val (str (async/<! input-chan))] 
                                        ; Loop over each incoming and push to buffer
      (.write input val) 
                                        ; write to input stream
      (recur (str (async/<! input-chan)))) 
    (async/go-loop []                                                 
                                        ; recur on the next packet
      (let [val (json/read output :key-fn keyword)] 
                                        ; push it to the output channel
        (async/>! output-chan val) 
                                        ; Parse the full json packet 
        (recur))) 
                                        ; recurse
    output-chan)) 
                                        ; return the output channel


(defn callback-factory
  "Generates a callback function that will parse incoming packets and
  push them to the function f"
  [f]
  (let [input-chan (async/chan)
                                        ; Create the input channel
        output-chan (create-json-processor input-chan)]
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
