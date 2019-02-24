package com.yodigi.quiplash.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

import static java.util.Collections.sort;

public class RetrieveQuestionsUtil {

    private Logger LOGGER = LoggerFactory.getLogger(RetrieveQuestionsUtil.class);

    public Set<String> getRandomQuestions(Integer num) throws IOException {
        String filename = "pgQuestions.txt";
        Random random = new Random();
        ClassLoader classLoader = getClass().getClassLoader();
        int numberOfLines = numberOfLinesInFile(filename);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(classLoader.getResourceAsStream(filename))));
        Set<String> randomQuestions = new HashSet<>();
        Set<Integer> randomNumbersSet = new HashSet<>();
        while(randomNumbersSet.size() < num) {
            randomNumbersSet.add(random.nextInt(numberOfLines));
        }
        List<Integer> randomNumbersList = new ArrayList<>(randomNumbersSet);
        sort(randomNumbersList);
        int lineNumber = 0;
        while (randomNumbersList.size() > 0) {
            String line = reader.readLine();
            if (randomNumbersList.get(0).equals(lineNumber)) {
                randomQuestions.add(line);
                LOGGER.debug(String.format("Adding question: %s", line));
                randomNumbersList.remove(0);
            }
            lineNumber += 1;
        }

        return randomQuestions;
    }

    int numberOfLinesInFile(String fileName) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName))));
        int numberOfLines = 0;
        while (lineNumberReader.readLine() != null) {
            numberOfLines += 1;
        }
        return numberOfLines;
    }
}
