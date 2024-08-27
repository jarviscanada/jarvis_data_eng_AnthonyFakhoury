package ca.jrvs.apps.practice;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleCalculatorTest {

  SimpleCalculator calculator;

  @Before
  public void init() {
    calculator = new SimpleCalculatorImpl();
  }

  @Test
  public void test_add() {
    int expected = 2;
    int actual = calculator.add(1, 1);
    assertEquals(expected, actual);
  }

  @Test
  public void test_subtract() {
    //write your test here
    int expected = 2;
    int actual = calculator.subtract(4, 2);
    assertEquals(expected, actual);
  }

  @Test
  public void test_multiply() {
    //write your test here
    int expected = 2;
    int actual = calculator.multiply(1, 2);
    assertEquals(expected, actual);
  }

  @Test
  public void test_divide() {
    //write your test here
    int expected = 2;
    int actual = (int) calculator.divide(8, 4);
    assertEquals(expected, actual);
  }

}
