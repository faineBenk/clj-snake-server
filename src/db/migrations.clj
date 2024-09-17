(ns db.migrations
  (:require [migratus.core :as migratus]
            [migratus.migrations :as mgs]
            [migratus.utils :as utils]
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

(defn map-to-SQLMigration [item]
  )

(defn migrate-all []
  (let [all-migrations (map map-to-SQLMigration (mgs/find-migration-files
                                                 (java.io.File. "/home/shdvv/clojure-projects/snake-server/resources/migrations/") nil))]
    (when (seq all-migrations)
      (migratus/rollback migratus-config)
      (recur))))

(defn rollback-all []
  (let [applied-migrations (mgs/list-migrations migratus-config)]
    (when (seq applied-migrations)
      (migratus/rollback migratus-config)
      (recur))))
