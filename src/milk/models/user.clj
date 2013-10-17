(ns milk.models.user
  (:refer-clojure :exclude [sort find])
  (:require [monger.collection :as mc]
            [noir.session :as session]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [monger.query :refer [with-collection find sort paginate]])
  (:import org.bson.types.ObjectId))

(defn get-user [username]
  (println username  (mc/find-one-as-map "users" {:username username}))
  (mc/find-one-as-map "users" {:username username}))

(defn get-user-by-id [id]
  (mc/find-map-by-id "users" (ObjectId. id)))

(defn user-pastes [user page & [others]]
  (with-collection "pastes"
    (find (merge {:user (str (:_id (get-user user)))} others))
    (sort {:date -1})
    (paginate :page page :per-page 10)))

(defn count-user-pastes [user & [others]]
  (mc/count "pastes" (merge {:user (str (:_id (get-user user)))} others)))




(defn admin? []
  (session/get :admin))

(defn me []
  (session/get :username))

(defn valid-user? [username]
  (vali/rule (not (first (mc/find "users" {:username username})))
             [:username "That username is already taken"])
  (vali/rule (vali/min-length? username 3)
             [:username "Username must be at least 3 characters."])
  (not (vali/errors? :username :password)))

(defn prepare [{password :password :as user}]
  (assoc user :password (crypt/encrypt password)))

(defn valid-psw? [password]
  (vali/rule (vali/min-length? password 5)
             [:password "Password must be at least 5 characters."])
  (not (vali/errors? :password)))

(defn- store! [{:keys [username password]}]
  (mc/update  "users"  {:username username}  { :username username :password password} :upsert true ))

(defn login! [{:keys [username password] :as user}]
  (println "here is user1111 " username (get-user username) )
  (let [{stored-pass :password} (get-user username)]
    (if (and stored-pass 
             (crypt/compare password stored-pass))
      (do
        (session/put! :admin true)
        (session/put! :username username))
      (vali/set-error :username "Invalid username or password"))))

(defn add! [{:keys [username password] :as user}]
  (when (valid-user? username)
    (when (valid-psw? password)
      (-> user (prepare) (store!))))
  )

(defn edit! [{:keys [username old-name password]}]
  (let [user {:username username :password password}]
    (if (= username old-name)
      (when (valid-psw? password)
        (-> user (prepare) (store!)))
      (add! user))))

(defn remove!
  "Delete an existing paste."
  [username]
  (mc/remove "users" {:username username}))