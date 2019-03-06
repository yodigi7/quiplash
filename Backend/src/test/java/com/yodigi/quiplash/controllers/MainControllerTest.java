package com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.dto.InitResponse;
import com.yodigi.quiplash.dto.JoinRequest;
import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import com.yodigi.quiplash.repositories.ContenderRepository;
import com.yodigi.quiplash.repositories.GameRepository;
import com.yodigi.quiplash.utils.RepoUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MainControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContenderRepository contenderRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private RepoUtil repoUtil;
    @InjectMocks
    private MainController mainControllerMock;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(mainControllerMock)
                .build();
    }
    
    @Test
    public void whenCallingInitGame_thenSaveGameIsCalledAndGameIdIsReturned() throws Exception {
        Game game = new Game();
        game.setId(1L);
        doReturn(game).when(gameRepository).save(any());

        System.out.println(mockMvc);
        mockMvc.perform(post("/init"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"gameId\":1}"));
        InitResponse initResponse = mainControllerMock.initGame();

        assertEquals((Long) 1L, initResponse.getGameId());
        verify(gameRepository, times(2)).save(any());
    }

    @Test
    public void givenGameIdExists_whenCallingEndGame_thenCallDeleteGame() throws Exception {
        Game game = new Game();
        doReturn(game).when(repoUtil).findGameById(1L);

        mockMvc.perform(post("/game/1/end"))
                .andExpect(status().isOk());
        mainControllerMock.endGame(1L);

        verify(gameRepository, times(2)).delete(game);
    }

    // TODO: Call end game on non-existant game

    @Test
    public void giveGameIdExists_whenCallingJoinGame_thenJoinGame() throws Exception {
        Game game = new Game();
        game.setPhase("joining");
        game.setContenders(new ArrayList<>());
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setName("Liz");
        doReturn(null).when(contenderRepository).findByGameAndName(game, joinRequest.getName());
        doReturn(game).when(repoUtil).findGameById(1L);

        mockMvc.perform(post("/game/1/join").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Anthony\"}"))
                .andExpect(status().isOk());
        mainControllerMock.joinGame(1L, joinRequest);

        verify(contenderRepository, times(2)).save(any(Contender.class));
    }

    // TODO: Join when game doesn't exist

    // TODO: join when phase isn't joining

    // TODO: join when 8 contenders, then add to audience
}
