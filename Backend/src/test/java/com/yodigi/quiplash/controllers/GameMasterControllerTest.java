package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.SpringConfig;
import com.yodigi.quiplash.repositories.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = SpringConfig.class)
@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
public class GameMasterControllerTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameMasterController gameMasterController;

    @Test
    public void splitQuestions_returnsSameNumberOfQuestions() {
        Set<String> questions = new HashSet<>();
        questions.add("test");
        questions.add("test1");
        questions.add("test2");
        questions.add("test3");
        questions.add("test4");
        questions.add("test5");
        List<Set<String>> newQuestionSet = gameMasterController.splitQuestions(questions);
        int newCount = 0;
        for (Set<String> questionSet: newQuestionSet) {
            newCount += questionSet.size();
        }
        assertEquals(newCount, questions.size());
    }

}
