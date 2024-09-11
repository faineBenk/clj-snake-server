(ns db.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defonce db-config
  (edn/read-string (slurp (io/resource "db-config.edn"))))
