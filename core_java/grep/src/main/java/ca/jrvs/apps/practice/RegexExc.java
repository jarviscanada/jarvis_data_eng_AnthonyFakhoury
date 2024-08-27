package ca.jrvs.apps.practice;

public interface RegexExc {

  /**
   * return true if filename extension is jpg or jpeg (case-insensitive)
   * @param filename the name of the file to check
   * @return true if jpg/jpeg, false otherwise
   */
  boolean matchJpeg(String filename);

  /**
   * return true if ip is valid
   * to simplify the problem, IP address range is from 0.0.0.0 to 999.999.999.999
   * @param ip the ip to check for validity
   * @return true if ip is valid, false otherwise
   */
  boolean matchIp(String ip);

  /**
   * return true if line is empty (e.g. empty, white space, tabs, etc...)
   * @param line the line to check
   * @return true if the line is empty, false otherwise
   */
  boolean isEmptyLine(String line);
}