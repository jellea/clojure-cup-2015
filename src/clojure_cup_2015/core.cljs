(ns clojure-cup-2015.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.js :as cljs]
            [cljs.tools.reader]
            [cljsjs.codemirror]
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.matchbrackets]
            [cljsjs.codemirror.addon.fold.foldgutter]
            [cljsjs.codemirror.addon.edit.closebrackets]))

(enable-console-print!)

(def config
  {:initial-code "(+ 1 4)"})

(defonce !state (atom {:code "(* 3 8)"}))

(defn cm-editor
  [props cm-opts]
  (reagent/create-class
   {:component-did-mount
    (fn [this]
      (let [editor (.fromTextArea js/CodeMirror (reagent/dom-node this) (clj->js cm-opts))]
        (.on editor "change" #((:on-change props) (.getValue %)))
        (reagent/set-state this {:editor editor})))

    :should-component-update
    (fn [this]
      (let [editor  (:editor (reagent/state this))
            val     (:code (:code @!state))
            update? (not= val (.getValue editor))]
        (when update? (.setValue editor val))
        update?))

    :reagent-render
    (fn [_] [:textarea {:default-value (:default-value props)}])}))

(defn error! [error]
  (swap! !state assoc :error error))

(defn dismiss! [] (error! nil))

(defn q [selector] (.querySelector js/document selector))

(defn eval [in-str]
  (let [st (cljs/empty-state)]
    (cljs/eval-str st in-str 'fiddle.runtime
                   {:eval cljs/js-eval :source-map true :ns 'fiddle.runtime}
                   (fn [{:keys [error value]}]
                     (if error
                       (do
                         (error! (->> error .-cause .-message))
                         (swap! !state assoc :result nil))
                       (do
                         (dismiss!)
                         (swap! !state assoc :result (str value))))))))

(defn error-display []
  (let [{:keys [error]} @!state]
    [:div
     (when error
       [:div.error
        [:a {:href "#"} [:i.white {:on-click dismiss!} "X"]]
        [:p "ERROR"]
        [:p error]])]))

(defn result-display []
  (let [{:keys [result]} @!state]
    [:div result]))

(defn bang-bang []
  [:div
   [error-display]
   [:div.col.col-9 [cm-editor
                    {:on-change eval
                     :default-value (:initial-code config)}
                    {:matchBrackets true
                     :lineNumbers false
                     :autoCloseBrackets true
                     :theme "monokai"
                     :mode "clojure"}]]
   [result-display]])

(defn on-js-reload [])

(reagent/render-component [bang-bang]
                          (. js/document (getElementById "app")))


