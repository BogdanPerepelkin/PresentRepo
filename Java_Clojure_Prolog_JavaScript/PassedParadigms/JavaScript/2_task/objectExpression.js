// Const
function Const(value) {
    this.value = value;
}
Const.prototype.evaluate = function() { return this.value; };
Const.prototype.toString = function() { return this.value.toString(); }

// Variable
const varMap = { "x": 0, "y": 1, "z": 2 };
function Variable(name) {
    this.name = name;
    this.number = varMap[name];
}
Variable.prototype.evaluate = function(...args) { return args[this.number]; }
Variable.prototype.toString = function() { return this.name; };

// Abstract Operations
function AbstractOperation(...operations) {
    this.operations = operations;
}
AbstractOperation.prototype.evaluate = function(...args) {
    return this.func(...this.operations.map(cur => cur.evaluate(...args)));
};
AbstractOperation.prototype.toString = function() {
    return this.operations.map(op => op.toString()).join(" ") + " " + this.strSign;
};

function operationFabric(func, strSign) {
    const constructor = function(...args) {
        AbstractOperation.call(this, ...args)
    }
    constructor.prototype = Object.create(AbstractOperation.prototype);
    constructor.prototype.constructor = constructor;
    constructor.prototype.func = func;
    constructor.prototype.strSign = strSign;
    return constructor;
}

// Literally Operations
const Add = operationFabric((a, b) => a + b, "+");
const Subtract = operationFabric((a, b) => a - b, "-");
const Multiply = operationFabric((x, y) => x * y, "*");
const Divide = operationFabric((x, y) => x / y, "/");
const Negate = operationFabric(x => -x, "negate");
const Clamp = operationFabric((x, minimum, maximum) => Math.max(minimum, Math.min(x, maximum)), "clamp");
const SoftClamp =
    operationFabric(
        (x, minimum, maximum, lambda) =>
            minimum + (maximum - minimum) / (1 + Math.exp(
                lambda * ((maximum + minimum) / 2 - x)
                )
            ),
        "softClamp",
    )


// parsing by typical walk on stack
const signMap = {
    "negate": [Negate, 1],
    "+": [Add, 2],
    "-": [Subtract, 2],
    "*": [Multiply, 2],
    "/": [Divide, 2],
    "clamp": [Clamp, 3],
    "softClamp": [SoftClamp, 4]
}

function parse(expression) {
    const a = expression.split(" ").filter(elem => elem.trim() !== "");
    const stack = [];

    for (const i of a) {
        if (i in signMap) {
            const [Class, amountArgs] = signMap[i];
            const args = [];
            for (let i = 0; i < amountArgs; i++) {
                args.push(stack.pop());
            }
            stack.push(new Class(...args.reverse()));
        } else if (i in varMap) {
            stack.push(new Variable(i));
        } else {
            stack.push(new Const(parseInt(i)));
        }
    }

    return stack[0];
}


// example
//
// let expr = new Negate(new Const(-53))
// console.log(expr.evaluate(2, 1, 1));
// console.log(expr.toString());

let expr2 = parse("(- (* 2 x) 3)");
console.log(expr2.toString());
console.log(expr2.evaluate(5, 4));
