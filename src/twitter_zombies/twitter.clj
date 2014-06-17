(ns twitter-zombies.twitter
  (:use [twitter-zombies.oauth]
            [twitter.oauth]
            [twitter.callbacks]
            [twitter.callbacks.handlers]
            [twitter.api.restful]))

(defn tweet
  "Tweets a message."
  [^String message]
  (statuses-update :oauth-creds (get-creds) :params {:status message}))
