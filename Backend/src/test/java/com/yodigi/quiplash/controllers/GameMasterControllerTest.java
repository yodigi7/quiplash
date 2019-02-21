package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.SpringConfig;
import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.entities.Round;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = SpringConfig.class)
@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
public class GameMasterControllerTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameMasterController gameMasterController;

    @Test
    public void setQuestions_returnsValidSetup() {
        Game game = new Game();
        Set<String> questions = new HashSet<>();
        questions.add("question1");
        questions.add("question2");
        questions.add("question3");
        List<Contender> contenders = new ArrayList<>();
        contenders.add(new Contender(game, "Anthony"));
        contenders.add(new Contender(game, "Liz"));
        game.setContenders(contenders);

        gameMasterController.setQuestions(game, questions);

        for (Round round : game.getRounds()) {
            for (QuestionAnswer questionAnswer : round.getQuestionAnswers()) {
                assertNotNull(questionAnswer.getContender());
                assertNotNull(questionAnswer.getQuestion());
            }
        }
    }

    @Test
    public void getQuestionAnswers_returnsValidQuestionAnswers() {
        Round round = new Round();
        Game game = new Game();
        Set<Contender> contenders = new HashSet<>();
        contenders.add(new Contender(game, "Anthony"));
        contenders.add(new Contender(game, "Liz"));
        Set<String> questions = new HashSet<>();
        questions.add("question1");
        questions.add("question2");
        questions.add("question3");

        Set<QuestionAnswer> questionAnswers = gameMasterController.getQuestionAnswers(questions, contenders, round);

        for (QuestionAnswer questionAnswer : questionAnswers) {
            assertNotNull(questionAnswer.getQuestion());
            assertNotNull(questionAnswer.getContender());
        }
    }

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
