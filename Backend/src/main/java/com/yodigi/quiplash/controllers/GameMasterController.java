package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.dto.*;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class GameMasterController {

    private Logger LOGGER = LoggerFactory.getLogger(GameMasterController.class);

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

    @RequestMapping(value = "/game/{gameId}/contender-names", method = RequestMethod.GET)
    public @ResponseBody ContenderNamesResponse getContenderNames(@PathVariable Long gameId) throws InvalidGameIdException {
        Set<String> contenderNames = new HashSet<>();
        for (Contender contender: repoUtil.findGameById(gameId).getContenders()) {
            contenderNames.add(contender.getName());
        }
        return new ContenderNamesResponse(contenderNames);
    }

    @RequestMapping(value = "/game/{gameId}/start-game", method = RequestMethod.POST)
    public void startGame(@PathVariable Long gameId) throws Exception {
        LOGGER.info("Starting game: " + gameId);
        Game game = repoUtil.findGameById(gameId);
        Integer currentRound = game.getRound();
        if (currentRound == 0) {
            LOGGER.debug("Starting first round");
            Set<String> questions = retrieveQuestionsUtil
                    .getRandomQuestions(game.getContenders().size() * MAX_ROUNDS);
            LOGGER.info("Requested " + game.getContenders().size() * MAX_ROUNDS + " questions and got " + questions.size());
            setQuestions(game, questions);
            game.setRound(1);
            game.setPhase("answering questions");
            gameRepository.save(game);
            return;
        }
        LOGGER.info("Tried to start game but already started..., current round: " + currentRound);
        throw new Exception("Tried to start game but already started..., current round: " + currentRound);
    }

    @RequestMapping(value = "/game/{gameId}/start-round", method = RequestMethod.POST)
    public void startRound(@PathVariable Long gameId) throws InvalidGameIdException {
        Game game = repoUtil.findGameById(gameId);
        Integer nextRound = game.getRound() + 1;
        game.setRound(nextRound);
        game.setPhase("answering questions");
        gameRepository.save(game);
        LOGGER.info("starting round " + nextRound);
    }

    @RequestMapping(value = "/game/{gameId}/start-voting", method = RequestMethod.POST)
    public void startVoting(@PathVariable Long gameId) throws Exception {
        LOGGER.info("Starting voting");
        Game game = repoUtil.findGameById(gameId);
        game.setPhase("voting");
        if (canContinueVoting(game)) {
            LOGGER.debug("There is more to vote on still");
            setCurrentQuestionAnswers(game);
            LOGGER.info(String.format("Question: %s chosen", game.getCurrentQuestionAnswers().get(0).getQuestion()));
            LOGGER.debug(String.format("Number of questionAnswers: %d", game.getCurrentQuestionAnswers().size()));
            for (QuestionAnswer questionAnswer : game.getCurrentQuestionAnswers()) {
                LOGGER.debug(String.format("Question id: %d", questionAnswer.getId()));
            }
        } else {
            throw new Exception("No more questions to vote on!");
        }
        gameRepository.save(game);
    }

    @RequestMapping(value = "/game/{gameId}/question-votes", method = RequestMethod.GET)
    public @ResponseBody QuestionVotesResponse getQuestionVotes(@PathVariable Long gameId) throws InvalidGameIdException {
        Game game = repoUtil.findGameById(gameId);
        return new QuestionVotesResponse(game.getCurrentQuestionAnswers());
    }

    @RequestMapping(value = "/game/{gameId}/set-next-to-score", method = RequestMethod.POST)
    public void setNextToScore(@PathVariable Long gameId) throws Exception {
        Game game = repoUtil.findGameById(gameId);
        Set<QuestionAnswer> questionAnswers = generalUtil.getRoundByRoundNum(game.getRound(), game.getRounds())
                .getQuestionAnswers();

        // Filter out questionAnswers that have already been scored
        List<QuestionAnswer> questionAnswerList = new ArrayList<>();
        for(QuestionAnswer questionAnswer: questionAnswers) {
            if (questionAnswer.getScore() == null) {
                questionAnswer.setGame(null);
                questionAnswerList.add(questionAnswer);
            } else if (questionAnswer.getGame() != null) {
                questionAnswer.setGame(null);
                questionAnswerRepository.save(questionAnswer);
            }
        }
        LOGGER.info(String.format("Unscored answers: %d", questionAnswerList.size()));

        if (questionAnswerList.isEmpty()) {
            return;
        }

        Random rand = new Random();
        QuestionAnswer chosenQuestionAnswer = questionAnswerList.get(
                rand.nextInt(questionAnswerList.size()));
        chosenQuestionAnswer.setGame(game);
        chosenQuestionAnswer.setScore(0);

        // Find other matching answer
        Set<QuestionAnswer> chosenQuestionAnswers = new HashSet<>();
        chosenQuestionAnswers.add(chosenQuestionAnswer);
        for (QuestionAnswer questionAnswer: questionAnswerList) {
            if (questionAnswer.getQuestion().equals(chosenQuestionAnswer.getQuestion()) &&
                    !questionAnswer.getId().equals(chosenQuestionAnswer.getId())) {
                questionAnswer.setGame(game);
                questionAnswer.setScore(0);
                chosenQuestionAnswers.add(questionAnswer);
            }
        }
        LOGGER.debug(String.format("Found %d questions to replace, this should be 2.", chosenQuestionAnswers.size()));
        LOGGER.info(String.format("Chosen question: %s", chosenQuestionAnswer.getQuestion()));
        LOGGER.info(String.format("Chosen questionId: %d", chosenQuestionAnswer.getId()));
        game.setCurrentQuestionAnswers(chosenQuestionAnswers);
        gameRepository.save(game);
    }

    @RequestMapping(value = "/game/{gameId}/final-results", method = RequestMethod.GET)
    public @ResponseBody FinalResultsResponse getFinalResults(@PathVariable Long gameId) throws InvalidGameIdException {
        Game game = repoUtil.findGameById(gameId);
        if (!game.getPhase().equals("final results")) {
            game.setPhase("final results");
            gameRepository.save(game);
        }
        return new FinalResultsResponse(game.getContenders());
    }

    @RequestMapping(value = "/game/{gameId}/all-votes-submitted", method = RequestMethod.GET)
    public @ResponseBody AllVotesSubmitted allVotesSubmitted(@PathVariable Long gameId) throws InvalidGameIdException {
        Game game = repoUtil.findGameById(gameId);
        int numberOfVotes = 0;
        for (QuestionAnswer questionAnswer : game.getCurrentQuestionAnswers()) {
            numberOfVotes += questionAnswer.getScore();
        }
        LOGGER.info(String.format("Number of contenders: %d", game.getContenders().size()));
        LOGGER.info(String.format("Number of votes submitted: %d", numberOfVotes));
        if (numberOfVotes >= game.getContenders().size()) {
            return new AllVotesSubmitted(true);
        }
        return new AllVotesSubmitted(false);
    }

    @RequestMapping(value = "/game/{gameId}/more-to-vote-on", method = RequestMethod.GET)
    public @ResponseBody MoreToVoteOn moreQuestionsToVoteOn(@PathVariable Long gameId) throws Exception {
        Game game = repoUtil.findGameById(gameId);
        return new MoreToVoteOn(canContinueVoting(game));
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
        List<String> questions = new ArrayList<>(inpQuestions);

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
        List<Contender> contendersQuestion1 = new ArrayList<>(contenders);
        List<Contender> contendersQuestion2 = new ArrayList<>(contenders);

        for (String question: questions){
            QuestionAnswer questionAnswer1 = new QuestionAnswer();
            QuestionAnswer questionAnswer2 = new QuestionAnswer();
            questionAnswer1.setQuestion(question);
            questionAnswer1.setRound(round);
            questionAnswer2.setQuestion(question);
            questionAnswer2.setRound(round);
            if (!contendersQuestion1.isEmpty()) {
                Contender contender = contendersQuestion1.get(
                        rand.nextInt(contendersQuestion1.size()));
                LOGGER.debug(String.format("Contender: %s gets question: %s", contender.getName(), question));
                questionAnswer1.setContender(contender);
            } else {
                Contender contender = contendersQuestion2.get(
                        rand.nextInt(contendersQuestion2.size()));
                LOGGER.debug(String.format("Contender: %s gets question: %s", contender.getName(), question));
                questionAnswer1.setContender(contender);
            }
            if (!contendersQuestion1.isEmpty()) {
                Contender contender = contendersQuestion1.get(
                        rand.nextInt(contendersQuestion1.size()));
//                LOGGER.debug(String.format("Contender: %s gets question: %s", contender.getName(), question));
                questionAnswer2.setContender(contender);
            } else {
                Contender contender = contendersQuestion2.get(
                        rand.nextInt(contendersQuestion2.size()));
//                LOGGER.debug(String.format("Contender: %s gets question: %s", contender.getName(), question));
                questionAnswer2.setContender(contender);
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

    boolean canContinueVoting(Game game) throws Exception {
        Round currentRound = generalUtil.getRoundByRoundNum(game.getRound(), game.getRounds());
        for (QuestionAnswer questionAnswer: currentRound.getQuestionAnswers()) {
            if (questionAnswer.getScore() == null) {
                return true;
            }
        }
        return false;
    }

    void setCurrentQuestionAnswers(Game game) throws Exception {
        // Reset current question answers
        for (QuestionAnswer questionAnswer: game.getCurrentQuestionAnswers()) {
            questionAnswer.setGame(null);
            questionAnswerRepository.save(questionAnswer);
        }

        Round currentRound = generalUtil.getRoundByRoundNum(game.getRound(), game.getRounds());

        int selectedQuestionInt = new Random().nextInt(currentRound.getQuestionAnswers().size());
        String selectedQuestionStr = null;
        List<QuestionAnswer> selectedQuestionAnswers = new ArrayList<>();

        int i = 0;
        for (QuestionAnswer questionAnswer: currentRound.getQuestionAnswers()) {
            if (i == selectedQuestionInt) {
                selectedQuestionStr = questionAnswer.getQuestion();
                questionAnswer.setGame(game);
                questionAnswer.setScore(0);
                questionAnswerRepository.save(questionAnswer);
                break;
            }
            i++;
        }
        for (QuestionAnswer questionAnswer: currentRound.getQuestionAnswers()) {
            if (questionAnswer.getQuestion().equals(selectedQuestionStr)) {
                selectedQuestionAnswers.add(questionAnswer);
                questionAnswer.setGame(game);
                questionAnswer.setScore(0);
                questionAnswerRepository.save(questionAnswer);
            }
            i++;
        }
        game.setCurrentQuestionAnswers(selectedQuestionAnswers);
    }
}
