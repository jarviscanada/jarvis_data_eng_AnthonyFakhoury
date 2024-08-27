package ca.jrvs.apps.grep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JavaGrepLambdaImp extends JavaGrepImp {

  final Logger logger = LoggerFactory.getLogger(JavaGrepLambdaImp.class);

  @Override
  public List<File> listFiles(String rootDir) {
    Path rootPath = Paths.get(rootDir);

    try (Stream<Path> fileStream = Files.walk(rootPath)) {
      return fileStream.filter(file -> !Files.isDirectory(file))
          .map(Path::toFile).collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Failed to list files from directory: {}", rootDir, e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<String> readLines(File inputFile) throws IllegalAccessException {
    if (inputFile.toString().endsWith(".class")) {
      return super.readLines(inputFile);
    }
    if (!inputFile.isFile()) {
      throw new IllegalArgumentException("The provided file path does not denote a file");
    }

    try (Stream<String> lines = Files.lines(inputFile.toPath())) {
      return lines.collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Failed to read lines from file: {}", inputFile, e);
      throw new RuntimeException("Failed to read lines from file: " + inputFile, e);
    }
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    try (FileOutputStream fileOutputStream = new FileOutputStream(
        new File(getOutFile())); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
        fileOutputStream,
        StandardCharsets.UTF_8); BufferedWriter bufferedWriter = new BufferedWriter(
        outputStreamWriter)) {
      lines.forEach(line -> {
        try {
          bufferedWriter.write(line);
          bufferedWriter.newLine();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (IOException e) {
      logger.error("Failed to write to file", e);
    }
  }

  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
    }

    //Use default logger config
    BasicConfigurator.configure();

    JavaGrepLambdaImp javaGrepLambdaImp = new JavaGrepLambdaImp();
    javaGrepLambdaImp.setRegex(args[0]);
    javaGrepLambdaImp.setRootPath(args[1]);
    javaGrepLambdaImp.setOutFile(args[2]);

    try {
      javaGrepLambdaImp.process();
    } catch (Exception ex){
      javaGrepLambdaImp.logger.error("Error: Unable to process", ex);
    }
  }
}