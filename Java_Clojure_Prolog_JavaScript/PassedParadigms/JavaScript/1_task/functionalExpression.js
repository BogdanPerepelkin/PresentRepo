const evalAbs = (func) => (...operations) => (...args) => func(...operations.map(cur => cur(...args)));

const cnst = (a) => () => a;

const varMap = { "x": 0, "y": 1, "z": 2, "t": 3 };
function variable(name) {
    return (...args) =>
        args[varMap[name]];
}

// unary op
const negate = evalAbs((a) => -a);
const cosh = evalAbs(Math.cosh);
const sinh = evalAbs(Math.sinh);

// binary op
const add = evalAbs((a, b) => a + b);
const subtract = evalAbs((a, b) => a - b);
const multiply = evalAbs((a, b) => a * b);
const divide = evalAbs((a, b) => a / b);
const power = evalAbs(Math.pow);
const log = evalAbs((a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)));

// true constant
const phi = cnst((1 + Math.sqrt(5)) / 2);
const tau = cnst(2 * Math.PI);


// let testExpr = add(
//     subtract(
//         multiply(variable("x"), variable("x")),
//         multiply(cnst(2), variable("x")),
//     ),
//     cnst(1)
// );
//
// for (let i = 0; i < 10; i++) {
//     console.log(testExpr(i));
// }c
