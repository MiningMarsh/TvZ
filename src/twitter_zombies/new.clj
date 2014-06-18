(ns stream-fixer.core
  (:require [clojure.core.async :as async]
            [clojure.data.json :as json])
  (:import
   [java.io PipedWriter PipedReader]))


(defn create-stream []
  (let [output (PipedReader.)        ; PipedReader reads input
        input (PipedWriter. output)] ; written ro PipedWriter.
    {:input input :output output}))

(defn create-json-processor [input-chan]
  (let [{input :input output :output} (create-stream)                 ; Read input and output of stream from create-stream.
        output-chan (async/chan 100)                                  ; Create a channel of buffer size 100.
        process-chan (async/map< #(.write input (str %)) input-chan)] ; Take every value from the input channel, convert it to a string 
		                                                              ; before writing it to the output channel, this is the process channel.
    (async/go-loop [x 1]                                              ; Loop forever while not blocking.
      (recur (async/<! process-chan)))                                ; Consume process channel to force mapping.
    (async/go-loop []                                                 
      (let [val (json/read output :key-fn keyword)]
        (async/>! output-chan val)
        (recur)))
    output-chan))
