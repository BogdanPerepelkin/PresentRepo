package expression.generic.genericParser.types;

import expression.generic.genericParser.NumberInterface;

import java.math.BigInteger;

public class GenDouble implements NumberInterface<Double> {

    @Override
    public Double add(Double left, Double right) {
        return left + right;
    }

    @Override
    public Double sub(Double left, Double right) {
        return left - right;
    }

    @Override
    public Double mult(Double left, Double right) {
        return left * right;
    }

    @Override
    public Double divide(Double left, Double right) {
        return left / right;
    }

    @Override
    public Double area(Double left, Double right) {
        return left * right / 2;
    }

    @Override
    public Double perimeter(Double left, Double right) {
        return (left + right) * 2;
    }

    @Override
    public Double str2Num(String s) {
        return Double.parseDouble(s);
    }

    @Override
    public Double unaryMinus(Double arg) {
        return (-1) * arg;
    }

    @Override
    public Double sqrt(Double arg) {
        return Math.sqrt(arg);
    }

    @Override
    public Double int2Type(Integer arg) {
        return Double.valueOf(arg);
    }
}