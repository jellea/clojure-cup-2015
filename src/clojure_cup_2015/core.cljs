(ns clojure-cup-2015.core
  (:require-macros [clojure-cup-2015.macro :refer [read-snippets]])
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

(def snippets (read-snippets 5))

(enable-console-print!)

(declare eval)
(declare reload!)

(defonce cljs-compiler-state (cljs/empty-state))

(defn canvas-editor
  "Code mirror + a canvas, so quil can render to it"
  [id default-code]
  [:div
   [:div.output [:canvas.right {:id id}]]
   [:div [cm-editor {:on-change reload! :default-value default-code :canvas-id id} {}]]])

(defn monoline-editor
  [id default-code]
  [:div [cm-editor {:on-change reload! :default-value default-code :monoline true} {:scrollbarStyle "null"}]])

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
(defn by-id [id] (.getElementById js/document id))

(defn wobble! [e]
  (.remove (.-classList e) "wobble")
  (.setTimeout js/window #(.add (.-classList e) "wobble") 0))

(defn query-selector [s]
  (some-> (.querySelectorAll js/document s)
          array-seq))

(defn reload! [in-str]
  (doseq [e (query-selector ".output")]
    (wobble! e))
  (eval in-str))

(defn eval [in-str]
  (let [st cljs-compiler-state]
    (cljs/eval-str st in-str 'fiddle.runtime
                   {:eval cljs/js-eval
                    :ns 'fiddle.runtime
                    ;;:verbose true

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
   [:div.input (inject-editors content/all)]])

(defn on-js-reload [])

(defn init []
  (eval (str "(ns fiddle.runtime
     (:require [quil.core :as q]
               [quil.middleware :as m]))"
             (quil-symbols/import-symbols-src)))
  (reagent/render-component bang-bang
                            (. js/document (getElementById "app"))))

(init)
