package com.asp.mapquiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asp.mapquiz.question.Option;
import com.asp.mapquiz.question.Question;
import com.google.android.gms.maps.GoogleMap;

public class QuestionFragment extends Fragment {
    public static interface QuestionListener {
        public void onCorrectChosen();

        public void onWrongChosen();
    }
    private QuestionListener mListener;
    private Question mQuestion;

    private GoogleMap mMap;

    /* Order of options:
     * [Option 1 ---- Option 2]
     * [Option 4------Option 4]
     */
    private Button[] mOptions = new Button[4];

    public static QuestionFragment createFragment(GoogleMap map, QuestionListener listener,
                                                  Question question) {
        QuestionFragment fragment = new QuestionFragment();
        fragment.mListener = listener;
        fragment.mQuestion = question;
        fragment.mMap = map;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.question_fragment, null, false);
        mOptions[0] = (Button) v.findViewById(R.id.button1);
        mOptions[1] = (Button) v.findViewById(R.id.button2);
        mOptions[2] = (Button) v.findViewById(R.id.button3);
        mOptions[3] = (Button) v.findViewById(R.id.button4);
        for (int i = 0; i < 4; i++) {
            final Option option = mQuestion.getPossibleAnswers().get(i);
            mOptions[i].setTag(option);
            mOptions[i].setText(option.getName());
            mOptions[i].setOnClickListener(mAnswerListener);
        }

        mQuestion.changeMap(mMap);
        return v;
    }

    private final View.OnClickListener mAnswerListener = new View.OnClickListener() {
        public void onClick(View v) {
            Option option = (Option) v.getTag();
            if (mQuestion.isCorrect(option)) {
                mListener.onCorrectChosen();
            } else {
                mListener.onWrongChosen();
            }
            mQuestion.updateMap(option, mMap);
        }
    };
}
