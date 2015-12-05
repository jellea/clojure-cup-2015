(ns clojure-cup-2015.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.js :as cljs]
            [cljs.tools.reader :as r]
            [cljsjs.codemirror]
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.matchbrackets]
            [cljsjs.codemirror.addon.fold.foldgutter]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [clojure-cup-2015.quiltest]
            [clojure-cup-2015.content :as content]))

(enable-console-print!)

(def config
  {:initial-code "(+ 1 4)"})

(defonce !state (atom {}))

(defn error! [error]
  (swap! !state assoc :error error))

(defn dismiss! [] (error! nil))

(defn q [selector] (.querySelector js/document selector))

(defn eval [in-str !result]
  (let [st (cljs/empty-state)]
    (cljs/eval-str st in-str 'fiddle.runtime
                   {:eval cljs/js-eval :source-map true :ns 'fiddle.runtime}
                   (fn [{:keys [error value]}]
                     (if error
                       (error! (->> error .-cause .-message))
                       (do
                         (dismiss!)
                         (reset! !result (str value))))))))

(defn init-code-mirror [cm-ref !cm-obj !result !default-code]
  (let [cm (js/CodeMirror cm-ref
              #js {:value @!default-code
                   ; :lineNumbers true
                   :matchBrackets true
                   :autoCloseBrackets true
                   :theme "monokai"
                   :mode "clojure"})]
    (.on cm "change" #(let [current-code (-> cm .-doc .getValue)]
                        (reset! !default-code current-code)
                        (eval current-code !result)))
    (reset! !cm-obj cm)))

(defn editor [default-code]
  (let [!local {:cm-obj nil :value default-code :result ""}
        !cm-obj (atom nil)
        !value  (atom default-code)
        !result (atom nil)]
    (reagent/create-class
     {:component-did-mount
      (fn [this default-code]
        (let [cm-ref (.getDOMNode (aget this "refs" "cm"))]
          (init-code-mirror cm-ref !cm-obj !result !value)))
      :reagent-render
      (fn [default-code]
        [:div.editor
         [:div {:ref "cm"}]
         [:p "=>" @!result]])})))

(defn bang-bang []
  (let [{:keys [error] :as state} @!state]
    [:div
     (when error
       [:div.error
        [:a {:href "#"} [:i.white {:on-click dismiss!} "X"]]
        [:p "ERROR"]
        [:p error]])
     [editor "(+ 3 2)"]]))

(reagent/render-component [bang-bang]
                          (. js/document (getElementById "app")))


(defn on-js-reload [])
;; optionally touch your !state to force rerendering depending on
;; your application
;; (swap! !state update-in [:__figwheel_counter] inc)
