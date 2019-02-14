package com.yodigi.quiplash.utils;

import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.entities.QuestionAnswer;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.repositories.ContenderRepository;
import com.yodigi.quiplash.repositories.GameRepository;
import com.yodigi.quiplash.repositories.QuestionAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class RepoUtil {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private ContenderRepository contenderRepository;

    public Game findGameById(Long id) throws InvalidGameIdException {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            throw new InvalidGameIdException("Invalid game of id: " + id.toString());
        }
        return game;
    }

    public QuestionAnswer findQuestionAnswerById(Long id) throws Exception {
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(id).orElse(null);
        if (questionAnswer == null) {
            // TODO: Create custom exception
            throw new Exception("Invalid question answer id of: " + id.toString());
        }
        return questionAnswer;
    }

    public Contender findContenderByNameAndGameId(String name, Long gameId) throws Exception {
        for(Contender contender: findGameById(gameId).getContenders()) {
            if (contender.getName().equals(name)) {
                return contender;
            }
        }
        throw new Exception("This shouldn't happen...");
    }

    public Contender findContenderByNameAndGame(String name, Game game) throws Exception {
        Contender contender = contenderRepository.findByGameAndName(game, name);
        if (contender == null) {
            // TODO: Create custom exception
            throw new Exception("Invalid contender with name: " + name + " and gameId: " + game.getId().toString());
        }
        return contender;
    }
}
