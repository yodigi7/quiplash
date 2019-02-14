package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.utils.RepoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class SharedController {

    @Autowired
    private RepoUtil repoUtil;

    @RequestMapping("/game/{gameId}/get-round")
    public Integer getRound(@PathVariable Long gameId) throws InvalidGameIdException {
        return repoUtil.findGameById(gameId).getRound();
    }

    @RequestMapping("/game/{gameId}/get-phase")
    public String getPhase(@PathVariable Long gameId) throws InvalidGameIdException {
        return repoUtil.findGameById(gameId).getPhase();
    }

    @RequestMapping("/game/{gameId}/questions-to-score")
    public List<QuestionAnswer> getQuestionAnswerToScore(@PathVariable Long gameId) throws InvalidGameIdException {
        return repoUtil.findGameById(gameId).getCurrentQuestionAnswers();
    }
}
