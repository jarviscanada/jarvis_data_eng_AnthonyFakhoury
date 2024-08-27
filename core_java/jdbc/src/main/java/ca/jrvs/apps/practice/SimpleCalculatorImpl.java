package ca.jrvs.apps.practice;

public class SimpleCalculatorImpl implements SimpleCalculator {

  @Override
  public int add(int x, int y) {
    return x+y;
  }

  @Override
  public int subtract(int x, int y) {
    return x-y;
  }

  @Override
  public int multiply(int x, int y) {
    return x*y;
  }

  @Override
  public double divide(int x, int y) {
    if (y == 0) {
      throw new ArithmeticException("Division by zero is not allowed.");
    }
    return (double) x/y;
  }

  @Override
  public int power(int x, int y) {
    return (int) Math.pow(x, y);
  }

  @Override
  public double abs(double x) {
    return Math.abs(x);
  }

}
