(ns twitter-zombies.core
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
		[twitter-zombies.oauth]
		[twitter-zombies.twitter.callback])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))

(def ^:dynamic
     *custom-streaming-callback*
     (AsyncStreamingCallback. (callback-factory (comp println :text))
                      (comp println response-return-everything)
                  exception-print))

(statuses-filter :params {:track "yolo"}
         :oauth-creds my-creds
         :callbacks *custom-streaming-callback*)

(defn -main
  "Print all incoming tweets from search."
  [& args]
  (println "Done"))
