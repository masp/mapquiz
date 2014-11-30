package com.asp.mapquiz.state;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.asp.mapquiz.question.Option;
import com.asp.mapquiz.question.Question;
import com.asp.mapquiz.R;
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
    private static final int ZOOM_LEVEL = 9;
    private static final int OUTLINE_STROKE_WIDTH = 5;
    private static final int OUTLINE_COLOR = Color.RED;

    private Context mContext;
    private StateOption mCorrect;
    private List<StateOption> mWrongs;

    /**
     * @param context Context of the question
     * @param wrongs <b>Must be three(3) wrong options</b>
     * @param correct The option that is correct
     */
    public StateQuestion(Context context, StateOption correct, List<StateOption> wrongs) {
        if (wrongs.size() != 3)
            throw new IllegalArgumentException("You must pass three wrong options to " +
                    "the StateQuestion constructor!");
        mContext = context;
        mCorrect = correct;
        mWrongs = new ArrayList<StateOption>(wrongs);
    }

    @Override
    public void changeMap(GoogleMap map) {
        //      Select state and choose location within state to display
        LatLng loc = mCorrect.getRandomPoint();
        final Iterable<LatLng> outline = mCorrect.getPoints();
        map.clear();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, ZOOM_LEVEL));
        Resources res = mContext.getResources();
        map.addMarker(new MarkerOptions()
                .title(res.getString(R.string.marker_title))
                .snippet(res.getString(R.string.marker_snippet))
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
        if (!(chosenOption instanceof StateOption))
            throw new IllegalArgumentException("Must pass StateOption to StateQuestion");
        StateOption stateOption = (StateOption) chosenOption;
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(stateOption.getBounds(), 80));
    }
}
