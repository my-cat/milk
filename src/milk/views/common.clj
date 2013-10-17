(ns milk.views.common
  (:use 
       hiccup.core
       hiccup.page
       hiccup.element)
(:require [clojure.string :as string]
            [clojure.tools.macro :as macro]
             [me.raynes.laser :refer [defdocument defragment] :as l]
             [hiccup-bridge.core :as hicv]
             [clojure.java.io :refer [resource]] ))

(defmacro defpartial
  "Create a function that returns html using hiccup. The function is callable with the given name. Can optionally include a docstring or metadata map, like a normal function declaration."
  [fname & args]
  (let [[fname args] (macro/name-with-attributes fname args)
        [params & body] args]
    `(defn ~fname ~params
       (html
        ~@body))))
(def main-links [{:url "/milk/admin" :text "Admin"}])

(def admin-links [{:url "/" :text "Milk"}
                  {:url "/milk/admin" :text "Pastes"}
                  {:url "/milk/admin/users" :text "Users"}
                  {:url "/milk/logout" :text "Logout"}])

(def includes {:jquery (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js")
               :default (include-css "/css/default.css")
               :reset (include-css "/css/reset.css")
               :blog.js (include-js "/js/blog.js")})

(def includes-show {:jquery (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js")
               :default (include-css "/css/modern.css")
               :reset (include-css "/css/modern-responsive.css")
               :carousel.js (include-js "/js/carousel.js")})
;; Helper partials
(defn static [file]
  (-> file resource slurp l/unescaped))
(defpartial build-head [incls]
            [:head
             [:title "每日一文"]
             (map #(get includes %) incls)])


(defpartial build-head-x [incls]
            [:head
             [:title "The Picture Blog"]
             (map #(get includes-show %) incls)])

(defpartial link-item [{:keys [url cls text]}]
            [:li
             (link-to {:class cls} url text)])

;; Layouts

(defpartial main-layout [& content]
            (html5
              (build-head [:reset :default :jquery :blog.js])
              [:body
               [:div#wrapper
                [:div.content
                 [:div#header
                  [:h1 (link-to "/" "The Lib-Noir Pastes")]
                  [:ul.nav
                   (map link-item main-links)]]
                 content]]]))

(defpartial admin-layout [& content]
            (html5
              (build-head [:reset :default :jquery :blog.js])
              [:body
               [:div#wrapper
                [:div.content
                 [:div#header
                  [:h1 (link-to "/milk/admin" "Admin")]
                  [:ul.nav
                   (map link-item admin-links)]]
                 content]]]))

(defpartial show-layout [& content]
            (html5
              (build-head-x [:reset :default :jquery :carousel.js])
              (hicv/html->hiccup (slurp "src/milk/views/my.html"))))

(defpartial show-markdown []
            (html5              
              (hicv/html->hiccup (slurp "src/milk/views/markdown.html"))))

(defpartial show-home []
            (html5              
              (hicv/html->hiccup (slurp "src/milk/views/template/home.html"))))

(defpartial show-git  []
            (html5
              (hicv/html->hiccup (slurp "src/milk/views/template/git.html"))))

(defpartial show-parsehttp  []
            (html5
              (hicv/html->hiccup (slurp "src/milk/views/template/parsehttp.html"))))