package com.yodigi.quiplash.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RetrieveQuestionsUtilTest {

    @Autowired
    private RetrieveQuestionsUtil retrieveQuestionsUtil;

    @Test
    public void givenAPositiveNumber_whenCallingGetRandomQuestions_thenReturnThatManyUniqueQuestions() throws IOException {
        Set<String> questions = retrieveQuestionsUtil.getRandomQuestions(5);

        assertEquals(5, questions.size());
    }

    @Test
    public void givenANegativeNumberOrZero_whenCallingGetRandomQuestions_thenReturnAnEmptySet() throws IOException {
        assertEquals(new HashSet<>(), retrieveQuestionsUtil.getRandomQuestions(-20));
        assertEquals(new HashSet<>(), retrieveQuestionsUtil.getRandomQuestions(0));
    }

}
