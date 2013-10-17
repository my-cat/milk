(ns milk.models.paste
  (:refer-clojure :exclude [sort find])
  (:require [noir.session :as session]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clj-time.core :as ctime]
            [clj-time.coerce :as coerce]
            [clj-time.format :as tform]
            [noir.validation :as vali]
            [monger.collection :as mc]
            [monger.query :refer [with-collection find sort limit paginate]]
            [monger.operators :refer [$inc]])
  (:import java.io.StringReader
           org.apache.commons.codec.digest.DigestUtils))

(def date-format (tform/formatter "MM/dd/yy" (ctime/default-time-zone)))
(def time-format (tform/formatter "hh:mm" (ctime/default-time-zone)))
(def paste-id
  "The current highest paste-id."
  (atom
   (-> (with-collection "pastes"
         (find {})
         (sort {:id -1})
         (limit 1))
         first
         :id)))

(defn preview
  "Get the first 5 lines of a string."
  [s]
  (->> s StringReader. io/reader line-seq (take 5) (string/join "\n")))

(defn generate-id
  "Generate a hex string of a SHA1 hack of a random UUID.
   Return the first 25 characters."
  []
  (-> (java.util.UUID/randomUUID)
      str
      DigestUtils/shaHex
      (.substring 0 25)))

(defn perma-link-ge [id]
  (str "/milk/view/" id))

(defn edit-url [{:keys [paste-id]}]
  (str "/milk/paste/edit/" paste-id ))


(defn valid-x? [{:keys [title contents]}]
  (vali/rule (vali/has-value? title)
             [:title "There must be a title"])
  (vali/rule (vali/has-value? contents)
             [:contents "There's no post content."])
  (not (vali/errors? :title :contents)))

(defn paste-map [id random-id user title  contents  fork views perma-link]
  (let [private  false 
        random-id (or random-id (generate-id))
        perma-link (perma-link-ge id)]
       
      {:paste-id (if private random-id (str id))
       :id id
       :random-id random-id
       :title title 
       :user  user
       :contents contents
       :summary  (preview contents)
       :private (boolean private)
       :lines (let [lines (count (filter #{\newline} contents))]
                (if (= \newline (last contents))
                  lines
                  (inc lines)))
       :fork fork
       :views views
       :perma-link perma-link
         }
       ))

(defn wrap-time [paste]
  (let [ts (ctime/now)]
    (-> paste
      (assoc :ts (coerce/to-long ts))
      (assoc :date (tform/unparse date-format ts))
      (assoc :tme (tform/unparse time-format ts)))))

(defn validate [contents]
  (cond
    (>= (count contents) 614400) {:error "That paste was too big. Has to be less than 600KB"}
    (not (re-seq #"\S" (str contents))) {:error "Your paste cannot be empty."}
    :else {:contents contents}))


(defn same-user? [user paste]
  (or (and user (= (:id user) (:user paste)))
      (some #{(:paste-id paste)} (session/get :anon-pastes))))



(defn paste
  "Create a new paste."
  [ title contents  user & [fork]]
      (let [id (swap! paste-id inc)
            random-id (generate-id)
            paste (wrap-time (paste-map id
                    random-id
                    user
                    title
                    contents
                    fork
                    0
                    ""))]
            (when (valid-x? paste )
            (mc/insert-and-return "pastes" paste))))


(defn get-paste
  "Get a paste."
  [id]
  (mc/find-one-as-map "pastes" {:paste-id id}))

(defn view-paste
  "Get a paste and increment its view count."
  [id]
  (let [views (session/get :views)]
    (when (>= (count views) 5000)
      (session/remove! :views))
    (if (some #{id} views)
      (get-paste id)
      (when-let [paste (mc/find-and-modify "pastes" {:paste-id id}
                                           {$inc {:views 1}}
                                           :return-new true)]
        (session/update-in! [:views] conj id)
        paste))))

(defn get-paste-by-id
  "Get a paste by its :id key (which is the same regardless of being public or private."
  [id]
  (mc/find-one-as-map "pastes" {:paste-id id}))

(defn update-paste
  "Update an existing paste."
  [{:keys [id title contents] }]
  (println "this is in update " title id contents)
  (let [paste  (get-paste id ) 
      {:keys [id random-id user fork]} paste  
                  paste-new (wrap-time (paste-map id
                    random-id
                    user
                    title
                    contents
                    fork
                    0
                    "")) ]  
                         (println "I am in the here " paste-new id paste )  
                  (if (valid-x? paste-new )
                (mc/update "pastes" {:id id} paste-new )
                false)))

(defn delete-paste
  "Delete an existing paste."
  [id]
  (mc/remove "pastes" {:paste-id id}))

(defn get-pastes
  "Get public pastes."
  [page]
  (with-collection "pastes"
    (find {:private false})
    (sort {:date -1})
    (paginate :page page :per-page 20)))

(defn count-pastes
  "Count pastes."
  [& [private?]]
  (mc/count "pastes" (if-not (nil? private?)
                       {:private private?}
                       {})))

(defn get-forks [paste page]
  "Get forks of a paste."
  (with-collection "pastes"
    (find {:fork (:id paste)
           :private true})
    (sort {:date -1})
    (paginate :page page :per-page 20)))

(defn count-forks [paste]
  "Count forks of a paste."
  (mc/count "pastes" {:fork (:id paste)
                      :private false}))

(defn count-pages [n per]
  (long (Math/ceil (/ n per))))

(defn proper-page [n]
  (if (<= n 0) 1 n))

(defn get-latest []
  (get-pastes 1))
