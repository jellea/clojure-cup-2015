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
            [cljsjs.codemirror.addon.edit.closebrackets]
            [clojure-cup-2015.content :as content]
            [clojure-cup-2015.quil-symbols :as quil-symbols]
            [quil.core]
            [quil.middleware]))

(enable-console-print!)

(declare eval)

(defonce cljs-compiler-state (cljs/empty-state))

(defn canvas-editor
  "Code mirror + a canvas, so quil can render to it"
  [id default-code]
  [:div
   [:canvas.right {:id id}]
   [cm-editor {:on-change eval :default-value default-code} {}]])

(defn inject-editors
  "Replace [:quil-code ...] in the content data with canvas-editor components."
  [c]
  (if (vector? c)
    (if (= :quil-code (first c))
      (into [canvas-editor] (rest c))
      (mapv inject-editors c))
    c))

(defn error! [error]
  (swap! !state assoc :error error))

(defn dismiss! [] (error! nil))

(defn q [selector] (.querySelector js/document selector))




(defn eval [in-str]
  (prn in-str)
  (let [st cljs-compiler-state]
    (cljs/eval-str st in-str 'fiddle.runtime
                   {:eval cljs/js-eval
                    :ns 'fiddle.runtime
                    ;; don't ask me why this works. It stops Clojurescript from complaining that
                    ;; *load-fn* isn't defined
                    :load (fn [_ cb] (cb {:lang :clj :source ""}))}
                   (fn [{:keys [error value]}]
                     (if error
                       (do
                         (error! (->> error .-cause .-message))
                         (swap! !state assoc :result nil))
                       (do
                         (dismiss!)
                         (swap! !state assoc :result (str value))))))))

(eval (str "(ns fiddle.runtime (:require [quil.core :as q] [quil.middleware :as m])) "))

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
   [:div
    (inject-editors content/chapter-1)]
   [:div.results
    [result-display]]])

(defn on-js-reload [])

(defn init []
  (reagent/render-component bang-bang
                            (. js/document (getElementById "app"))))

(init)

(comment
  [cm-editor
   {:on-change eval
    :default-value (:initial-code config)}
   {:matchBrackets true
    :lineNumbers false
    :autoCloseBrackets true
    :theme "monokai"
    :mode "clojure"}])
