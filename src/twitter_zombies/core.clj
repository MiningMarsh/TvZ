(ns twitter-zombies.core
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


                                        ; supply a callback that only prints the text of the status
(def ^:dynamic
  *custom-streaming-callback*
  (AsyncStreamingCallback. (fn [_ x & a] (try
                                           (println (-> x
                                                        str
                                                        json/read-json
                                                        :text))
                                           (catch Exception e (println (str e "\n" x)))))
                           (comp println response-return-everything)
                           exception-print))

(statuses-filter :params {:track "swag"}
                 :oauth-creds (get-creds)
                 :callbacks *custom-streaming-callback*)
