package ca.jrvs.apps.practice;

import java.util.Iterator;
import java.util.stream.BaseStream;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class LambdaStreamExcImpTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private LambdaStreamExc testObj;

  @Before
  public void setUp() {
    System.setOut(new PrintStream(outContent));
    testObj = new LambdaStreamExcImp();
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void createStrStream() {
    Stream<String> expected = Stream.of("a", "b", "c");
    Stream<String> actual = testObj.createStrStream("a", "b", "c");
    assertStreamEquals(expected, actual);
  }

  @Test
  public void toUpperCase() {
    Stream<String> expected = Stream.of("A", "B", "C");
    Stream<String> actual = testObj.toUpperCase("a", "b", "c");
    assertStreamEquals(expected, actual);
  }

  @Test
  public void filter() {
    Stream<String> input = Stream.of("a", "b", "ac", "db", "ea");
    Stream<String> expected = Stream.of("b", "db");
    Stream<String> actual = testObj.filter(input, "a");
    assertStreamEquals(expected, actual);
  }

  @Test
  public void createIntStream() {
    IntStream expected = IntStream.of(1, 2, 3, 4, 7);
    IntStream actual = testObj.createIntStream(new int[]{1, 2, 3, 4, 7});
    assertBaseStreamEquals(expected, actual);
  }

  @Test
  public void toListStream() {
    List<String> expected = Arrays.asList("c", "b", "a");
    List<String> actual = testObj.toList(Stream.of("c", "b", "a"));
    assertEquals(expected, actual);
  }

  @Test
  public void toListBaseStream() {
    List<Integer> expected = Arrays.asList(1, 3, 5);
    List<Integer> actual = testObj.toList(IntStream.of(1, 3, 5));
    assertEquals(expected, actual);
  }

  @Test
  public void createIntStreamRange() {
    IntStream expected = IntStream.rangeClosed(3, 5);
    IntStream actual = testObj.createIntStream(3, 5);
    assertBaseStreamEquals(expected, actual);
  }

  @Test
  public void squareRootIntStream() {
    DoubleStream expected = DoubleStream.of(1.0, 2.0, 3.0, 4.0);
    DoubleStream actual = testObj.squareRootIntStream(IntStream.of(1, 4, 9, 16));
    assertBaseStreamEquals(expected, actual);
  }

  @Test
  public void getOdd() {
    IntStream expected = IntStream.of(1, 3, 5);
    IntStream actual = testObj.getOdd(IntStream.of(1, 2, 3, 4, 5, 6));
    assertBaseStreamEquals(expected, actual);
  }

  @Test
  public void getLambdaPrinter() {
    outContent.reset(); // Clear the output stream before the test
    String expected = "<test>";
    Consumer<String> printer = testObj.getLambdaPrinter("<", ">");
    printer.accept("test");
    assertEquals(expected, outContent.toString().trim());
  }

  @Test
  public void printMessages() {
    outContent.reset(); // Clear the output stream before the test
    String expected = "<test1>" + System.lineSeparator() + "<test2>";
    Consumer<String> printer = testObj.getLambdaPrinter("<", ">");
    testObj.printMessages(new String[]{"test1", "test2"}, printer);
    assertEquals(expected, outContent.toString().trim());
  }

  @Test
  public void printOdd() {
    outContent.reset(); // Clear the output stream before the test
    String expected = "<1>" + System.lineSeparator() + "<3>" + System.lineSeparator() + "<5>";
    Consumer<String> printer = testObj.getLambdaPrinter("<", ">");
    testObj.printOdd(IntStream.of(1, 2, 3, 4, 5, 6), printer);
    assertEquals(expected, outContent.toString().trim());
  }

  @Test
  public void flatNestedInt() {
    Stream<Integer> expected = Stream.of(1, 4, 9, 16);
    Stream<Integer> actual = testObj.flatNestedInt(Stream.of(Arrays.asList(1, 2), Arrays.asList(3, 4)));
    assertStreamEquals(expected, actual);
  }

  private static void assertStreamEquals(Stream<?> s1, Stream<?> s2) {
    Iterator<?> ite1 = s1.iterator();
    Iterator<?> ite2 = s2.iterator();
    while (ite1.hasNext() && ite2.hasNext()) {
      assertEquals(ite1.next(), ite2.next());
    }
    assertTrue(!ite1.hasNext() && !ite2.hasNext());
  }

  private static void assertBaseStreamEquals(BaseStream<?, ?> s1, BaseStream<?, ?> s2) {
    Iterator<?> ite1 = s1.iterator();
    Iterator<?> ite2 = s2.iterator();
    while (ite1.hasNext() && ite2.hasNext()) {
      assertEquals(ite1.next(), ite2.next());
    }
    assertTrue(!ite1.hasNext() && !ite2.hasNext());
  }
}
