(ns db.migrations
  (:require [migratus.core :as migratus]
            [db.config :as config]))

(defn migratus-config
  [{:keys [dbtype dbname host port user password]}]
  {:store :database
   :migration-dir "migrations/"
   :db {:connection-uri (str "jdbc:" dbtype "://" host "/" dbname "?user=" user "&password=" password)}})

;; Run pending migrations
(defn migrate []
  (migratus/migrate (migratus-config config/db-config)))

;; Rollback the last migration
(defn rollback []
  (migratus/rollback (migratus-config config/db-config)))
