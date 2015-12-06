(ns clojure-cup-2015.common
  (:require [reagent.core :as reagent]))

(def config
  {:initial-code "(+ 1 4)"})

(defonce !tooltip (reagent/atom nil))

(defonce !state (reagent/atom {:code (:initial-code config)}))
