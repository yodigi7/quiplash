package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.dto.PhaseResponse;
import com.yodigi.quiplash.dto.QuestionToScoreResponse;
import com.yodigi.quiplash.dto.RoundResponse;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.utils.RepoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SharedController {

    Logger LOGGER = LoggerFactory.getLogger(SharedController.class);

    @Autowired
    private RepoUtil repoUtil;

    @RequestMapping(value = "/game/{gameId}/round-number", method = RequestMethod.GET)
    public @ResponseBody RoundResponse getRound(@PathVariable Long gameId) throws InvalidGameIdException {
        return new RoundResponse(repoUtil.findGameById(gameId).getRound());
    }

    @RequestMapping(value = "/game/{gameId}/phase", method = RequestMethod.GET)
    public @ResponseBody PhaseResponse getPhase(@PathVariable Long gameId) throws InvalidGameIdException {
        return new PhaseResponse(repoUtil.findGameById(gameId).getPhase());
    }

    @RequestMapping(value = "/game/{gameId}/question-to-score", method = RequestMethod.GET)
    public @ResponseBody QuestionToScoreResponse getQuestionAnswerToScore(@PathVariable Long gameId) throws InvalidGameIdException {
        List<QuestionAnswer> questionAnswerList = repoUtil.findGameById(gameId).getCurrentQuestionAnswers();
        LOGGER.debug(String.format("Returning %d question answers.", questionAnswerList.size()));
        return new QuestionToScoreResponse(questionAnswerList);
    }
}
