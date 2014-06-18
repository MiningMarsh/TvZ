(ns twitter-zombies.core
  (:require [twitter-zombies.twitter :as twitter]
            [twitter-zombies.graph :as mc]
            [twitter-zombies.resources :as res])
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
		[twitter-zombies.oauth])
  (:require [cheshire.core :as json]
            [http.async.client :as ac]
            [clojure.core.async :as async :refer [<! <!! >! >!!]]
            [twitter.api.streaming :as stream]
            [twitter.callbacks.protocols :as callbacks]
            [twitter.callbacks.handlers :as handlers]
            [clojure.java.io :as io])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))


(def random-text
  (mc/add-text {} (slurp (res/resource-file "stallman.txt"))))

(defn random-tweet [g n]
  (let [twt (atom (mc/random-walk g))]
    (while (or (= (count @twt) 0) (> (count @twt) n))
      (swap! twt (fn [x] (mc/random-walk g))))
    @twt))


(defn process-tweet [tweet]
  tweet)

(defmacro dbg
  [x]
  `(let [x# ~x]
     (printf "dbg %s:%s> %s is %s\n"
               ~*ns*
               ~(:line (meta &form))
               ~(pr-str x)
               (pprint-str x#))
     (flush)
     x#))

(defmacro try-pst
  [& body]
  `(try ~@body
        (catch Exception e#
          (st/print-stack-trace e#)
          (throw e#))))

(defn init-chans
  [filt]
  (let [incoming (async/chan)
        ;; Drop oldest tweets
        raw-tweets (async/chan (async/sliding-buffer 1024))
        tweets-ch (->> raw-tweets
                       (async/filter< :text)
                       (async/map< #'process-tweet))
        make-user-stream (fn [] (stream/user-stream
                                 :params {:track filt}
                                 :oauth-creds (get-creds)
                                 :callbacks (callbacks/map->AsyncStreamingCallback
                                             {:on-bodypart (fn [_ chunk]
                                                             (async/put! incoming chunk))
                                              :on-failure (comp println handlers/response-return-everything)
                                              :on-exception #(println %2)})))
        chunk-stream (java.io.PipedOutputStream.)
        json-reader (io/reader (java.io.PipedInputStream. chunk-stream))
        json-process (async/thread
                       (loop [s (json/parsed-seq json-reader true)]
                         (when (seq s)
                           (if (async/>!! raw-tweets (first s))
                             (recur (rest s))
                             ;; else stop the works
                             ))))
        process (async/go-loop [working-stream (make-user-stream)]
                  (let [t (async/timeout 90000)
                        [val port] (async/alts! [incoming t])]
                    (cond
                     ;; it timed out
                     (= port t) (do ((:cancel (meta working-stream)))
                                    (recur (make-user-stream)))
                     (nil? val) :closed
                     :else (let [data (.toByteArray val)]
                             (.write chunk-stream data 0 (count data))
                             (recur working-stream)))))]
    {:tweets tweets-ch
     :process process}))

(defn -main
  "Right now we just exit."
  [& args]
  (let [yolo-chan (:tweets (init-chans "yolo"))]
    (while true
      (println (:text (<!! yolo-chan))))))


