package src.com.yodigi.quiplash.controllers;

import com.yodigi.quiplash.controllers.GameMasterController;
import com.yodigi.quiplash.controllers.MainController;
import com.yodigi.quiplash.dto.ContenderNamesResponse;
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
import java.util.LinkedList;
import java.util.Objects;

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
    // needs implementation of the givenID being input by the user
    // not enough experience with javascript to connect it to the files in that half of the project
    @Test
    public void giveGameIdDoesntExist_whenCallingJoinGame() throws Exception {
        Game game = new Game();
        Long givenID = 1L; // TODO: this would need to be changed to an input from the user, whatever they put in the box (not currently available)
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setName("Liz");
        if (!givenID.equals(game.getId())){ //if the game id isnt a valid ID, tell the user
            //posts to the URL that the game is full rather than allowing the user to join
            mockMvc.perform(post("/game/1/join").contentType(MediaType.APPLICATION_JSON)
                            .content("The given ID does not exist. Check the screen and try again."))
                    .andExpect(status().isOk());
        }

        verify(contenderRepository, times(2)).save(any(Contender.class));
    }

    // TODO: join when phase isn't joining
    // not sure this will properly reject the player as opposed to just tell them the game is full
    // idea is to just not put them into the game as opposed to push them anywhere
    @Test
    public void giveGameIdExists_whenCallingJoinGame_gameIsOutofPhase() throws Exception {
        Game game = new Game();
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setName("Liz");
        doReturn(null).when(contenderRepository).findByGameAndName(game, joinRequest.getName());
        doReturn(game).when(repoUtil).findGameById(1L);
        if (!Objects.equals(game.getPhase(), "joining")){ //if the phase isn't 'joining,' reject it
            //posts to the URL that the game is full rather than allowing the user to join
            mockMvc.perform(post("/game/1/join").contentType(MediaType.APPLICATION_JSON)
                    .content("This game is currently busy. Try again when the round is finished."))
                    .andExpect(status().isOk());
        }

        verify(contenderRepository, times(2)).save(any(Contender.class));
    }

    // TODO: join when 8 contenders, then add to audience
    //do a while loop (while number of joined members < 8, allow for joining, then push them to join in the audience)
    @Test
    public void giveGameIdExists_whenCallingJoinGame_butFullLobby() throws Exception {
        Game game = new Game();
        GameMasterController contenderNamesResponse = new GameMasterController();
        JoinRequest joinRequest = new JoinRequest();
        ArrayList audience = new ArrayList<>(); // creates the audience array to be used later
        ArrayList contenders = new ArrayList<>();

        game.setPhase("joining");
        game.setContenders(contenders);
        game.setAudience(audience); //correlates audience to previously made array
        joinRequest.setName("Liz");

        while (contenders.size() < 8) {
            giveGameIdDoesntExist_whenCallingJoinGame();
            giveGameIdExists_whenCallingJoinGame_gameIsOutofPhase();
            giveGameIdExists_whenCallingJoinGame_thenJoinGame();

        }

        //TODO: this would need to be reassigned to a new audience repository rather than contender
        doReturn(null).when(contenderRepository).findByGameAndName(game, joinRequest.getName());
        doReturn(game).when(repoUtil).findGameById(1L); //allows user to join but funnels the user to the audience

        mockMvc.perform(post("/game/1/join").contentType(MediaType.APPLICATION_JSON)
                        .content("{Audience: }"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/game/1/join").contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(audience.size()))) //posts audience size to the web window
                .andExpect(status().isOk());
        mainControllerMock.joinGame(1L, joinRequest);

        verify(contenderRepository, times(2)).save(any(Contender.class));

    }
}

