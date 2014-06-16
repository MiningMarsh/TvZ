(ns twitter-zombies.resources
  (:require [clojure.java.io :as io]))

(defn slurp-resource [resource-file]
  "Returns the contents of a resource file as a string."
  (-> 
    resource-file
    io/resource
    io/file 
    slurp))

