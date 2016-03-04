package com.asp.placequiz.state;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.asp.placequiz.game.Mode;
import com.asp.placequiz.question.Option;
import com.asp.placequiz.question.Question;
import com.asp.placequiz.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * StateQuestion represents any question which deals with the United States as possible answers
 */
public class StateQuestion implements Question {
    private static final int OUTLINE_STROKE_WIDTH = 5;
    private static final int OUTLINE_COLOR = Color.RED;

    private Context mContext;
    private StateOption mCorrect;
    private List<StateOption> mWrongs;
    private Mode.Difficulty mDifficulty;

    /**
     * @param context Context of the question
     * @param wrongs <b>Must be three(3) wrong options</b>
     * @param correct The option that is correct
     */
    public StateQuestion(Context context, Mode.Difficulty difficulty, StateOption correct,
                         List<StateOption> wrongs) {
        if (wrongs.size() != 3)
            throw new IllegalArgumentException("You must pass three wrong options to " +
                    "the StateQuestion constructor!");
        mContext = context;
        mDifficulty = difficulty;
        mCorrect = correct;
        mWrongs = new ArrayList<StateOption>(wrongs);
    }

    @Override
    public void changeMap(GoogleMap map) {
        //      Select state and choose location within state to display
        LatLng loc = mCorrect.getRandomPoint();
        final Iterable<LatLng> outline = mCorrect.getPoints();
        map.clear();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, mDifficulty.getZoomLevel()));
        Resources res = mContext.getResources();
        map.addMarker(new MarkerOptions()
                .title(res.getString(R.string.marker_title))
                .position(loc));
        map.addPolygon(new PolygonOptions()
                .strokeColor(OUTLINE_COLOR)
                .strokeWidth(OUTLINE_STROKE_WIDTH)
                .addAll(outline));
    }

    @Override
    public List<Option> getPossibleAnswers() {
        List<Option> options = new ArrayList<Option>();
        options.add(mCorrect);
        options.addAll(mWrongs);
        return options;
    }

    @Override
    public boolean isCorrect(Option option) {
        return option.equals(mCorrect);
    }

    @Override
    public void updateMap(Option chosenOption, GoogleMap map) {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(mCorrect.getBounds(), 80));
    }
}
