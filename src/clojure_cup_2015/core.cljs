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

(def config
  {:initial-code "(+ 1 4)"})

(defonce !state (atom {:code "(* 3 8)"}))

(defonce cljs-compiler-state (cljs/empty-state))

(defn cm-editor
  [props {:as cm-opts :or {matchBrackets true
                           lineNumbers false
                           autoCloseBrackets true
                           theme "monokai"
                           mode "clojure"}}]
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

(defn canvas-editor
  "Code mirror + a canvas, so quil can render to it"
  [id default-code]
  [:div
   [cm-editor {:on-change eval :default-value default-code} {}]
   [:canvas {:id id}]])

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
                   {:eval cljs/js-eval :source-map true :ns 'fiddle.runtime}
                   (fn [{:keys [error value]}]
                     (if error
                       (do
                         (error! (->> error .-cause .-message))
                         (swap! !state assoc :result nil))
                       (do
                         (dismiss!)
                         (swap! !state assoc :result (str value))))))))

(eval (str "(ns clojurecup.user " (quil-symbols/make-require-str) ")"))

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
