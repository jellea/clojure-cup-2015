(ns clojure-cup-2015.core
  (:require-macros [clojure-cup-2015.macro :refer [read-snippets]])
  (:require [reagent.core :as reagent]
            [dommy.core :as d
             :refer-macros [sel sel1]]
            [clojure-cup-2015.editor :refer [cm-editor]]
            [clojure-cup-2015.common :refer [config !state]]
            [cljsjs.codemirror]
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.matchbrackets]
            [cljsjs.codemirror.addon.fold.foldgutter]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [clojure-cup-2015.content :as content]
            [quil.core]
            [quil.middleware]))

(def snippets (read-snippets 5))

(enable-console-print!)

(defn q [selector] (.querySelector js/document selector))
(defn by-id [id] (.getElementById js/document id))

(defn error-display []
  (let [{:keys [error]} @!state]
    [:div
     (when error
       [:p.error error])]))

(defn canvas-editor
  "Code mirror + a canvas, so quil can render to it"
  [id default-code]
  [:div
   [:div.right.holder {:id (str id "_holder")}
    [:canvas {:id id}]
    [:i {:class "fa fa-lg fa-history"}]
    [error-display]]
   [cm-editor {:default-value default-code :id id} {}]])

(defn monoline-editor
  [id default-code]
  [:div [cm-editor {:default-value default-code
                    :monoline true
                    :id id}
         {:scrollbarStyle "null"}]])

(defn inject-editors
  "Replace [:quil-code ...] in the content data with canvas-editor components."
  [c]
  (if (vector? c)
    (if (= :quil-code (first c))
      (into [canvas-editor] (rest c))
      (mapv inject-editors c))
    c))

(defn bang-bang []
  [:div
   [:div.input (inject-editors content/all)]])

(defn on-js-reload [])

(defn mirrorize-one! [e]
  (let [cmid (d/attr e "data-cmid")
        text (->> e .-text clojure.string/trim)
        new (d/create-element :div)]
    (d/remove-class! e "editor") ;; only replace once (d/add-class! editor "cm")
    ;; (d/add-class! new "cm")
    (d/insert-before! new e)
    (reagent/render-component [canvas-editor cmid text] new)))

(defn mirrorize! []
  (doseq [e (sel ".editor")]
    (mirrorize-one! e)))

(defn init []
  (mirrorize!)
  #_(reagent/render-component bang-bang
                              (. js/document (getElementById "app"))))

(init)
