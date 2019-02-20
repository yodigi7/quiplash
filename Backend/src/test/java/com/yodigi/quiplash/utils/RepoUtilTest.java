package com.yodigi.quiplash.utils;

import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.repositories.ContenderRepository;
import com.yodigi.quiplash.repositories.GameRepository;
import com.yodigi.quiplash.repositories.QuestionAnswerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RepoUtilTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private QuestionAnswerRepository questionAnswerRepository;
    @Mock
    private ContenderRepository contenderRepository;

    @InjectMocks
    private RepoUtil repoUtil;
    
    @Test
    public void givenAValidGameId_whenCallingFindGameById_thenReturnsCorrectGame() throws InvalidGameIdException {
        Game game = new Game();
        game.setPhase("test phase");
        game.setId(1L);
        doReturn(Optional.of(game)).when(gameRepository).findById(1L);

        Game returnedGame = repoUtil.findGameById(1L);

        assertEquals(game, returnedGame);
    }

    @Test(expected = InvalidGameIdException.class)
    public void givenAnInvalidGameId_whenCallingFindGameById_thenThrowsInvalidGameIdException() throws InvalidGameIdException {
        repoUtil.findGameById(1L);
    }
    
    @Test
    public void givenValidQuestionAnswerId_whenCallingFindQuestionAnswerById_thenReturnsCorrectQuestionAnswer() throws Exception {
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setId(1L);
        questionAnswer.setScore(1);
        doReturn(Optional.of(questionAnswer)).when(questionAnswerRepository).findById(1L);

        QuestionAnswer returnedQuestionAnswer = repoUtil.findQuestionAnswerById(1L);

        assertEquals(questionAnswer, returnedQuestionAnswer);
    }

    @Test(expected = Exception.class)
    public void givenInvalidQuestionAnswerId_whenCallingFindQuestionAnswerById_thenThrowsException() throws Exception {
        repoUtil.findQuestionAnswerById(1L);
    }

    @Test
    public void givenValidName_whenCallingFindContenderByNameAndGame_thenReturnsCorrectContender() throws Exception {
        Contender contender = new Contender();
        contender.setName("contender name");
        contender.setId(1L);
        contender.setScore(1000L);
        Game game = new Game();
        game.setId(1L);
        doReturn(contender).when(contenderRepository).findByGameAndName(game, "name");

        Contender returnedContender = repoUtil.findContenderByNameAndGame("name", game);

        assertEquals(contender, returnedContender);
    }

    @Test(expected = Exception.class)
    public void givenInvalidName_whenCallingFindContenderByNameAndGame_thenThrowsException() throws Exception {
        repoUtil.findContenderByNameAndGame("name", new Game());
    }
}
