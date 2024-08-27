# README

## Introduction
This project implements a `grep`-like tool in Java. The application recursively searches through files in a specified directory, identifies lines that match a given regular expression, and writes the matched lines to an output file. Two implementations are provided: one using traditional Java I/O and another leveraging Java 8 Streams and lambdas. Technologies used include core Java, SLF4J for logging, Maven for project management, and Docker for containerization.

## Quick Start
To use the application:

1. Compile and package the application using Maven:
   ```bash
   mvn clean package
   ```

2. Run the application:
   ```bash
   java -jar target/grep-1.0-SNAPSHOT.jar "regex" "rootPath" "outFile"
   ```

3. To use the Dockerized version:
   ```bash
   docker build -t your_docker_id/grep .
   docker run --rm -v `pwd`/data:/data -v `pwd`/log:/log your_docker_id/grep "regex" "/data" "/log/grep.out"
   ```

## Implementation
### Pseudocode
```plaintext
method process:
  initialize an empty list to hold matched lines
  for each file in the directory returned by listFiles(rootPath):
    for each line in the file returned by readLines(file):
      if the line matches the regex pattern:
        add the line to the list of matched lines
  write the matched lines to the output file using writeToFile(matchedLines)
```

### Performance Issue
The current implementation reads entire files into memory, which can lead to high memory usage for large files. This can be fixed by processing lines one at a time and writing matches directly to the output file to reduce memory overhead.

## Test
Manual testing was conducted by preparing sample data files and running various test cases to match different regex patterns. The output was compared against expected results to verify correctness. Sample commands were run, and the results were manually inspected for accuracy.

## Deployment
The application is Dockerized for easier distribution. A Dockerfile is used to create a Docker image containing the Java application. The image can be built and run using Docker commands, enabling consistent execution across different environments.

## Improvement
1. Implement unit tests to automate testing and ensure code correctness.
2. Optimize memory usage by processing files line by line instead of reading entire files into memory.
3. Add support for different file encodings to handle various text formats.