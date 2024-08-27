package ca.jrvs.apps.practice;

public class NotSoSimpleCalculatorImpl implements NotSoSimpleCalculator {

  private SimpleCalculator calc;

  public NotSoSimpleCalculatorImpl(SimpleCalculator calc) {
    this.calc = calc;
  }

  @Override
  public int power(int x, int y) {
    return (int) Math.pow(x, y);
  }

  @Override
  public int abs(int x) {
    return Math.abs(x);
  }

  @Override
  public double sqrt(int x) {
    return Math.sqrt(x);
  }

}