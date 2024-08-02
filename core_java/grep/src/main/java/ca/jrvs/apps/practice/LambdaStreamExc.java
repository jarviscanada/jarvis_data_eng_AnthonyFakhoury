package ca.jrvs.apps.practice;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface LambdaStreamExc {

  /**
   * Create a String stream from array
   *
   * note: arbitrary number of value will be stored in an array
   *
   * @param strings an array of strings
   * @return a stream of the strings
   */
  Stream<String> createStrStream(String ... strings);


  /**
   * Convert all strings to uppercase
   * please use createStrStream
   *
   * @param strings an array of strings
   * @return a stream of strings where all strings are uppercase
   */
  Stream<String> toUpperCase(String ... strings);

  /**
   * filter strings that contains the pattern
   * e.g.
   * filter(stringStream, "a") will return another stream where no element contains a
   *
   *
   * @param stringStream a stream of strings
   * @param pattern the pattern to be filtered out
   * @return a filtered stream
   */
  Stream<String> filter(Stream<String> stringStream, String pattern);

  /**
   * Create a intStream from a arr[]
   * @param arr an array of integers
   * @return an intStream created from arr
   */
  IntStream createIntStream(int[] arr);

  /**
   * Convert a stream to list
   *
   * @param stream a stream
   * @param <E> the type of list to be returned
   * @return a list made from the stream
   */
  <E> List<E> toList(Stream<E> stream);

  /**
   * Convert a intStream to list
   *
   * @param intStream a stream of integers
   * @return a list of integers
   */
  List<Integer> toList(IntStream intStream);

  /**
   * Create a IntStream ranging from start to end inclusive
   * @param start the integer where the stream starts
   * @param end the integer where the stream ends
   * @return an IntStream ranging from start to end inclusive
   */
  IntStream createIntStream(int start, int end);

  /**
   * Convert a intStream to a doubleStream
   * and compute square root of each element
   * @param intStream a stream of integers
   * @return a stream of doubles consisting of every integer square rooted
   */
  DoubleStream squareRootIntStream(IntStream intStream);


  /**
   * filter all even number and return odd numbers from a intStream
   * @param intStream a stream of integers
   * @return a stream of integers filtered to contain only odd numbers
   */
  IntStream getOdd(IntStream intStream);

  /**
   * Return a lambda function that print a message with a prefix and suffix
   * This lambda can be useful to format logs
   *
   * You will learn:
   *   - functional interface http://bit.ly/2pTXRwM & http://bit.ly/33onFig
   *   - lambda syntax
   *
   * e.g.
   * LambdaStreamExc lse = new LambdaStreamImp();
   * Consumer<String> printer = lse.getLambdaPrinter("start>", "<end");
   * printer.accept("Message body");
   *
   * sout:
   * start>Message body<end
   *
   * @param prefix prefix str
   * @param suffix suffix str
   * @return a Lambda function that accepts a body message and prints it surrounded
   * by a prefix and a suffix
   */
  Consumer<String> getLambdaPrinter(String prefix, String suffix);

  /**
   * Print each message with a given printer
   * Please use `getLambdaPrinter` method
   *
   * e.g.
   * String[] messages = {"a","b", "c"};
   * lse.printMessages(messages, lse.getLambdaPrinter("msg:", "!"));
   *
   * sout:
   * msg:a!
   * msg:b!
   * msg:c!
   *
   * @param messages an array of string messages
   * @param printer the printer used to print the messages
   */
  void printMessages(String[] messages, Consumer<String> printer);

  /**
   * Print all odd numbers from a intStream.
   * Please use `createIntStream` and `getLambdaPrinter` methods
   *
   * e.g.
   * lse.printOdd(lse.createIntStream(0, 5), lse.getLambdaPrinter("odd number:", "!"));
   *
   * sout:
   * odd number:1!
   * odd number:3!
   * odd number:5!
   *
   * @param intStream a stream of integers
   * @param printer the printer used to print the odd integers
   */
  void printOdd(IntStream intStream, Consumer<String> printer);

  /**
   * Square each number from the input.
   * Please write two solutions and compare difference
   *   - using flatMap
   *
   * @param ints a stream of integer lists
   * @return a stream of integers where each integer from the ints stream is squared
   */
  Stream<Integer> flatNestedInt(Stream<List<Integer>> ints);

}