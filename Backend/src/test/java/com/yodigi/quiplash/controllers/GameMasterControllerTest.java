package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.dto.FinalResultsResponse;
import com.yodigi.quiplash.dto.QuestionVotesResponse;
import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.entities.Round;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.repositories.GameRepository;
import com.yodigi.quiplash.repositories.QuestionAnswerRepository;
import com.yodigi.quiplash.utils.GeneralUtil;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
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
    private GeneralUtil generalUtil;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private QuestionAnswerRepository questionAnswerRepository;
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
        contenders.add(new Contender());
        game.setContenders(contenders);
        doReturn(game).when(repoUtil).findGameById(1L);
        Game game2 = new Game();
        game2.setRound(0);
        game2.setId(2L);
        game2.setContenders(contenders);
        doReturn(game).when(repoUtil).findGameById(1L);
        doReturn(new HashSet<>()).when(retrieveQuestionsUtil).getRandomQuestions(6);
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
    public void givenThereAreMoreQuestionsToVoteOn_whenCallingStartVoting_thenUpdatePhaseAndUpdateCurrentQuestionAnswers() throws Exception {
        Round round = new Round();
        round.setRoundNumber(2);
        List<QuestionAnswer> fullQuestionAnswerList = new ArrayList<>();
        QuestionAnswer questionAnswer1 = new QuestionAnswer();
        questionAnswer1.setQuestion("temp");
        QuestionAnswer questionAnswer3 = new QuestionAnswer();
        questionAnswer3.setQuestion("temp");
        fullQuestionAnswerList.add(questionAnswer1);
        fullQuestionAnswerList.add(questionAnswer3);
        round.setQuestionAnswers(fullQuestionAnswerList);
        Game game = new Game();
        game.setRound(2);
        game.setId(1L);
        game.setRounds(Collections.singletonList(round));
        List<QuestionAnswer> originalQuestionAnswers1 = Collections.singletonList(new QuestionAnswer());
        game.setCurrentQuestionAnswers(originalQuestionAnswers1);

        Round round2 = new Round();
        Game game2 = new Game();
        game2.setRound(1);
        game2.setId(2L);
        round2.setRoundNumber(1);
        List<QuestionAnswer> fullQuestionAnswerList2 = new ArrayList<>();
        QuestionAnswer questionAnswer2 = new QuestionAnswer();
        questionAnswer2.setQuestion("test");
        QuestionAnswer questionAnswer4 = new QuestionAnswer();
        questionAnswer4.setQuestion("test");
        fullQuestionAnswerList2.add(questionAnswer2);
        fullQuestionAnswerList2.add(questionAnswer4);
        round2.setQuestionAnswers(fullQuestionAnswerList2);
        game2.setRounds(Collections.singletonList(round2));
        game2.setCurrentQuestionAnswers(Collections.singletonList(new QuestionAnswer()));

        doReturn(game).when(repoUtil).findGameById(1L);
        doReturn(game2).when(repoUtil).findGameById(2L);
        doReturn(round2).when(generalUtil).getRoundByRoundNum(eq(1), any());
        doReturn(round).when(generalUtil).getRoundByRoundNum(eq(2), any());

        mockMvc.perform(post("/game/1/start-voting"))
                .andExpect(status().isOk());
        gameMasterController.startVoting(2L);

        assertEquals("voting", game.getPhase());
        assertEquals("voting", game2.getPhase());
        assertTrue(game.getCurrentQuestionAnswers().stream().anyMatch(questionAnswer1::equals));
        assertTrue(game.getCurrentQuestionAnswers().stream().anyMatch(questionAnswer3::equals));
        assertTrue(game2.getCurrentQuestionAnswers().stream().anyMatch(questionAnswer2::equals));
        assertTrue(game2.getCurrentQuestionAnswers().stream().anyMatch(questionAnswer4::equals));
        verify(gameRepository).save(game);
        verify(gameRepository).save(game2);
    }

    @Test(expected = Exception.class)
    public void givenThereAreNoMoreQuestionsToVoteOn_whenCallingStartVoting_thenThrowExceptionAndDontSaveAnything() throws Exception {
        Round round = new Round();
        round.setRoundNumber(2);
        Game game = new Game();
        game.setRound(2);
        game.setId(1L);
        game.setRounds(Collections.singletonList(round));
        List<QuestionAnswer> originalQuestionAnswers1 = Collections.singletonList(new QuestionAnswer());
        game.setCurrentQuestionAnswers(originalQuestionAnswers1);

        Round round2 = new Round();
        Game game2 = new Game();
        game2.setRound(1);
        game2.setId(2L);
        round2.setRoundNumber(1);
        game2.setRounds(Collections.singletonList(round2));
        game2.setCurrentQuestionAnswers(Collections.singletonList(new QuestionAnswer()));

        doReturn(game).when(repoUtil).findGameById(1L);
        doReturn(game2).when(repoUtil).findGameById(2L);
        doReturn(round2).when(generalUtil).getRoundByRoundNum(eq(1), any());
        doReturn(round).when(generalUtil).getRoundByRoundNum(eq(2), any());

        mockMvc.perform(post("/game/1/start-voting"))
                .andExpect(status().isInternalServerError());
        gameMasterController.startVoting(2L);

        verify(gameRepository, times(0)).save(any());
        verify(questionAnswerRepository, times(0)).save(any());
    }

    @Test
    public void givenValidGameIdAndGameHasCurrentQuestionAnswer_whenCallingGetQuestionVotes_thenCurrentQuestionAnswerAreReturned() throws Exception {
        Game game = new Game();
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setId(1L);
        QuestionAnswer questionAnswer2 = new QuestionAnswer();
        questionAnswer2.setId(2L);
        List<QuestionAnswer> questionAnswerList = new ArrayList<>();
        questionAnswerList.add(questionAnswer);
        questionAnswerList.add(questionAnswer2);
        game.setCurrentQuestionAnswers(questionAnswerList);
        doReturn(game).when(repoUtil).findGameById(1L);

        mockMvc.perform(get("/game/1/question-votes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"questionAnswers\":[{\"id\":1}, {\"id\":2}]}"));
        QuestionVotesResponse  questionVotesResponse = gameMasterController.getQuestionVotes(1L);

        assertEquals(questionAnswerList, questionVotesResponse.getQuestionAnswers());
    }

    @Test
    public void givenValidGameIdAndGameHasContenders_whenCallingGetFinalResults_thenReturnAllOfTheContenders() throws Exception {
        Contender contender = new Contender();
        contender.setId(1L);
        contender.setName("contender name");
        Game game = new Game();
        game.setPhase("test");
        game.setContenders(Collections.singletonList(contender));
        Set<Contender> expectedContenders = new HashSet<>();
        expectedContenders.add(contender);
        doReturn(game).when(repoUtil).findGameById(1L);

        mockMvc.perform(get("/game/1/final-results").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"contenders\":[{\"id\":1, \"name\":\"contender name\"}]}"));
        FinalResultsResponse finalResultsResponse = gameMasterController.getFinalResults(1L);

        assertEquals(expectedContenders, finalResultsResponse.getContenders());
    }

    @Test
    public void givenValidGameId_whenCallingSet_thenCurrentQuestionAnswersAreUpdated() throws Exception {
        Game game = new Game();
        QuestionAnswer currentQuestionAnswer1 = new QuestionAnswer();
        QuestionAnswer currentQuestionAnswer2 = new QuestionAnswer();
        QuestionAnswer newQuestionAnswer1 = new QuestionAnswer();
        QuestionAnswer newQuestionAnswer2 = new QuestionAnswer();
        Round round = new Round();
        currentQuestionAnswer1.setGame(game);
        currentQuestionAnswer2.setGame(game);
        currentQuestionAnswer1.setScore(0);
        currentQuestionAnswer2.setScore(0);
        newQuestionAnswer1.setScore(null);
        newQuestionAnswer2.setScore(null);
        currentQuestionAnswer1.setId(1L);
        currentQuestionAnswer2.setId(2L);
        newQuestionAnswer1.setId(3L);
        newQuestionAnswer2.setId(4L);
        currentQuestionAnswer1.setQuestion("question1");
        currentQuestionAnswer2.setQuestion("question1");
        newQuestionAnswer1.setQuestion("question2");
        newQuestionAnswer2.setQuestion("question2");
        currentQuestionAnswer1.setRound(round);
        currentQuestionAnswer2.setRound(round);
        newQuestionAnswer1.setRound(round);
        newQuestionAnswer2.setRound(round);
        List<QuestionAnswer> expectedNewQuestionAnswers = new ArrayList<>();
        expectedNewQuestionAnswers.add(newQuestionAnswer1);
        expectedNewQuestionAnswers.add(newQuestionAnswer2);
        List<QuestionAnswer> currentQuestionAnswers = new ArrayList<>();
        currentQuestionAnswers.add(currentQuestionAnswer1);
        currentQuestionAnswers.add(currentQuestionAnswer2);
        List<QuestionAnswer> allQuestionAnswers = new ArrayList<>();
        allQuestionAnswers.add(currentQuestionAnswer1);
        allQuestionAnswers.add(currentQuestionAnswer2);
        allQuestionAnswers.add(newQuestionAnswer1);
        allQuestionAnswers.add(newQuestionAnswer2);
        round.setQuestionAnswers(allQuestionAnswers);
        game.setCurrentQuestionAnswers(currentQuestionAnswers);
        game.setRound(1);
        game.setId(1L);
        game.setRounds(Collections.singletonList(round));
        doReturn(game).when(repoUtil).findGameById(1L);
        doReturn(round).when(generalUtil).getRoundByRoundNum(anyInt(), any());

        gameMasterController.setNextToScore(1L);

        assertEquals(expectedNewQuestionAnswers.size(), game.getCurrentQuestionAnswers().size());
        assertTrue(expectedNewQuestionAnswers.contains(game.getCurrentQuestionAnswers().get(0)));
        assertTrue(expectedNewQuestionAnswers.contains(game.getCurrentQuestionAnswers().get(1)));
    }

    @Test(expected = Exception.class)
    public void givenOnlyOnePersonInGameId_whenCallingStartGame_thenThrowException() throws Exception {
        Game game = new Game();
        Set<Contender> contenders = new HashSet<>();
        contenders.add(new Contender());
        game.setContenders(contenders);
        doReturn(game).when(repoUtil).findGameById(1L);

        mockMvc.perform(post("/game/1/start-game"))
                .andExpect(status().isInternalServerError());
        gameMasterController.getFinalResults(1L);
    }

    // TODO: add test for checking that a group of 3+ will have questions evenly split each round

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
