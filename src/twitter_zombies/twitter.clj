(ns twitter-zombies.twitter
  (:use [twitter-zombies.oauth]
            [twitter.oauth]
            [twitter.callbacks]
            [twitter.callbacks.handlers]
            [twitter.api.restful]))

(defn send
  "Tweets a message."
  [^String message]
  (let [message (if (>= (count message) 140)
                (subs message 0 139)
		        message)]
    (statuses-update :oauth-creds (get-creds) :params {:status message})))
