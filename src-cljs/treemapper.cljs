(ns treemapper)

(def camera (THREE/PerspectiveCamera. 75 (/ window/innerWidth window/innerHeight) 1 10000))
(def scene (THREE/Scene.))
(def r (THREE/CanvasRenderer.))
(def geometry (THREE/CubeGeometry. 200 200 200))
(def material (THREE/MeshBasicMaterial. (js-obj "color" 1500 "wireframe" true)))
(def mesh (THREE/Mesh. geometry material))

(defn ^:export init []
  (aset (.-position camera) "z" 1000)
  (.add scene mesh)
  (.setSize r window/innerWidth window/innerHeight)
  (.appendChild document/body (.-domElement r)))

(defn ^:export animate []
  (js/requestAnimationFrame animate)
  (let [rot (.-rotation mesh)
        x (.-x rot)
        y (.-y rot)]
    (aset rot "x" (+ x 0.01))
    (aset rot "y" (+ y 0.01)))
  (.render r scene camera))

(defn ^:export render []
  (init)
  (animate))
