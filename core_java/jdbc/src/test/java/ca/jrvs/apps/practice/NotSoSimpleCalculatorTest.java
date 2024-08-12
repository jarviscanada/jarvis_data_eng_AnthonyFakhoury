package ca.jrvs.apps.practice;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NotSoSimpleCalculatorTest {

  NotSoSimpleCalculator calc;

  @Mock
  SimpleCalculator mockSimpleCalc;

  @Before
  public void init() {
    calc = new NotSoSimpleCalculatorImpl(mockSimpleCalc);

    // Lenient stubbing to avoid UnnecessaryStubbingException
    lenient().when(mockSimpleCalc.power(2, 3)).thenReturn(8);
    lenient().when(mockSimpleCalc.abs(-10)).thenReturn(10.0);
  }

  @Test
  public void test_power() {
    // Arrange
    int base = 2;
    int exponent = 3;
    int expected = 8;

    // Act
    int actual = calc.power(base, exponent);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  public void test_abs() {
    // Arrange
    int input = -10;
    int expected = 10;

    // Act
    int actual = calc.abs(input);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  public void test_sqrt() {
    // Arrange
    double input = 16.0;
    double expected = 4.0;

    // Act
    double actual = calc.sqrt((int) input);

    // Assert
    assertEquals(expected, actual, 0.001); // Adding a delta for floating-point comparison
  }
}
