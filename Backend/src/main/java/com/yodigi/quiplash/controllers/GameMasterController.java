package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.entities.Round;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.repositories.ContenderRepository;
import com.yodigi.quiplash.repositories.GameRepository;
import com.yodigi.quiplash.repositories.QuestionAnswerRepository;
import com.yodigi.quiplash.repositories.RoundRepository;
import com.yodigi.quiplash.utils.RepoUtil;
import com.yodigi.quiplash.utils.GeneralUtil;
import com.yodigi.quiplash.utils.RetrieveQuestionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class GameMasterController {

    Logger LOGGER = LoggerFactory.getLogger(GameMasterController.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private ContenderRepository contenderRepository;

    @Autowired
    private RepoUtil repoUtil;

    @Autowired
    private RetrieveQuestionsUtil retrieveQuestionsUtil;

    @Autowired
    private GeneralUtil generalUtil;

    private static final int MAX_ROUNDS = 3;

    @RequestMapping("/game/{gameId}/get-contender-names")
    public Set<String> getContenderNames(@PathVariable Long gameId) throws InvalidGameIdException {
        Set<String> contenderNames = new HashSet<>();
        for (Contender contender: repoUtil.findGameById(gameId).getContenders()) {
            contenderNames.add(contender.getName());
        }
        return contenderNames;
    }

    @RequestMapping("/game/{gameId}/start-game")
    public void startGame(@PathVariable Long gameId) throws InvalidGameIdException {
        LOGGER.info("Starting game: " + gameId);
        Game game = repoUtil.findGameById(gameId);
        Integer currentRound = game.getRound();
        if (currentRound == 0 || currentRound == null) {
            LOGGER.debug("Starting first round");
            Set<String> questions = retrieveQuestionsUtil
                    .getRandomQuestions(game.getContenders().size() * MAX_ROUNDS / 2);
            LOGGER.info("Requested " + game.getContenders().size() * MAX_ROUNDS / 2 + " questions and got " + questions.size());
            setQuestions(game, questions);
            game.setRound(1);
            game.setPhase("answering questions");
            gameRepository.save(game);
            return;
        }
        LOGGER.info("Tried to start game but already started..., current round: " + currentRound);
    }

    @RequestMapping("/game/{gameId}/start-round")
    public Integer startRound(@PathVariable Long gameId) throws InvalidGameIdException {
        Game game = repoUtil.findGameById(gameId);
        Integer currentRound = game.getRound();
        Integer nextRound = currentRound + 1;
        game.setRound(nextRound);
        game.setPhase("answering questions");
        gameRepository.save(game);
        return nextRound;
    }

    @RequestMapping("/game/{gameId}/start-voting")
    public void startVoting(@PathVariable Long gameId) throws Exception {
        Game game = repoUtil.findGameById(gameId);
        if (game.getPhase().equals("answering questions")){
            game.setPhase("voting");
        }
        gameRepository.save(game);
    }

    @RequestMapping("/game/{gameId}/get-question-votes")
    public List<QuestionAnswer> getQuestionVotes(@PathVariable Long gameId) throws InvalidGameIdException {
        Game game = repoUtil.findGameById(gameId);
        game.clearCurrentQuestionAnswers();
        game.setPhase("showing votes");
        gameRepository.save(game);
        return game.getCurrentQuestionAnswers();
    }

    @RequestMapping("/game/{gameId}/set-next-to-score")
    public void setNextToScore(@PathVariable Long gameId) throws Exception {
        Game game = repoUtil.findGameById(gameId);
        Set<QuestionAnswer> questionAnswers = generalUtil.getRoundByRoundNum(game.getRound(), game.getRounds())
                .getQuestionAnswers();

        // Filter out questionAnswers that have already been scored
        List<QuestionAnswer> questionAnswerList = new ArrayList<>();
        for(QuestionAnswer questionAnswer: questionAnswers) {
            if (questionAnswer.getScore().equals(null)) {
                questionAnswerList.add(questionAnswer);
            }
        }

        if (questionAnswerList.isEmpty()) {
            return;
        }

        Random rand = new Random();
        QuestionAnswer chosenQuestionAnswer = questionAnswerList.get(
                rand.nextInt(questionAnswerList.size()));

        // Find other matching answer
        Set<QuestionAnswer> chosenQuestionAnswers = new HashSet<>();
        chosenQuestionAnswers.add(chosenQuestionAnswer);
        for (QuestionAnswer questionAnswer: questionAnswerList) {
            if (questionAnswer.getQuestion().equals(chosenQuestionAnswer.getQuestion()) &&
                    !questionAnswer.getId().equals(chosenQuestionAnswer.getId())) {
                chosenQuestionAnswers.add(questionAnswer);
            }
        }
        game.setCurrentQuestionAnswers(chosenQuestionAnswers);
        gameRepository.save(game);
    }

    @RequestMapping("/game/{gameId}/final-results")
    public Set<Contender> getFinalResults(@PathVariable Long gameId) throws InvalidGameIdException {
        return repoUtil.findGameById(gameId).getContenders();
    }

    void setQuestions(Game game, Set<String> questions) {
        Set<Round> rounds = new HashSet<>();
        List<Set<String>> splitQuestions = splitQuestions(questions);
        Set<Contender> contenders = game.getContenders();
        for (int i = 1; i <= MAX_ROUNDS; i++) {
            Round round = new Round(i);
            round.setQuestionAnswers(
                    getQuestionAnswers(
                            splitQuestions.get(i - 1), contenders, round));
            round.setGame(game);
            rounds.add(round);
        }
        game.setRounds(rounds);
    }

    List<Set<String>> splitQuestions(Set<String> inpQuestions) {
        Random rand = new Random();
        List<Set<String>> splitQuestions = new ArrayList<>();
        List<String> questions = new ArrayList<>();
        questions.addAll(inpQuestions);

        for (int i = 0; i < MAX_ROUNDS; i++) {
            Set<String> roundQuestions = new HashSet<>();
            for (int j = 0; j < inpQuestions.size() / MAX_ROUNDS; j++) {
                int index = rand.nextInt(questions.size());
                LOGGER.debug("Random index: " + index);
                roundQuestions.add(
                        questions.get(index));
                LOGGER.debug("Added question: " + questions.get(index) + " to round " + i);
                questions.remove(index);
                LOGGER.debug("List after removing question: " + questions.toString());
            }
            splitQuestions.add(roundQuestions);
        }
        return splitQuestions;
    }

    Set<QuestionAnswer> getQuestionAnswers(Set<String> questions, Set<Contender> contenders, Round round) {
        Random rand = new Random();
        Set<QuestionAnswer> questionAnswers = new HashSet<>();
        List<Contender> contendersQuestion1 = new ArrayList<>();
        List<Contender> contendersQuestion2 = new ArrayList<>();
        contendersQuestion1.addAll(contenders);
        contendersQuestion2.addAll(contenders);

        for (String question: questions){
            QuestionAnswer questionAnswer1 = new QuestionAnswer();
            QuestionAnswer questionAnswer2 = new QuestionAnswer();
            questionAnswer1.setQuestion(question);
            questionAnswer1.setRound(round);
            if (!contendersQuestion1.isEmpty()) {
                questionAnswer1.setContender(
                        contendersQuestion1.get(
                                rand.nextInt(contendersQuestion1.size())));
            } else {
                questionAnswer1.setContender(
                        contendersQuestion2.get(
                                rand.nextInt(contendersQuestion2.size())));
            }
            if (!contendersQuestion1.isEmpty()) {
                questionAnswer2.setContender(
                        contendersQuestion1.get(
                                rand.nextInt(contendersQuestion1.size())));
            } else {
                questionAnswer2.setContender(
                        contendersQuestion2.get(
                                rand.nextInt(contendersQuestion2.size())));
            }
            if (questionAnswer1.getContender().equals(questionAnswer2.getContender())) {
                return getQuestionAnswers(questions, contenders, round);
            }
            LOGGER.debug("Contenders " + questionAnswer1.getContender().getName() + " and " +
                    questionAnswer2.getContender().getName() + " got question: " + questionAnswer1.getQuestion());
            questionAnswers.add(questionAnswer1);
            questionAnswers.add(questionAnswer2);
        }
        return questionAnswers;
    }
}
