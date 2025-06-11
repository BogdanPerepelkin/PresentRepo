/*
Expression:
    ws Objects ws

Objects:
    ws Object ws

Object:
    First
    Second
    Third
    Significant
    Brackets
    UnaryMinus

Significant:
    Const
    Variable

parseSignificant:
    fair parsing of significant (Variable or Const)

parseSecondBin(left, type):
    tmp = parseSignificant()
    right = parseRightSecondBin(tmp)
    if (type == -): return new Subtract(left, right)
    else: return new Add(left, right)

parseRightSecondBin(arg):
    if take Multiply: return parseMultiply(arg)
    if take Add: return parseAdd
    if take Brackets: return parseBrackets()
    if take UnaryMinus: return parseUnaryMinus()
    if take Significant: return parseSignificant

parseFirstBin(left, type):
    tmp = parseSignificant()
    right = parseRightFirstBin(tmp)
    if (type == /): return new Divide(left, right)
    else: return new Multiply(left, right)

parseRightFirstBin(arg):
    if take Multiply: return parseMultiply(arg)
    if take Brackets: return parseBrackets()
    if take UnaryMinus: return parseUnaryMinus()
    if take Significant: return parseSignificant

parseUnary():
    if take



1 + 2 * 3
после "+" ожидаю хуйню, которая может быть Add или Multiply или другой ебанью


 */