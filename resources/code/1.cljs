(defn draw-pink-triangles []
  ;; First we set the stage: a background color, and no borders around shapes
  (background 20 200 151)
  (no-stroke)

  ;; Set a fill color for shapes. The numbers correspond with
  ;; red - green - blue, and go up to 255
  (fill 34 95 215)

  ;; Fill the width and height of the canvas with triangles
  (doseq [x (range 0 (width) 50)
          y (range 0 (height) 50)]
    (triangle (+ x 25) y
              x (+ y 50)
              (+ x 50) (+ y 50))))
