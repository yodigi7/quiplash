package com.yodigi.quiplash.utils;

import com.yodigi.quiplash.entities.Round;

import java.util.List;
import java.util.Set;

public class GeneralUtil {

    public Round getRoundByRoundNum(Integer roundNum, Set<Round> roundList) throws Exception {
        for (Round round: roundList){
            if (round.getRoundNumber().equals(roundNum)) {
                return round;
            }
        }
        throw new Exception("This should not happen hopefully...");
    }

}
