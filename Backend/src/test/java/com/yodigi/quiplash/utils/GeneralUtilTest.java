package com.yodigi.quiplash.utils;

import com.yodigi.quiplash.entities.Round;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GeneralUtilTest {

    @Autowired
    private GeneralUtil generalUtil;

    @Test(expected = Exception.class)
    public void givenAnInvalidRoundNumber_whenCallingGetRoundByRoundNum_thenExpectAnException() throws Exception {
        Set<Round> rounds = new HashSet<>();
        for (int i = 1; i <= 3; i++) {
            Round round = new Round();
            round.setRoundNumber(i);
            rounds.add(round);
        }

        generalUtil.getRoundByRoundNum(4, rounds);
    }

    @Test
    public void givenAValidRoundNumber_whenCallingGetRoundByRoundNum_thenItReturnsTheCorrectRound() throws Exception {
        Set<Round> rounds = new HashSet<>();
        Round expectedRound = new Round();
        expectedRound.setRoundNumber(4);
        rounds.add(expectedRound);
        for (int i = 1; i <= 3; i++) {
            Round round = new Round();
            round.setRoundNumber(i);
            rounds.add(round);
        }

        Round returnedRound = generalUtil.getRoundByRoundNum(4, rounds);

        assertEquals(expectedRound, returnedRound);
        assertEquals(4, (int) returnedRound.getRoundNumber());
    }
}
