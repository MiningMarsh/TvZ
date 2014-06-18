(ns twitter-zombies.twitter.stream
  (:import
   [java.io PipedWriter PipedReader]))

(defn create-stream []
  (let [output (PipedReader.)           ; PipedReader reads input
        input (PipedWriter. output)]    ; written ro PipedWriter.
    {:input input :output output}))
