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
  (AsyncStreamingCallback. (callback-factory (comp println 
                                                   #(str (:name (get % 0)) ": " (get % 1))
                                                   (juxt :user :text)))
                           (comp println response-return-everything)
                           exception-print))



(defn -main
  "Print all incoming tweets from search."
  [& args]
  (statuses-filter :params {:track (first args)}
                   :oauth-creds (get-creds)
                   :callbacks *custom-streaming-callback*))
