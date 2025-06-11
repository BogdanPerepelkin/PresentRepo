// Const
function Const(value) {
    this.value = value;
}
Const.prototype.evaluate = function() { return this.value; };
Const.prototype.toString = function() { return this.value.toString(); }
Const.prototype.prefix = function() { return this.value.toString(); }
Const.prototype.postfix = function() {return this.value.toString(); }

// Variable
const varMap = { "x": 0, "y": 1, "z": 2 };
function Variable(name) {
    this.name = name;
    this.number = varMap[name];
}
Variable.prototype.evaluate = function(...args) { return args[this.number]; }
Variable.prototype.toString = function() { return this.name; };
Variable.prototype.prefix = function() { return this.name };
Variable.prototype.postfix = function() { return this.name };

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
AbstractOperation.prototype.prefix = function() {
    return "(" + this.strSign + " " + this.operations.map(op => op.prefix()).join(" ") + ")";
}
AbstractOperation.prototype.postfix = function() {
    return "(" + this.operations.map(op => op.postfix()).join(" ") + " " + this.strSign + ")";
}

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
    );
const SumCb = operationFabric((...args) => args.reduce((sum, x) => sum + Math.pow(x, 3), 0), "sumCb");
const Rmc =
    operationFabric(
    (...args) => Math.cbrt(args.reduce((sum, x) => sum + Math.pow(x, 3), 0) / args.length),
    "rmc"
    );
const MeanCb = operationFabric(
    (...args) => args.length === 0 ? 0 : args.reduce((sum, x) => sum + Math.pow(x, 3), 0) / args.length,
    "meanCb"
);

// parsing by typical walk on stack
const signMap = {
    "negate": [Negate, 1],
    "+": [Add, 2],
    "-": [Subtract, 2],
    "*": [Multiply, 2],
    "/": [Divide, 2],
    "clamp": [Clamp, 3],
    "softClamp": [SoftClamp, 4],
    "sumCb": [SumCb, -1],
    "rmc": [Rmc, -1],
    "meanCb": [MeanCb, -1]
}

//==========================================Reverse Poland task_7=======================================================

function parse(expression) {
    const tokens = expression.split(" ").filter(elem => elem.trim() !== "");
    const stack = [];
    let balanse = 0;

    for (const token of tokens) {
        if (token in signMap) {
            const [Class, amountArgs] = signMap[token];
            const args = [];
            for (let i = 0; i < amountArgs; i++) {
                args.push(stack.pop());
            }
            stack.push(new Class(...args.reverse()));
        } else if (token in varMap) {
            stack.push(new Variable(token));
        } else {
            stack.push(new Const(parseInt(token)));
        }
    }

    return stack[0];
}

//==========================================Straight Poland=======================================================

function ParsingError(message) {
    this.message = message;
}
ParsingError.prototype = Object.create(Error.prototype);
ParsingError.prototype.name = "ParsingError";
ParsingError.prototype.constructor = ParsingError;

const specMap = {
    "negate": Negate
}

const brackets = {
    "(": ")",
    "{": "}",
}

function parsePrefix(expression) {
    let index = 0;
    const tokens = expression.split(/([()\s])/g).filter(elem => elem.trim() !== "");

    function parseBrackets() {
        if (index >= tokens.length) {
            throw new ParsingError("unexpected eof");
        }
        let curArg = tokens[index++];
        if (curArg in brackets) {
            let sign = tokens[index++];
            if (!(sign in signMap)) {
                throw new ParsingError("unexpected sign");
            }
            let [Class, amountArgs] = signMap[sign];
            let args = [];

            if (amountArgs === -1) {
                while (index < tokens.length && tokens[index] !== brackets[curArg]) {
                    args.push(parseBrackets());
                }
            } else {
                for (let i = 0; i < amountArgs; i++) {
                    args.push(parseBrackets());
                }
            }

            if (index >= tokens.length || tokens[index] !== brackets[curArg]) {
                throw new ParsingError("expected: " + brackets[curArg] + " found " + tokens[index]);
            }
            index++;
            return new Class(...args);
        } else if (curArg in varMap) {
            return new Variable(curArg);
        } else if (curArg in specMap) {
            return new specMap[curArg](parseBrackets());
        } else {
            if (!isNaN(curArg)) {
                return new Const(Number(curArg));
            }
            throw new ParsingError('wrong number:' + curArg);
        }
    }

    let result = parseBrackets();
    if (index !== tokens.length) {
        throw new ParsingError("wrong end: " + tokens[index]);
    }
    return result;
}

//==========================================Reverse Poland=======================================================

// function parsePostfix(expression) {
//     const tokens = expression.split(/([()\s])/g).filter(elem => elem.trim() !== "");
//     if (tokens.length === 0) {
//         throw new ParsingError("empty input");
//     }
//     const stack = [];
//     let index = 0;
//
//     while (index < tokens.length) {
//         const token = tokens[index];
//         if (token in brackets) {
//             stack.push(token);
//         } else if (token in signMap) {
//             const [Class, amountArgs] = signMap[token];
//             const args = [];
//             let curLeft;
//
//             function putToken() {
//                 if (!(curLeft in brackets)) {
//                     args.push(curLeft);
//                 } else {
//                     throw new ParsingError("wrong amount of args for: " + token);
//                 }
//             }
//
//             if (amountArgs === -1) {
//                 curLeft = stack.pop();
//                 while (!(curLeft in brackets)) {
//                     putToken();
//                     if (stack.length === 0) {
//                         throw new ParsingError("expected open bracket");
//                     }
//                     curLeft = stack.pop()
//                 }
//                 stack.push(curLeft);
//             } else {
//                 for (let i = 0; i < amountArgs; i++) {
//                     curLeft = stack.pop();
//                     putToken();
//                 }
//             }
//
//             if (index >= tokens.length) {
//                 throw new ParsingError("expected closing bracket, found eof");
//             }
//             index++;
//             const close = tokens[index];
//             if (!Object.values(brackets).includes(close)) {
//                 throw new ParsingError("expected closing bracket, found: " + close);
//             }
//             const open = stack.pop();
//             if (brackets[open] !== close) {
//                 throw new ParsingError("wrong brackets pair for: " + close + " Found: " + open);
//             }
//             stack.push(new Class(...args.reverse()));
//         } else if (token in varMap) {
//             stack.push(new Variable(token));
//         } else {
//             if (isNaN(token)) {
//                 throw new ParsingError('expected number, var, open bracket, op. Found: ' + token);
//             }
//             stack.push(new Const(Number(token)));
//         }
//         index++;
//     }
//
//     if (stack.length !== 1) {
//         throw new ParsingError("excessive info");
//     }
//
//     return stack[0];
// }

// example
// let expr2 = parsePostfix("x sumCb");
// console.log(expr2.toString());
// console.log(expr2.prefix());
// console.log(expr2.postfix());
// console.log(expr2.evaluate(5, 4, 3));

