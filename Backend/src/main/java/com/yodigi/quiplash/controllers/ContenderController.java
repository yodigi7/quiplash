package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.entities.Round;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.repositories.QuestionAnswerRepository;
import com.yodigi.quiplash.utils.RepoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class ContenderController {

    Logger LOGGER = LoggerFactory.getLogger(ContenderController.class);

    @Autowired
    private RepoUtil repoUtil;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @RequestMapping("/name/{name}/get-questions")
    public Set<QuestionAnswer> restGetQuestions(@RequestHeader Long gameId, @PathVariable String name) throws Exception {
        Round currentRound = getCurrentRound(
                repoUtil.findGameById(gameId));
        Set<QuestionAnswer> questionAnswers = getQuestions(currentRound, name);
        LOGGER.info(name + " from game: " + gameId + " requested questions and got " + questionAnswers.size() + " questions.");
        return questionAnswers;
    }

    @RequestMapping("/name/{name}/submit-answer")
    public void submitAnswer(@PathVariable String name,
                             @RequestHeader Long gameId,
                             @RequestHeader Long questionAnswerId,
                             @RequestBody String answer) throws Exception {
        QuestionAnswer questionAnswer = repoUtil.findQuestionAnswerById(questionAnswerId);
        Game game = repoUtil.findGameById(gameId);
        Contender contender = repoUtil.findContenderByNameAndGame(name, game);
        if (questionAnswer.getContender().getId().equals(contender.getId())) {
            throw new Exception("You tried to answer for someone else. That's against the rules...");
        }
        if (!game.getPhase().equals("submitting answers")) {
            throw new Exception("Time for submitting answers is closed, sorry...");
        }

        questionAnswer.setAnswer(answer);
        questionAnswerRepository.save(questionAnswer);
        //TODO: Do a check to see if everyone is done early
    }

    @RequestMapping("/name/{name}/vote")
    public void vote(@PathVariable String name,
                                 @RequestHeader Long gameId,
                                 @RequestHeader Long questionAnswerId
    ) throws Exception {
        Game game = repoUtil.findGameById(gameId);
        QuestionAnswer questionAnswer = repoUtil.findQuestionAnswerById(questionAnswerId);
        boolean validQuestionAnswerId = false;
        for (QuestionAnswer questionAnswerGame: game.getCurrentQuestionAnswers()) {
            if (questionAnswerGame.getId().equals(questionAnswerId)){
                validQuestionAnswerId = true;
                break;
            }
        }
        if (!validQuestionAnswerId) {
            throw new Exception("You tried to cheat, that isn't a current question to vote on!");
        }
        if (questionAnswer.getContender().getName().equals(name)) {
            throw new Exception("You can't vote for yourself...");
        }
        questionAnswer.incrementScore();
        questionAnswerRepository.save(questionAnswer);
        // TODO: Add check to see if everyone is done voting
    }

    private Set<QuestionAnswer> getQuestions(Round currentRound, String name) {
        return currentRound.getQuestionAnswers().stream()
                .filter(questionAnswer -> questionAnswer.getContender().getName().equals(name))
                .collect(Collectors.toSet());
    }

    private Round getCurrentRound(Game game) throws Exception {
        Integer currentRound = game.getRound();
        for(Round round: game.getRounds()) {
            if (round.getRoundNumber().equals(currentRound)) {
                return round;
            }
        }
        LOGGER.warn("Current round: " + currentRound);
        throw new Exception("This should never happen..., no more rounds left");
    }
}
