(ns clojure-cup-2015.content)

(def header
  [:header
   [:h1 "Into The Land of Quil"]
   [:h2 "A Great and Valiant Journey of Derring-do"]])


(def chapter-1
  [:div
   [:p "Goodday, traveler. Disinhiret your eyes, because by the time this journey ends, you will no longer trust them."]
   [:p "Quil lets you do visual programming. You can make drawings and animations, and give instant feedback to user interaction."]
   [:p "A Quil program is called a sketch."]
   [:quil-code
    "pink-triangles" "(defn draw-pink-triangles []
  (no-stroke)
  (background 255)

  (fill 240 150 155) ;; #f4d5dd
  (doall (for [x (range 0 (width) 50)
               y (range 0 (height) 50)]
    (triangle (+ x 25) y, x (+ y 50), (+ x 50) (+ y 50)))))

(sketch
  :host \"pink-triangles\"
  :size [300 300]
  :draw draw-pink-triangles)"]

   [:p "and something else"]

   #_[:quil-code "tailspin" "
(defn setup []
  (frame-rate 30)
  (let [max-r (/ (width) 2)
        n (int (map-range (width)
                            200 500
                            20 50))]
   {:dots (into [] (for [r (map #(* max-r %)
                                (range 0 1 (/ n)))]
                     [r 0]))}))

(def speed 0.0003)

(defn move [dot]
  (let [[r a] dot]
    [r (+ a (* r speed))]))

(defn update-state [state]
  (update-in state [:dots] #(map move %)))

(defn dot->coord [[r a]]
  [(+ (/ (width) 2) (* r (cos a)))
   (+ (/ (height) 2) (* r (sin a)))])

(defn draw-state [state]
  (background 255)
  (fill 0)
  (let [dots (:dots state)]
    (loop [curr (first dots)
           tail (rest dots)
           prev nil]
      (let [[x y] (dot->coord curr)]
        (ellipse x y 5 5)
        (when prev
          (let [[x2 y2] (dot->coord prev)]
            (line x y x2 y2))))
      (when (seq tail)
        (recur (first tail)
               (rest tail)
               curr)))))

(sketch
  :host \"tailspin\"
  :size [500 500]
  :setup setup
  :update update-state
  :draw draw-state
  :middleware [m/fun-mode])"]])


(def all
  [:div
   header
   chapter-1])
