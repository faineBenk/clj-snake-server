(ns db.core
  (:require [next.jdbc :as jdbc]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [db.config :as config]
            [db.migrations :refer [migrate rollback migrate-all rollback-all]]
            [cheshire.core :as json]))

(def datasource (atom nil))

(defn connect []
  (reset! datasource (jdbc/get-datasource config/db-config))
  (println "Database connected."))

(defn disconnect []
  (when @datasource
    (reset! datasource nil)
    (println "Database disconnected.")))

(defn get-user-score [player]
   (jdbc/execute! @datasource ["SELECT score FROM players WHERE player = ?" player]))

(defn get-last-id []
 (jdbc/execute! @datasource ["SELECT MAX(id) FROM players"]))

(defn set-user-score [player new-score]
    (try
      (jdbc/execute! @datasource ["INSERT INTO players (player,score) VALUES (?,?)" player new-score])
      (println (str player " added to snake-db."))
      :added
      (catch Exception e
        (do
          (println (str player " already exists."))
          (let [old-score (:players/score (first (get-user-score player)))]
          ;; update score of existing player only if new-score > latest score
          (if (> new-score  old-score)
            (do
              (jdbc/execute! @datasource [ "UPDATE players SET score = ? WHERE player = ?" new-score player])
              :updated)
            :not-updated))))))


(defn select-sorted-players-by-score []
  (jdbc/execute! @datasource ["SELECT * FROM players ORDER BY score DESC LIMIT 5"]))

(defn -main []
  ;(rollback-all)
  ;(migrate-all)
  (connect)
  (println "Database started."))

