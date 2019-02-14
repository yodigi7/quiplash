package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.exceptions.ContenderAlreadyExistsException;
import com.yodigi.quiplash.exceptions.InvalidGameIdException;
import com.yodigi.quiplash.repositories.ContenderRepository;
import com.yodigi.quiplash.repositories.GameRepository;
import com.yodigi.quiplash.utils.RepoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RepoUtil repoUtil;

    @Autowired
    private ContenderRepository contenderRepository;

    @RequestMapping("/init")
    public Long initGame() {
        LOGGER.info("INITIALIZING GAME");
        Game game = new Game();
        game.setPhase("joining");
        return gameRepository.save(game).getId();
    }

    @RequestMapping("/join")
    public void joinGame(@RequestHeader Long gameId, @RequestHeader String name) throws Exception {
        Game game = repoUtil.findGameById(gameId);
        if (!game.getPhase().equals("joining")) {
            throw new Exception("Sorry, the joining phase has closed");
        }

        if (exists(game, name)) {
            throw new ContenderAlreadyExistsException(name + " already exists for game: " + gameId.toString());
        }

        if (game.getContenders().size() == 8) {
            // TODO: add to audience
        } else {
            LOGGER.info(name + " joined game: " + gameId.toString());
            contenderRepository.save(
                    new Contender(game, name));
        }
    }

    @RequestMapping("/end")
    public Boolean endGame(@RequestHeader Long id) throws InvalidGameIdException {
        Game game = repoUtil.findGameById(id);
        if (game != null) {
            gameRepository.delete(game);
            return true;
        }
        return false;
    }

    boolean exists(Game game, String name) {
        return contenderRepository.findByGameAndName(game, name) != null;
    }

}
