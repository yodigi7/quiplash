package com.yodigi.quiplash.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RetrieveQuestionsUtil {
    Random random = new Random();

    Logger LOGGER = LoggerFactory.getLogger(RetrieveQuestionsUtil.class);

    public Set<String> getRandomQuestions(Integer num) {
        Set<String> returnList = new HashSet<>();
        while (returnList.size() < num) {
            returnList.add(RandomStringUtils.randomAlphabetic(10));
        }
        LOGGER.info("Generated questions:");
        for (String question: returnList) {
            LOGGER.info(question);
        }
        return returnList;
    }

}
