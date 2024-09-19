(ns core
  (:gen-class)
  (:require [org.httpkit.server :as httpkit :refer [run-server]]
            [compojure.core :as compojure :refer [defroutes GET POST DELETE ANY context]]
            [db.core :as db]
            [cheshire.core :as json])
  (:import [java.io InputStreamReader BufferedReader]))

(defn parse-body-as-json [body-stream]
  (-> body-stream
      InputStreamReader.
      BufferedReader. ; for efficiency
      json/parse-stream))

;; sends players' score to db
(defn score-handler [req]
   (let [player-name (-> req :params :player-name)
         body (parse-body-as-json (:body req))
         score (get body "score")
         result (db/set-user-score player-name score)
         msg {:status result}]
         ;; id (db/get-user-id player-name)]
     (println "Full request:")
     (println req)
     (println "This is body:")
     (println body)
     (println "This is score:")
     (println score)
     (json/generate-string msg)))

(defn get-rating-table [req]
  (println req)
  (json/generate-string (db/select-sorted-players-by-score)))

(defroutes all-routes
  (POST "/set-score/:player-name" [:as req] (score-handler req))
  (GET "/rating-table" [] get-rating-table))

(defn -main []
  (println "Server started on localhost:8080")
  (run-server all-routes {:port 8080})
  (db/-main))

