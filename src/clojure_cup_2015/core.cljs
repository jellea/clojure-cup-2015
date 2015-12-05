(ns clojure-cup-2015.core
  (:require [reagent.core :as reagent]
            [clojure-cup-2015.editor :refer [cm-editor]]
            [clojure-cup-2015.common :refer [config !state]]
            [cljs.js :as cljs]
            [cljs.tools.reader]
            [cljsjs.codemirror]
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.matchbrackets]
            [cljsjs.codemirror.addon.fold.foldgutter]
            [cljsjs.codemirror.addon.edit.closebrackets]))

(enable-console-print!)

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
   [:div {:style {:width "600px"}}
    [cm-editor
     {:on-change eval
      :default-value (:initial-code config)}
     {:matchBrackets true
      :lineNumbers false
      :autoCloseBrackets true
      :theme "monokai"
      :mode "clojure"}]]
   [:div.results
    [result-display]]])

(defn on-js-reload [])

(defn init []
  (reagent/render-component bang-bang
                            (. js/document (getElementById "app"))))

(init)
