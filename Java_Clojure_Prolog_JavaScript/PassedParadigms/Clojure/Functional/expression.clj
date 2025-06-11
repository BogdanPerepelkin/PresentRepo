(defn constant [val]
    (fn [arg] val))

(defn variable [val]
    #(get % val))

; any amount of args. Just create correct "func"
(defn abstractOp [func]
    (fn [& args]
        (fn [mapVals]
            (apply func (map #(% mapVals) args))
            )))
(def add (abstractOp +))
(def subtract (abstractOp -))
(def multiply (abstractOp *))
(def divide (abstractOp (fn [& args] (/ (double a) (double b)))))
(def negate (abstractOp -))
(def arcTan (abstractOp Math/atan))
(def arcTan2 (abstractOp Math/atan2))

(def operations {
                 '+ add,
                 '- subtract,
                 '* multiply,
                 '/ divide,
                 'negate negate,
                 'atan arcTan,
                 'atan2 arcTan2
                 })

(defn parse [expr]
    (cond
        (number? expr) (constant expr)
        (symbol? expr) (variable (str expr))
        (list? expr) (let [op (first expr)
                           args (rest expr)]
                         (apply (get operations op) (map parse args)))))

(defn parseFunction [inStr]
    (parse (read-string inStr)))
