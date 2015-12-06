(ns clojure-cup-2015.core
  (:require [reagent.core :as reagent]
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

(enable-console-print!)

(defn q [selector] (.querySelector js/document selector))
(defn by-id [id] (.getElementById js/document id))

(defn canvas-editor
  "Code mirror + a canvas, so quil can render to it"
  [id default-code]
  [:div
   [:div.output [:canvas.right {:id id}]]
   [:div [cm-editor {:default-value default-code
                     :id id}
                    {}]]])

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

(defn init []
  (reagent/render-component bang-bang (. js/document (getElementById "app"))))

(init)
