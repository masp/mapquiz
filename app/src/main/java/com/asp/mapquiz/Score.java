package com.asp.mapquiz;

import java.util.List;

/**
 * Created by sepaula on 10/12/2014.
 */
public class Score {
    private long mPlayerID;
    private boolean mCorrect;
    private State mState;
    private List<State> mStates;
    private long mDuration;
    private int mQuizType;

    public Score(long playerID, boolean correct, State selectedState, List<State> states, long duration, int quizType){
        mPlayerID=playerID;
        mCorrect=correct;
        mState=selectedState;
        mStates=states;
        mDuration=duration;


    }

}


//When player hits start (first game) or when new map appears (repeat game), store dateTime
//When player selects option, stop timer and record duration and whether the answer is right or wrong.

// for each game, record map coordinates, state array (four random states), correct state, duration and right or wrong.

// score is calculated as total score, % correct answers, average duration for correct answer.

// maximum of maxSeconds=6 seconds before time-out. Maximum score for right answer maxScore= 10 points
// score declines to minScore=0 points at time-out.

// groups of nGames = 7 are combined into a Round. Games are automatically started within a round.
// the user starts a round. At the end of the round, the scores are displayed and advertisement - basic menu