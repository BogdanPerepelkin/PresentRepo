(defn vectorOp [func & args]
    (apply mapv func args))
(def v+ (partial vectorOp +))
(def v- (partial vectorOp -))
(def v* (partial vectorOp *))
(def vd (partial vectorOp /))

(defn scalar [& args]
    (reduce + (apply v* args)))

; :NOTE: possible to make reading better: replace (a b) by (x1 y1 z1) as args
(defn vect [& args]
    (reduce (fn [a b]
                [(- (* (a 1) (b 2)) (* (a 2) (b 1)))
                 (- (* (a 2) (b 0)) (* (a 0) (b 2)))
                 (- (* (a 0) (b 1)) (* (a 1) (b 0)))]
                ) args))

; :NOTE: repeat - have not use it
(defn v*s [vec & args]
    (mapv * vec (repeat (reduce * args))))

(defn matrixOp [func & args]
    (apply vectorOp (partial vectorOp func) args))
(def m+ (partial matrixOp +))
(def m- (partial matrixOp -))
(def m* (partial matrixOp *))
(def md (partial matrixOp /))

(defn m*s [mx & args]
    (mapv #(apply v*s % args) mx))

(defn m*v [mx vec]
    (mapv (fn [mxVec] (scalar mxVec vec))
          mx))

(defn transpose [mx]
    (apply mapv vector mx))

(defn m*m [& args]
    (reduce
        (fn [mA mB]
            (let [transp (transpose mB)]
            (mapv (fn [aVec] (mapv (fn [bVec] (scalar aVec bVec)) transp)) mA))
        )
        args))

(defn vectorInSquare? [args]
    (and (vector? args)
         (every? (fn [x] (and (every? vector? x) (vector? x)))
            args)
    ))

(defn tensorOp [func]
    (fn [& args]
    (if (every? vector? args)
        (apply mapv (tensorOp func) args)
        (apply func args)
    ))
    )

(def t+ (tensorOp +))
(def t- (tensorOp -))
(def t* (tensorOp *))
(def td (tensorOp /))

;(println "t+ ->" (t+ [[1] [2]] [[2] [3]]))
;(println "v* ->" (v* [1 2 3] [4 5 6]))
;(println "scalar ->" (scalar [1 2] [1 2]))
;(println "vect ->" (vect [1 4 2] [1 2 7] [1 2 3]))
;(println "v*s ->" (v*s  [1 2 3] 2 3 5 3))
;(println "ma+mb ->" (m+ [[1 2] [3 4]] [[ 1 2] [3 4]] [[2 3] [4 5]]))
;(println "m*s ->" (m*s [[1 2] [3 4]] 1 2 3))
;(println "m*v ->" (m*v [[1 2] [3 4]] [1 1]))
;(println "m*m my ->" (m*m [[1 2] [3 4]] [[5 6] [7 8]]))