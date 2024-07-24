package ca.jrvs.apps.grep;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.BasicConfigurator;

public class JavaGrepImp implements JavaGrep{
  final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

  private String regex;
  private String rootPath;
  private String outFile;

  @Override
  public String getRegex() {
    return regex;
  }

  @Override
  public void setRegex(String regex) {
    this.regex = regex;
  }

  @Override
  public String getRootPath() {
    return rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String getOutFile() {
    return outFile;
  }

  @Override
  public void setOutFile(String outFile) {
    this.outFile = outFile;
  }

  @Override
  public void process() throws IOException, IllegalAccessException {
    List<String> matchedLines = new ArrayList<>();
    for (File file : listFiles(rootPath)){
      for (String line : readLines(file)){
        if (containsPattern(line)){
          matchedLines.add(line);
        }
      }
    }
    writeToFile(matchedLines);
  }

  @Override
  public List<File> listFiles(String rootDir) {
    List<File> fileList = new ArrayList<>();
    traverseDirectory(new File(rootDir), fileList);
    return fileList;
  }

  private void traverseDirectory(File dir, List<File> fileList) {
    if (dir.isDirectory()) {
      File[] files = dir.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isFile()) {
            fileList.add(file);
          } else if (file.isDirectory()) {
            traverseDirectory(file, fileList);
          }
        }
      }
    }
  }

  @Override
  public List<String> readLines(File inputFile) throws IllegalAccessException {
    if (!inputFile.isFile()) {
      throw new IllegalAccessException("The provided input is not a file.");
    }

    List<String> lines = new ArrayList<>();
    BufferedReader bufferedReader = null;
    try {
      FileReader fileReader = new FileReader(inputFile);
      bufferedReader = new BufferedReader(fileReader);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        lines.add(line);
      }
    } catch (IOException e) {
      logger.error("Failed to read lines from file: " + inputFile, e);
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          logger.error("Failed to close reader", e);
        }
      }
    }
    return lines;
  }

  @Override
  public boolean containsPattern(String line) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(line);
    return matcher.find();
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    FileOutputStream fileOutputStream = null;
    OutputStreamWriter outputStreamWriter = null;
    BufferedWriter bufferedWriter = null;
    try {
      fileOutputStream = new FileOutputStream(new File(getOutFile()));
      outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
      bufferedWriter = new BufferedWriter(outputStreamWriter);
      for (String line : lines){
        bufferedWriter.write(line);
        bufferedWriter.newLine();
      }
    } catch (IOException e) {
      logger.error("Failed to write to file", e);
    } finally {
      if (bufferedWriter != null){
        bufferedWriter.close();
      }
      if (outputStreamWriter != null){
        outputStreamWriter.close();
      }
      if (fileOutputStream != null){
        fileOutputStream.close();
      }
    }
  }

  public static void main(String[] args) throws IllegalAccessException {
    if (args.length != 3){
      throw new IllegalAccessException("USAGE: JavaGrep regex rootPath outFile");
    }

    //Use Default logger config
    BasicConfigurator.configure();

    JavaGrepImp javaGrepImp = new JavaGrepImp();
    javaGrepImp.setRegex(args[0]);
    javaGrepImp.setRootPath(args[1]);
    javaGrepImp.setOutFile(args[2]);

    try {
      javaGrepImp.process();
    } catch (Exception ex) {
      javaGrepImp.logger.error("Error: Unable to process", ex);
    }
  }
}
