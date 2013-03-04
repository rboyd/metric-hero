(ns treemapper)

(def camera (THREE/PerspectiveCamera. 75 (/ window/innerWidth window/innerHeight) 1 10000))
(def scene (THREE/Scene.))
(def r (THREE/CanvasRenderer.))
(def geometry (THREE/CubeGeometry. 200 200 200))
(def material (THREE/MeshBasicMaterial. (js-obj "color" 1500 "wireframe" true)))

(defn ^:export init []
  (aset (.-position camera) "x" 400)
  (aset (.-position camera) "y" 650)
  (aset (.-position camera) "z" 1000)
  (.lookAt camera (THREE/Vector3. 400 0 300))
  (.setSize r window/innerWidth window/innerHeight)
  (.appendChild document/body (.-domElement r))
  (.render r scene camera))

(declare scale-fn)

(defn ^:export render-node [node]
  (let [width     (.-dx node)
        length    (.-dy node)
        height    (scale-fn (.-modified node))
        x         (+ (.-x node) (/ width 2))
        y         (/ height 2)
        z         (+ (.-y node) (/ length 2))
        node-geo  (THREE/CubeGeometry. width height length)
        node-mesh (THREE/Mesh. node-geo material)]
    (aset (.-position node-mesh) "x" x)
    (aset (.-position node-mesh) "y" y)
    (aset (.-position node-mesh) "z" z)
    (.add scene node-mesh)
    (.render r scene camera))
  (render-nodes (.-children node)))

(defn ^:export render-nodes [nodes]
  (doall (map render-node nodes)))

(defn get-size [node]
  (.-size node))

(defn find-scale [nodes]
  (let [max-time (reduce max (map #(.-modified %) nodes))
        min-time (reduce min (map #(.-modified %) nodes))
        factor   (/ 100 (- max-time min-time))
        scale-fn #(* (- % min-time) factor)]
    scale-fn))

(declare nodes)
(defn ^:export parse-nodes [_ root]
  (def nodes root)
  (let [d3-treemap (-> (.treemap d3/layout)
                       (.size (array 800 600))
                       (.sticky true)
                       (.value get-size))]
    (def scale-fn (find-scale (.-children root)))
    (.nodes d3-treemap nodes)
    (render-nodes (.-children nodes))))

(defn ^:export print-node [node]
  (.log js/console node))

(defn ^:export print-nodes [nodes]
  (doall (map print-node nodes)))

(defn ^:export render []
  (init)
  (.json js/d3 "output.json" parse-nodes))
