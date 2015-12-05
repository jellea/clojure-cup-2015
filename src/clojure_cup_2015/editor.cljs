(ns clojure-cup-2015.editor
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure-cup-2015.common :refer [config !state]]))

(def opts {:matchBrackets true
           :lineNumbers false
           :autoCloseBrackets true
           :theme "monokai"
           :mode "clojure"
           :lineWrapping true})

(defn move-canvas
  [cm canvas-id]
  (when canvas-id
    (let [canvas (.getElementById js/document canvas-id)
          cmheight (.heightAtLine cm (+ (.-line (.getCursor cm)) 15) "local")
          height (max (- cmheight (.-height canvas)) 5)]
      (set! (.. canvas -style -transform) (str "translateY(" height "px)")))))

(defn outer-sexp
  "Returns the outer sexp"
  [cm]
  (if-not (-> cm (.getTokenAt (.getCursor cm))
              .-state
              .-indentStack)
    (prn "not in from")

    (do
      (while (-> cm (.getTokenAt (.getCursor cm)) .-state .-indentStack)
        (.moveH cm -1 "char"))
      (let [start (.getCursor cm)]
        (.moveH cm 1 "char")
        (while (-> cm (.getTokenAt (.getCursor cm)) .-state .-indentStack)
          (.moveH cm 1 "char"))
        (let [end (.getCursor cm)]
          (.setSelection cm start end)
          (.getSelection cm))))))

(defn add-inline
  "Add a inline comment/result/documentation what ever"
  [{:keys [line ch text]} editor]
  (let [dom-node (.createElement js/document "span")]
    (set! (.-innerHTML dom-node) text)
    (.setBookmark (.-doc editor) #js {:line line :ch ch} #js {:widget dom-node})))

(defn cm-editor
  "CodeMirror reagent component"
  [props cm-opts]
  (reagent/create-class
   {:component-did-mount
    (fn [this]
      (let [eval-code #((:on-change props) (.getValue %))
            editor (.fromTextArea js/CodeMirror (reagent/dom-node this) (clj->js (merge opts cm-opts)))]
        (eval-code editor)
        (add-inline {:line 0 :ch 100 :text "hi"} editor)
        (when (:monoline props)
          (js/oneLineCM editor))
        (.on editor "change" eval-code)
        (.on editor "cursorActivity" #(move-canvas % (:canvas-id props)))
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
