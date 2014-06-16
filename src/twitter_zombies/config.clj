(ns twitter-zombies.config
  (:use (twitter-zombies [resources])))

(defn get-oauth-key
  "Gets the specified key credential used for oauth login."
  [^String key]
  (let [secret (slurp-resource key)]
    (subs secret 0 (- (count secret) 1))))

(def get-oauth-key (memoize get-oauth-key))

(defn get-consumer-secret 
  "Returns the OAUTH twitter secret defined in resources/consumer-secret"
  []
  (get-oauth-key "consumer-secret"))

(defn get-consumer-key
  "Returns the OAUTH twitter secret defined in resources/consumer-key"
  []
  (get-oauth-key "consumer-key"))
