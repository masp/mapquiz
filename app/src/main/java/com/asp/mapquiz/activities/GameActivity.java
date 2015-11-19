package com.asp.mapquiz.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.asp.mapquiz.R;
import com.asp.mapquiz.fragments.NextButtonFragment;
import com.asp.mapquiz.fragments.QuestionFragment;
import com.asp.mapquiz.game.GameType;
import com.asp.mapquiz.game.GameTypeFactory;
import com.asp.mapquiz.game.Mode;
import com.asp.mapquiz.question.Option;
import com.asp.mapquiz.question.Question;
import com.asp.mapquiz.question.QuestionFactory;
import com.asp.mapquiz.state.StateQuestionFactory;
import com.asp.mapquiz.ui.ScoreBar;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    public static final String GAME_TYPE_TAG = "GAME_TYPE";
    public static final String QUIZ_TYPE_TAG = "QUIZ_TYPE";
    public static final String DIFFICULTY_TAG = "DIFFICULTY_TYPE";

    private ScoreBar mScoreBar;
    private Toast mResultPopup;

    private Mode mMode;
    private GameType mGameManager;
    private SupportMapFragment mMapFragment;
    private QuestionFragment mQuestionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        GameType.Name gameType = (GameType.Name)
                getIntent().getSerializableExtra(GAME_TYPE_TAG);
        Mode.Difficulty difficulty = (Mode.Difficulty)
                getIntent().getSerializableExtra(DIFFICULTY_TAG);
        QuestionFactory.QuizType quizType = (QuestionFactory.QuizType)
                getIntent().getSerializableExtra(QUIZ_TYPE_TAG);
        mMode = new Mode.ModeBuilder()
            .withGameType(gameType)
            .withDifficulty(difficulty)
            .withQuizType(quizType)
            .build();

        mGameManager = GameTypeFactory.createGameType(mMode.getGameType());

        mScoreBar = (ScoreBar) findViewById(R.id.score_bar);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_select_map_fragment);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                GameActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mScoreBar.updateTime(1000L);
                    }
                });
            }
        }, 0L, 1000L);

        showNextQuestion();
    }

    private void showNextQuestion() {
        final Question question = QuestionFactory.createQuestion(mMode.getQuizType(), mMode.getDifficulty());
        mQuestionFragment = QuestionFragment.createFragment(
                mMapFragment.getMap(), mQuestionListener, question);
        showFragment(mQuestionFragment);
    }

    private void showNextButton(boolean isCorrect) {
        final boolean shouldContinue = mGameManager.shouldAskAnother(isCorrect);
        NextButtonFragment nextFragment = NextButtonFragment.createFragment(isCorrect,
                new NextButtonFragment.OnNextPressedListener() {
                    @Override
                    public void onPressed() {
                        mResultPopup.cancel();
                        if (shouldContinue) {
                            showNextQuestion();
                        } else {
                            Intent intent = new Intent(GameActivity.this, SummaryActivity.class);

                            intent.putExtra(SummaryActivity.SUMMARY_SCORE_TAG,
                                    mGameManager.getQuestionsRight());
                            intent.putExtra(SummaryActivity.SUMMARY_WRONG_TAG,
                                    mGameManager.getTotalQuestions() - mGameManager.getQuestionsRight());
                            intent.putExtra(GAME_TYPE_TAG, mMode.getGameType());
                            intent.putExtra(QUIZ_TYPE_TAG, mMode.getQuizType());
                            intent.putExtra(DIFFICULTY_TAG, mMode.getDifficulty());
                            startActivity(intent);
                        }
                    }
                });
        showFragment(nextFragment);
        updateScoreBoard();
        showPopupMark(isCorrect);
    }

    private void updateScoreBoard() {
        mScoreBar.setCorrect(mGameManager.getQuestionsRight());
        mScoreBar.setIncorrect(mGameManager.getTotalQuestions() - mGameManager.getQuestionsRight());
    }

    private void showPopupMark(boolean isCorrect) {
        Resources res = getResources();
        TextView mark = new TextView(getApplicationContext());
        mark.setText(isCorrect ? res.getString(R.string.correct_mark) :
                res.getString(R.string.wrong_mark));
        mark.setTextSize(res.getDimension(R.dimen.answer_popup_text_size));
        mark.setTypeface(null, Typeface.BOLD);
        mark.setTextColor(isCorrect ? res.getColor(R.color.transparent_green) :
                res.getColor(R.color.transparent_red));

        mResultPopup = new Toast(getApplicationContext());
        mResultPopup.setDuration(Toast.LENGTH_SHORT);
        mResultPopup.setGravity(Gravity.CENTER, 0, 0);
        mResultPopup.setView(mark);
        mResultPopup.show();
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.options, fragment);
        ft.commit();
    }

    private final QuestionFragment.QuestionListener mQuestionListener =
        new QuestionFragment.QuestionListener() {
            @Override
            public void onChosen(Question question, Option option) {
                showNextButton(question.isCorrect(option));
            }
        };
}
