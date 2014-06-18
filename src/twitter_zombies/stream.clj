(ns zombie-twitter.stream
  (:require [clojure.core.async :as async]
            [clojure.data.json :as json])
  (:import
   [java.io PipedWriter PipedReader]))


(defn create-stream []
  (let [output (PipedReader.)        ; PipedReader reads input
        input (PipedWriter. output)] ; written ro PipedWriter.
    {:input input :output output}))

(defn create-json-processor [input-chan]
  (let [{input :input output :output} (create-stream) ;Bind input and output to the stream endpoints
        output-chan (async/chan 100)]  ;Create the output channel with a buffer of 100 json objects
    (async/go (loop [val (str (async/<! input-chan))] ;Loop over each incoming and push to buffer
                (.write input val) ; write to input stream
                (recur (str (async/<! input-chan))))) ;recur on the next packet
    (async/go-loop []                                                 
      (let [val (json/read output :key-fn keyword)] ;Parse the full json packet 
        (async/>! output-chan val) ;push it to the output channel
        (recur))) ;recurse
    output-chan)) ;return the output channel
