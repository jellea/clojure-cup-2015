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

(defn error-display [id]
  (let [{:keys [error]} @!state
        cid (:id error)]
    [:div
     (when (and error (= id cid))
       [:p.error (:message error)])]))

(defn canvas-editor
  "Code mirror + a canvas, so quil can render to it"
  [id default-code]
  [:div
   [:div.right.holder {:id (str id "_holder")}
    [:canvas {:id id}]
    [error-display id]]
   [cm-editor {:default-value default-code :id id} {}]])

(defn monoline-editor
  [id default-code]
  [:div [cm-editor {:default-value default-code
                    :monoline true
                    :id id}
         {:scrollbarStyle "null"}]])

(defn on-js-reload [])

(defn mirrorize-one! [e]
  (let [monoline (d/attr e "data-monoline")
        cmid (d/attr e "data-cmid")
        text (->> e .-text clojure.string/trim)
        new (d/create-element :div)]
    (d/remove-class! e "editor") ;; only replace once (d/add-class! editor "cm")
    ;; (d/add-class! new "cm")
    (d/insert-before! new e)
    (if monoline
      (reagent/render-component [monoline-editor cmid text] new)
      (reagent/render-component [canvas-editor cmid text] new))))

(defn mirrorize! []
  (doseq [e (sel ".editor")]
    (mirrorize-one! e)))

(defn init []
  (mirrorize!)
  #_(reagent/render-component bang-bang
                              (. js/document (getElementById "app"))))

(init)
