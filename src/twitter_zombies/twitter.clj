(ns twitter-zombies.twitter
  (:use [twitter-zombies.oauth]
        [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
		[twitter-zombies.twitter.callback]
        [twitter.api.restful]
        [twitter.api.streaming])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))

(defn tweet
  "Tweets a message."
  [^String message]
  (let [message (if (>= (count message) 140)
                (subs message 0 139)
		        message)]
    (statuses-update :oauth-creds (get-creds) :params {:status message})))

(defn reduce-tweets [f initial track]
  "Takes a function of signature (fn [acc x] ...), and reduces that function 
   using incoming tweets. The function must return 2 values. If the second is 
   true, then the first is passed into the next cycle. Otherwise, the first is 
   returned."
  (statuses-filter :params {:track track}
                   :oauth-creds (get-creds)
                   :callbacks 
                     (AsyncStreamingCallback. 
                       (callback-factory
                         (fn [tweet]
                           (let [[ret keep-going] (f initial tweet)]
                             (if keep-going
                               (def initial ret)
                               ret)))) ; TODO: This part needs to exit out of 
                                       ; reduce-tweets, returning the value of 'ret'.
                       response-return-everything
                       exception-print)))
