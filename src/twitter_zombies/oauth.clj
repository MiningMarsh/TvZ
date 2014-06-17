(ns twitter-zombies.oauth
  (:use (twitter-zombies [conf])
    [twitter.oauth]
    [twitter.callbacks]
    [twitter.callbacks.handlers]
    [twitter.api.restful]))
(defn get-creds
  "Returns the OAuth creds for the account specified in the conf.ini"
  []
  (let [conf (read-conf)]
    (make-oauth-creds
	  (get-in conf [:Consumer :Key])
	  (get-in conf [:Consumer :Secret])
	  (get-in conf [:User :Token])
	  (get-in conf [:User :Secret]))))

(def get-creds (memoize get-creds))
