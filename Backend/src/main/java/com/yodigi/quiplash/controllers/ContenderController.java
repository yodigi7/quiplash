package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.dto.AnswerRequest;
import com.yodigi.quiplash.dto.QuestionsResponse;
import com.yodigi.quiplash.dto.VoteRequest;
import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.entities.Round;
import com.yodigi.quiplash.repositories.QuestionAnswerRepository;
import com.yodigi.quiplash.utils.GeneralUtil;
import com.yodigi.quiplash.utils.RepoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class ContenderController {

    private Logger LOGGER = LoggerFactory.getLogger(ContenderController.class);

    @Autowired
    private GeneralUtil generalUtil;

    @Autowired
    private RepoUtil repoUtil;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private GameMasterController gameMasterController;

    @RequestMapping(value = "/game/{gameId}/name/{name}/questions", method = RequestMethod.GET)
    public @ResponseBody QuestionsResponse restGetQuestions(@PathVariable Long gameId, @PathVariable String name) throws Exception {
        Round currentRound = getCurrentRound(
                repoUtil.findGameById(gameId));
        Set<QuestionAnswer> questionAnswers = getQuestions(currentRound, name);
        LOGGER.info(name + " from game: " + gameId + " requested questions and got " + questionAnswers.size() + " questions.");
        return new QuestionsResponse(questionAnswers);
    }

    @RequestMapping(value = "/game/{gameId}/name/{name}/answer", method = RequestMethod.POST)
    public void submitAnswer(@PathVariable String name,
                             @PathVariable Long gameId,
                             @RequestBody AnswerRequest answerRequest) throws Exception {
        Long questionAnswerId = answerRequest.getQuestionAnswerId();
        String answer = answerRequest.getAnswer();
        QuestionAnswer questionAnswer = repoUtil.findQuestionAnswerById(questionAnswerId);
        Game game = repoUtil.findGameById(gameId);
        Contender contender = repoUtil.findContenderByNameAndGame(name, game);
        if (!questionAnswer.getContender().getId().equals(contender.getId())) {
            throw new Exception("You tried to answer for someone else. That's against the rules...");
        }
        if (!game.getPhase().equals("answering questions")) {
            throw new Exception("Time for submitting answers is closed, sorry...");
        }

        questionAnswer.setAnswer(answer);
        questionAnswerRepository.save(questionAnswer);
        if (allQuestionsAnswered(generalUtil.getQuestionAnswers(game))) {
            gameMasterController.startVoting(gameId);
        }
    }

    @RequestMapping(value = "/game/{gameId}/name/{name}/vote", method = RequestMethod.POST)
    public void vote(@PathVariable String name,
                     @PathVariable Long gameId,
                     @RequestBody VoteRequest voteRequest
    ) throws Exception {
        Long questionAnswerId = voteRequest.getQuestionAnswerId();
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
//        if (questionAnswer.getContender().getName().equals(name)) {
//            throw new Exception("You can't vote for yourself...");
//        }
        LOGGER.info(String.format("%s voted for id: %d", name, questionAnswerId));
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

    boolean allQuestionsAnswered(Set<QuestionAnswer> questionAnswers) {
        for (QuestionAnswer questionAnswer : questionAnswers) {
            if (questionAnswer.getAnswer() == null) {
                return false;
            }
        }
        return true;
    }
}
