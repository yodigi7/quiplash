package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.entities.Round;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.repositories.GameRepository;
import com.yodigi.quiplash.utils.RepoUtil;
import com.yodigi.quiplash.utils.RetrieveQuestionsUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameMasterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RetrieveQuestionsUtil retrieveQuestionsUtil;
    @Mock
    private RepoUtil repoUtil;
    @Mock
    private GameRepository gameRepository;
    @InjectMocks
    private GameMasterController gameMasterController;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(gameMasterController)
                .build();
    }

    @Test
    public void givenValidGameId_whenCallingGetContenderNames_thenReturnCorrespondingContenderNames() throws Exception {
        List<Contender> contenders = new ArrayList<>();
        Contender contender = new Contender();
        contender.setId(1L);
        contender.setName("Anthony");
        contenders.add(contender);
        contender = new Contender();
        contender.setId(2L);
        contender.setName("Liz");
        contenders.add(contender);
        Game game = new Game();
        game.setId(1L);
        game.setContenders(contenders);
        Set<String> givenNames = new HashSet<>();
        givenNames.add("Anthony");
        givenNames.add("Liz");

        doReturn(game).when(repoUtil).findGameById(1L);

        mockMvc.perform(get("/game/1/contender-names").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"names\":[\"Anthony\", \"Liz\"]}"));
        Set<String> contenderNamesResponse = gameMasterController.getContenderNames(1L).getNames();

        assertEquals(givenNames, contenderNamesResponse);
        verify(repoUtil, times(2)).findGameById(1L);
    }

    @Test
    public void givenValidGameIdAndCurrentRoundIsZero_whenCallingStartGame_thenStartGameAndIncrementRoundCountAndUpdatePhase() throws Exception {
        Game game = new Game();
        game.setRound(0);
        game.setId(1L);
        List<Contender> contenders = new ArrayList<>();
        contenders.add(new Contender());
        game.setContenders(contenders);
        doReturn(game).when(repoUtil).findGameById(1L);
        Game game2 = new Game();
        game2.setRound(0);
        game2.setId(2L);
        game2.setContenders(contenders);
        doReturn(game).when(repoUtil).findGameById(1L);
        doReturn(new HashSet<>()).when(retrieveQuestionsUtil).getRandomQuestions(anyInt());
        doReturn(game2).when(repoUtil).findGameById(2L);

        mockMvc.perform(post("/game/1/start-game"))
                .andExpect(status().isOk());
        gameMasterController.startGame(2L);

        assertEquals("answering questions", game.getPhase());
        assertEquals("answering questions", game2.getPhase());
        assertEquals((Integer) 1, game.getRound());
        assertEquals((Integer) 1, game2.getRound());
        verify(gameRepository).save(game);
        verify(gameRepository).save(game2);
    }

    @Test
    public void givenValidGameIdAndCurrentRoundIsZero_whenCallingStartGame_thenGenerate1QuestionsPerContenderPerRound() throws Exception {
        Game game = new Game();
        game.setRound(0);
        game.setId(1L);
        List<Contender> contenders = new ArrayList<>();
        Contender contender = new Contender();
        contender.setId(1L);
        contenders.add(contender);
        contender = new Contender();
        contender.setId(2L);
        contenders.add(contender);
        contender = new Contender();
        contender.setId(3L);
        contenders.add(contender);
        game.setContenders(contenders);
        doReturn(game).when(repoUtil).findGameById(1L);
        Game game2 = new Game();
        game2.setRound(0);
        game2.setId(2L);
        game2.setContenders(contenders);
        doReturn(game).when(repoUtil).findGameById(1L);
        doReturn(new HashSet<>()).when(retrieveQuestionsUtil).getRandomQuestions(9);
        doReturn(game2).when(repoUtil).findGameById(2L);

        mockMvc.perform(post("/game/1/start-game"))
                .andExpect(status().isOk());
        gameMasterController.startGame(2L);

        verify(retrieveQuestionsUtil, times(2)).getRandomQuestions(9);
    }

    @Test
    public void givenValidGameId_whenCallingStartRound_thenIncrementRoundAndUpdatePhase() throws Exception {
        Game game = new Game();
        game.setRound(0);
        game.setId(1L);
        doReturn(game).when(repoUtil).findGameById(1L);
        Game game2 = new Game();
        game2.setRound(0);
        game2.setId(2L);
        doReturn(game).when(repoUtil).findGameById(1L);
        doReturn(new HashSet<>()).when(retrieveQuestionsUtil).getRandomQuestions(anyInt());
        doReturn(game2).when(repoUtil).findGameById(2L);

        mockMvc.perform(post("/game/1/start-round"))
                .andExpect(status().isOk());
        gameMasterController.startRound(2L);

        assertEquals("answering questions", game.getPhase());
        assertEquals("answering questions", game2.getPhase());
        assertEquals((Integer) 1, game.getRound());
        assertEquals((Integer) 1, game2.getRound());
        verify(gameRepository).save(game);
        verify(gameRepository).save(game2);
    }

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
