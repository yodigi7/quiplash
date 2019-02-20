package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.dto.QuestionsResponse;
import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.entities.Round;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.repositories.QuestionAnswerRepository;
import com.yodigi.quiplash.utils.RepoUtil;
import org.apache.catalina.filters.CorsFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContenderControllerTest {

    private MockMvc mockMvc;

    @Mock
    RepoUtil repoUtil;

    @Mock
    QuestionAnswerRepository questionAnswerRepository;

    @InjectMocks
    private ContenderController contenderControllerMock;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(contenderControllerMock)
                .build();
    }

    @Test
    public void givenValidGameIdAndName_whenCallingRestGetQuestions_thenReturnCorrespondingQuestions() throws Exception {
        Game game = new Game();
        game.setId(1L);
        game.setRound(1);
        Round round = new Round();
        Set<Contender> contenders = new HashSet<>();
        Set<QuestionAnswer> expectedQuestionAnswers = new HashSet<>();
        List<QuestionAnswer> allQuestionAnswers = new ArrayList<>();
        for (long i = 0; i < 5; i++) {
            Contender contender;
            if (i == 4) {
                contender = new Contender(game, "test");
            } else {
                contender = new Contender(game, Long.toString(i));
            }
            contender.setScore(i);
            Set<QuestionAnswer> questionAnswers = new HashSet<>();
            for (long j = 0; j < 20; j++) {
                QuestionAnswer questionAnswer = new QuestionAnswer();
                questionAnswer.setId(j);
                questionAnswer.setQuestion("test");
                questionAnswer.setContender(contender);
                questionAnswer.setScore(1);
                questionAnswers.add(questionAnswer);
            }
            if (i == 4) {
                expectedQuestionAnswers.addAll(questionAnswers);
            }
            allQuestionAnswers.addAll(questionAnswers);
            contender.setQuestionAndAnswers(questionAnswers);
        }
        round.setQuestionAnswers(allQuestionAnswers);
        round.setRoundNumber(1);
        round.setGame(game);
        List<Round> rounds = new ArrayList<>();
        rounds.add(round);
        game.setRounds(rounds);
        game.setContenders(contenders);

        doReturn(game).when(repoUtil).findGameById(1L);
        doReturn(game).when(repoUtil).findGameById(1L);

        // TODO: Add ids to the return json
        mockMvc.perform(get("/game/1/name/test/questions"))
                .andExpect(status().isOk())
                .andExpect(content().json("{questions: [{question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}, {question: test, score: 1}]}"));

        QuestionsResponse questionsResponse = contenderControllerMock.restGetQuestions(1L, "test");

        assertEquals(expectedQuestionAnswers, questionsResponse.getQuestions());
    }
}
