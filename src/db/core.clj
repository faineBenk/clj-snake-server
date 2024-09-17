(ns db.core
  (:require [next.jdbc :as jdbc]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [db.config :as config]
            [db.migrations :refer [migrate rollback rollback-all]]))

(def datasource (atom nil))

(defn connect []
  (reset! datasource (jdbc/get-datasource config/db-config))
  (println "Database connected."))

(defn disconnect []
  (when @datasource
    (reset! datasource nil)
    (println "Database disconnected.")))

(defn get-user-id [player]
   (jdbc/execute! @datasource ["SELECT id FROM players WHERE player=" player]))

(defn get-last-id []
 (jdbc/execute! @datasource ["SELECT MAX(id) FROM players"]))

(defn set-user-score [player score]
  (let [id (inc (:max (first (get-last-id))))]
    (try
      (jdbc/execute! @datasource ["INSERT INTO players (player,score) VALUES (?,?)" player score])
      (println (str player " added to snake-db."))
      (catch Exception e
        (println (str player " already exists."))))))

(defn sort-players-by-score []
  (jdbc/execute! @datasource ["SELECT * FROM players ORDER BY score [DESC]"]))

(defn -main []
  ;(rollback-all)
  (migrate)
  (migrate)
  (connect)
  (println "Database started."))

