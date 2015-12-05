(ns clojure-cup-2015.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.js :as cljs]
            [cljs.tools.reader]
            [cljsjs.codemirror]))

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
  (prn in-str)
  (let [st (cljs/empty-state)]
    (cljs/eval-str st in-str 'fiddle.runtime
                   {:eval cljs/js-eval :source-map true :ns 'fiddle.runtime}
                   (fn [{:keys [error value]}]
                     (if error
                       (do
                         (prn "bang")
                         (error! (->> error .-cause .-message))
                         (swap! !state assoc :result nil))
                       (do
                         (dismiss!)
                         (swap! !state assoc :result (str value))))))))

(defn bang-bang []
  (let [{:keys [result error] :as state} @!state]
    [:div
     (when error
       [:div.error
        [:a {:href "#"} [:i {:class "fa fa-check fa-lg" :on-click dismiss!}]]
        [:p "ERROR"]
        [:p error]])
     [cm-editor {:on-change     eval
                 :default-value (:initial-code config)}
      {:mode              "text/x-clojure"
       :theme             "solarized light"
       :matchBrackets     true
       :autoCloseBrackets true
       :styleActiveLine   true
       :lineNumbers       true
       :autofocus         true}]
     [:div result]
     ]))

(reagent/render-component [bang-bang]
                          (. js/document (getElementById "app")))


(defn on-js-reload [])
;; optionally touch your !state to force rerendering depending on
;; your application
;; (swap! !state update-in [:__figwheel_counter] inc)
