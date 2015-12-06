(ns clojure-cup-2015.core
  (:require-macros [clojure-cup-2015.macro :refer [read-snippets]])
  (:require [reagent.core :as reagent]
            [dommy.core :as d
             :refer-macros [sel sel1]]
            [clojure-cup-2015.editor :refer [cm-editor mirrors]]
            [clojure-cup-2015.common :refer [config !state !tooltip]]
            [cljsjs.codemirror]
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.matchbrackets]
            [cljsjs.codemirror.addon.fold.foldgutter]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [clojure-cup-2015.content :as content]
            [quil.core]
            [quil.middleware]))

(def !snippets (atom {}))

(enable-console-print!)

(defn q [selector] (.querySelector js/document selector))
(defn by-id [id] (.getElementById js/document id))

(defn error-display [id]
  (let [{:keys [error]} @!state
        cid (:id error)]
    [:div
     (when (and error (= id cid))
       [:p.error (:message error)])]))

(defn revert! [id _]
  (when-let [mirror (get @mirrors id)]
    (when-let [snippet (get @!snippets id)]
      (.setValue mirror snippet))))

(defn canvas-editor
  "Code mirror + a canvas, so quil can render to it"
  [id default-code]
  [:div
   [:div.right.holder {:id (str id "_holder")}
    [:canvas {:id id}]
    [error-display id]]
   [cm-editor {:default-value default-code :id id} {}]
   [:div.right {:style {:margin-right "315px"
                        :position "relative"
                        :z-index 400
                        :transform "translateY(-45px)"}}
    [:div.btn.bg-red.rounded.mr1 {:on-click (partial revert! id)} "revert code"]
    [:div.btn.bg-green.rounded "restart sketch"]]])

(defn monoline-editor
  [id default-code]
  [:div [cm-editor {:default-value default-code
                    :monoline true
                    :id id}
         {:scrollbarStyle "null"}]])

(defn tooltip [tt]
  (if @tt
    (let [{left :left top :top doc :doc name :name} @tt]
      [:div.tooltip {:style {:position "absolute"
                             :left left :top top
                             :z-index 2000000
                             :backgroundColor "#d0faf4"
                             :border "2px solid #c6ddf5"
                             :maxWidth  "40em"
                             :maxHeight "10000px"
                             :padding "1em"}}
       [:div {:style {:textDecoration "underline"
                      :fontWeight "bold"}} name]
       [:div doc]])
    [:div]))

(defn on-js-reload [])

(defn mirrorize-one! [e]
  (let [monoline (d/attr e "data-monoline")
        cmid (d/attr e "data-cmid")
        text (->> e .-text clojure.string/trim)
        new (d/create-element :div)]
    (d/remove-class! e "editor") ;; only replace once (d/add-class! editor "cm")
    ;; (d/add-class! new "cm")
    (d/insert-before! new e)
    (reagent/render-component [(if monoline monoline-editor canvas-editor) cmid text] new)
    (swap! !snippets assoc cmid text)))

(defn mirrorize! []
  (doseq [e (sel ".editor")]
    (mirrorize-one! e)))

(defn init []
  (mirrorize!)
  (reagent/render-component [tooltip !tooltip] (d/sel1 :#tooltip)))

(init)
