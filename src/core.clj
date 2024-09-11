(ns core
  (:gen-class)
  (:require [org.httpkit.server :as httpkit :refer [run-server]]
            [compojure.core :as compojure :refer [defroutes GET POST DELETE ANY context]]
            [db.core :as db]
            [cheshire.core :as json])
  (:import [java.io InputStreamReader BufferedReader]))

;; stores players' scores, sorted by maximum score
(def rating-table)

(defn parse-body-as-json [body-stream]
  (-> body-stream
      InputStreamReader.
      BufferedReader. ; for efficiency
      json/parse-stream))

;; sends players' score to db
(defn score-handler [req]
   (let [player-name (-> req :params :player-name)
         body (parse-body-as-json (:body req))
         score (get body "score")]
         ;; id (db/get-user-id player-name)]
     (println "Full request:")
     (println req)
     (println "This is body:")
     (println body)
     (println "This is score:")
     (println score)
     (db/set-user-score player-name score)))

;; (db/set-user-score  "shdvv" 10)

(defn update-rating-table [rating-table]
  (db/sort-players-by-score))

(defroutes all-routes
  (POST "/set-score/:player-name" [:as req] (score-handler req)))
  ;; (httpkit/GET "/get-user-id/"
  ;;              (compojure/response (compojure/context ":player" [player] player-name-handler))))
  ;;(httpkit/POST "/set-score"
  ;;              (compojure/context "/user/:id" [id] score-handler)))

(defn -main []
  (println "Server started on localhost:8080")
  (run-server all-routes {:port 8080})
  (db/-main))

