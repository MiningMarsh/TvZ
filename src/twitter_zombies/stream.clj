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
  ; Bind input and output to the stream endpoints
  (let [{input :input output :output} (create-stream) 
		; Create the output channel with a buffer of 100 json objects
        output-chan (async/chan 100)]  
	; Loop over each incoming and push to buffer
    (async/go (loop [val (str (async/<! input-chan))] 
				; write to input stream
                (.write input val) 
				; recur on the next packet
                (recur (str (async/<! input-chan))))) 
    (async/go-loop []                                                 
	  ; Parse the full json packet 
      (let [val (json/read output :key-fn keyword)] 
		; push it to the output channel
        (async/>! output-chan val) 
		; recurse
        (recur))) 
	; return the output channel
    output-chan)) 
