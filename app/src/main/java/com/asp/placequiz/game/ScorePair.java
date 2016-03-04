package com.asp.placequiz.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.asp.placequiz.activities.GameActivity;

/**
 * Simple data class for clean workaround of async allowing only one return value
 */
public final class ScorePair implements Parcelable {
    private int mTimeScore; // Max is 10 for instantly correct answer
    private int mCorrectAnswers;
    private int mWrongAnswers;

    public ScorePair(int timeScore, int correctAnswers, int wrongAnswers) {
        mTimeScore = timeScore;
        mCorrectAnswers = correctAnswers;
        mWrongAnswers = wrongAnswers;
    }

    private ScorePair(Parcel in) {
        mTimeScore = in.readInt();
        mCorrectAnswers = in.readInt();
        mWrongAnswers = in.readInt();
    }

    public int getNumberCorrectAnswers() {
        return mCorrectAnswers;
    }

    public int score() {
        return mTimeScore;
    }

    public int correct() {
        return getNumberCorrectAnswers();
    }

    public int getNumberWrongAnswers() {
        return mWrongAnswers;
    }

    public int wrong() {
        return getNumberWrongAnswers();
    }

    public int total() {
        return correct() + wrong();
    }

    public int maxScore() {
        return total() * GameActivity.TIME_LIMIT;
    }

    public float asPercent() {
        if (maxScore() == 0) {
            return 0;
        }
        return (float) score() / (float) maxScore();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mTimeScore);
        parcel.writeInt(mCorrectAnswers);
        parcel.writeInt(mWrongAnswers);
    }

    public static final Parcelable.Creator<ScorePair> CREATOR
            = new Parcelable.Creator<ScorePair>() {
        public ScorePair createFromParcel(Parcel in) {
            return new ScorePair(in);
        }

        public ScorePair[] newArray(int size) {
            return new ScorePair[size];
        }
    };

    @Override
    public String toString() {
        return correct() + "/" + total();
    }
}
