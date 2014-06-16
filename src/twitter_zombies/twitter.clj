(ns twitter-zombies.twitter
  (:use
    [twitter.oauth]
	[twitter.callbacks]
	[twitter.callbacks.handlers]
    [twitter.api.restful])
  (:import
    (twitter.callbacks.protocols SyncSingleCallback)))


