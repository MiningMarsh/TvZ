(ns zombie-twitter.stream
  (:require [clojure.core.async :as async]
            [clojure.data.json :as json])
  (:import
   [java.io PipedWriter PipedReader]))


(defn create-stream []
  (let [output (PipedReader.)        ; PipedReader reads input
        input (PipedWriter. output)] ; written ro PipedWriter.
    {:input input :output output}))

(defn create-json-processor 
  "Takes an input channel of strings and parses the json incrementally pushing to output-chan"
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
