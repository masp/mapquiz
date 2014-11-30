package com.asp.mapquiz;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.asp.mapquiz.question.Question;
import com.asp.mapquiz.question.QuestionFactory;
import com.asp.mapquiz.state.StateQuestionFactory;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Random;

public class LocationSelectActivity extends FragmentActivity {
    private static final String TAG = "LocationSelectActivity";

    private SupportMapFragment mMapFragment;
    private QuestionFragment mQuestionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);
        QuestionFactory.addQuestionFactory(QuestionFactory.QuizType.STATE,
                new StateQuestionFactory(this));
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_select_map_fragment);
        showNextQuestion();
    }

    private void showNextQuestion() {
        final Question question = QuestionFactory.createQuestion(QuestionFactory.QuizType.STATE);
        mQuestionFragment = QuestionFragment.createFragment(
                mMapFragment.getMap(), mQuestionListener, question);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.options, mQuestionFragment);
        ft.commit();
    }

    private final QuestionFragment.QuestionListener mQuestionListener =
        new QuestionFragment.QuestionListener() {
            @Override
            public void onCorrectChosen() {
                Toast t = Toast.makeText(LocationSelectActivity.this, "Correct!", Toast.LENGTH_SHORT);
                t.show();
                showNextQuestion();
            }

            @Override
            public void onWrongChosen() {
                Toast t = Toast.makeText(LocationSelectActivity.this, "Wrong!", Toast.LENGTH_SHORT);
                t.show();
                showNextQuestion();
            }
        };
}
