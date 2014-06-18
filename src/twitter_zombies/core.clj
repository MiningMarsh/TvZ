(ns twitter-zombies.core
  (:require [twitter-zombies.twitter :as twitter]
            [twitter-zombies.graph :as mc]
            [twitter-zombies.resources :as res])
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
		[twitter-zombies.oauth])
  (:require [clojure.data.json :as json]
            [http.async.client :as ac])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))

(defn -main
  "Right now we just exit."
  [& args]
  (println "Done"))

(def random-text
  (mc/add-text {} (slurp (res/resource-file "stallman.txt"))))

(defn random-tweet [g n]
  (let [twt (atom (mc/random-walk g))]
    (while (> (count @twt) n)
      (swap! twt (fn [x] (mc/random-walk g))))
    @twt))

; supply a callback that only prints the text of the status
(def ^:dynamic
     *custom-streaming-callback*
     (AsyncStreamingCallback. (comp println #(:text %) json/read-json #(str %2))
                      (comp println response-return-everything)
                  exception-print))

(statuses-filter :params {:track "Borat"}
         :oauth-creds (get-creds)
         :callbacks *custom-streaming-callback*)
