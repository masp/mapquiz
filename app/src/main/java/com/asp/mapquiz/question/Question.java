package com.asp.mapquiz.question;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

/**
 * Represents a question with a certain state:
 * - Modifies the map
 * - Determines the question
 * - Judges the answer the user provides
 */
public interface Question {
    public void changeMap(GoogleMap map);

    public List<Option> getPossibleAnswers();

    public boolean isCorrect(Option option);

    public void updateMap(Option chosenOption, GoogleMap map);
}
