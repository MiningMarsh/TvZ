(ns twitter-zombies.resources
  (:require [clojure.java.io :as io]))

(defn resource-file [^String path]
  "Returns the full path of a resource."
  (->
    path
    io/resource
	io/file))
