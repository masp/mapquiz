package com.asp.mapquiz.state;

import android.content.Context;

import com.asp.mapquiz.game.Mode;
import com.asp.mapquiz.question.Question;
import com.asp.mapquiz.question.QuestionFactory;
import com.asp.mapquiz.R;
import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StateQuestionFactory implements QuestionFactory.QuestionTypeFactory {
    private List<StateOption> mStateOptions;
    private Context mContext;
    private Random mRandom = new Random();

    public StateQuestionFactory(Context context) {
        mContext = context;
        XmlPullParser xmlParser = context.getResources().getXml(R.xml.states);
        try {
            StateOption.StateBuilder currState = null;
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = xmlParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mStateOptions = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("state".equals(tag)) {
                            currState = new StateOption.StateBuilder(
                                    xmlParser.getAttributeValue(null, "name"));
                        } else if (currState != null && "point".equals(tag)) {
                            double lat = Double.parseDouble(xmlParser.getAttributeValue(null, "lat"));
                            double lng = Double.parseDouble(xmlParser.getAttributeValue(null, "lng"));
                            currState.addPoint(new LatLng(lat, lng));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (currState != null && "state".equals(tag)) {
                            mStateOptions.add(currState.build());
                        }
                        break;
                }
                eventType = xmlParser.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Question createQuestion(Mode.Difficulty difficulty) {
        List<StateOption> options = getRandomStates(mRandom);
        StateOption correct = options.remove(mRandom.nextInt(options.size()));
        return new StateQuestion(mContext, difficulty, correct, options);
    }

    private List<StateOption> getRandomStates(Random random) {
        List<StateOption> stateOptions = new ArrayList<StateOption>();
        List<Integer> chosenIndices = new ArrayList<Integer>();
        chosenIndices.add(-1);
        for (int i = 0; i < 4; i++) {
            int randIndex = -1;
            while (chosenIndices.contains(randIndex))
                randIndex = random.nextInt(mStateOptions.size());
            chosenIndices.add(randIndex);
            StateOption stateOption = mStateOptions.get(randIndex);
            stateOptions.add(stateOption);
        }
        return stateOptions;
    }
}
