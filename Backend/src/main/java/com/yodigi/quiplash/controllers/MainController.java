package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.dto.InitResponse;
import com.yodigi.quiplash.dto.JoinRequest;
import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.exceptions.ContenderAlreadyExistsException;
import com.yodigi.quiplash.repositories.ContenderRepository;
import com.yodigi.quiplash.repositories.GameRepository;
import com.yodigi.quiplash.utils.RepoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    private Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RepoUtil repoUtil;

    @Autowired
    private ContenderRepository contenderRepository;

    @RequestMapping(value = "/init", method = RequestMethod.POST)
    public @ResponseBody InitResponse initGame() {
        LOGGER.info("INITIALIZING GAME");
        LOGGER.debug("test");
        Game game = new Game();
        game.setPhase("joining");
        return new InitResponse(gameRepository.save(game).getId());
    }

    @RequestMapping(value = "/game/{gameId}/join", method = RequestMethod.POST)
    public void joinGame(@PathVariable Long gameId, @RequestBody JoinRequest joinRequest) throws Exception {
        String name = joinRequest.getName();
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

    @RequestMapping(value = "/game/{gameId}/end", method = RequestMethod.POST)
    public void endGame(@PathVariable Long gameId) throws Exception {
        Game game = repoUtil.findGameById(gameId);
        if (game != null) {
            gameRepository.delete(game);
            return;
        }
        throw new Exception("Game never existed");
    }

    boolean exists(Game game, String name) {
        return contenderRepository.findByGameAndName(game, name) != null;
    }

}
