package expression.generic.genericParser.types;

import expression.exceptions.accum.*;
import expression.generic.genericParser.NumberInterface;

import java.math.BigInteger;

public class GenBigInteger implements NumberInterface<BigInteger> {

    @Override
    public BigInteger add(BigInteger left, BigInteger right) {
        return left.add(right);
    }

    @Override
    public BigInteger sub(BigInteger left, BigInteger right) {
        return left.subtract(right);
    }

    @Override
    public BigInteger mult(BigInteger left, BigInteger right) {
        return left.multiply(right);
    }

    @Override
    public BigInteger divide(BigInteger left, BigInteger right) {
        if (right.equals(BigInteger.ZERO)) {
            throw new DBZException(" in BigInteger");
        }
        return left.divide(right);
    }

    @Override
    public BigInteger area(BigInteger left, BigInteger right) {
        return left.multiply(right).divide(BigInteger.valueOf(2));
    }

    @Override
    public BigInteger perimeter(BigInteger left, BigInteger right) {
        return left.add(right).multiply(BigInteger.valueOf(2));
    }

    @Override
    public BigInteger str2Num(String s) {
        return new BigInteger(s);
    }

    @Override
    public BigInteger unaryMinus(BigInteger arg) {
        return arg.negate();
    }

    @Override
    public BigInteger sqrt(BigInteger arg) {
        return arg.sqrt();
    }

    @Override
    public BigInteger int2Type(Integer arg) {
        return BigInteger.valueOf(arg);
    }
}