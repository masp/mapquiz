package com.asp.mapquiz.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.asp.mapquiz.R;
import com.asp.mapquiz.database.tasks.FetchHighscoreTask;
import com.asp.mapquiz.fragments.LoadingFragment;
import com.asp.mapquiz.fragments.NextButtonFragment;
import com.asp.mapquiz.fragments.QuestionFragment;
import com.asp.mapquiz.fragments.QuestionMapFragment;
import com.asp.mapquiz.game.GameType;
import com.asp.mapquiz.game.GameTypeFactory;
import com.asp.mapquiz.game.Mode;
import com.asp.mapquiz.game.ScorePair;
import com.asp.mapquiz.question.Option;
import com.asp.mapquiz.question.Question;
import com.asp.mapquiz.question.QuestionFactory;
import com.asp.mapquiz.ui.GameTimer;
import com.asp.mapquiz.ui.ScoreBar;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    public static final int TIME_LIMIT = 10; // seconds

    public static final String GAME_TYPE_TAG = "GAME_TYPE";
    public static final String QUIZ_TYPE_TAG = "QUIZ_TYPE";
    public static final String DIFFICULTY_TAG = "DIFFICULTY_TYPE";

    private ScorePair mHighscore;

    private ScoreBar mScoreBar;

    private GameTimer mTimer;

    private ValueAnimator mTimerBarAnimator;
    private RoundCornerProgressBar mTimerBar;

    private Toast mResultPopup;

    private boolean mShouldAbortLoading = false;

    private Mode mMode;
    private GameType mGameManager;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    private QuestionFragment mQuestionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        initGameManager();

        FetchHighscoreTask highscoreTask = new FetchHighscoreTask(getApplicationContext(), mMode) {
            @Override
            protected void onPostExecute(ScorePair highscore) {
                mHighscore = highscore;
            }
        };
        highscoreTask.execute();

        mTimerBar = (RoundCornerProgressBar) findViewById(R.id.game_timer_bar);

        mScoreBar = (ScoreBar) findViewById(R.id.score_bar);
        mScoreBar.setTimeLimit(TIME_LIMIT);
        mScoreBar.setDifficulty(mMode.getDifficulty());

        mMapFragment = QuestionMapFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.location_select_map_fragment, mMapFragment);
        ft.commit();

        mTimer = new GameTimer(new Handler(getMainLooper()), TIME_LIMIT);
        mTimer.addTimerListener(new GameTimer.TimerListener() {
            @Override
            public void onTick() {
                mScoreBar.tickDown();
            }
        });
        mTimer.setEndTimerListener(new GameTimer.OnTimerEndListener() {
            @Override
            public void onTimeEnd() {
                if (mTimer == null || !mTimer.isRunning() || mTimer.isPaused()) return;
                mQuestionFragment.selectAnswer(Option.NULL);
            }
        });

        mTimerBarAnimator = ValueAnimator.ofFloat(1f, 0f);
        mTimerBarAnimator.setDuration(TIME_LIMIT * 1000L);
        mTimerBarAnimator.setInterpolator(new LinearInterpolator());
        mTimerBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (mTimer.isRunning() && !mTimer.isPaused()) {
                    mTimerBar.setProgress(mTimerBar.getMax() * (float) valueAnimator.getAnimatedValue());
                }
            }
        });

        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                showNextQuestion();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mTimer.isPaused()) {
            mTimer.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mTimer.isRunning()) {
            mTimer.pause();
        }
    }

    private Question initNewQuestion() {
        final Question question = QuestionFactory.createQuestion(mMode.getQuizType(), mMode.getDifficulty());
        question.changeMap(mMap);
        return question;
    }

    private void showNextQuestion() {
        if (mMap == null) throw new IllegalStateException("Map belonging to map fragment is null!");
        final Question question = initNewQuestion();

        LoadingFragment loadingFragment = LoadingFragment.createFragment(new LoadingFragment.LoadingListener() {
            @Override
            public void onUserFail() {
                finish();
            }
        });
        showFragment(loadingFragment);
        resetUIState();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mQuestionFragment = QuestionFragment.createFragment(mMap, mQuestionListener, question);
                showFragment(mQuestionFragment);
                mTimer.start();
                mTimerBarAnimator.start();
            }
        });
    }

    private void showNextButton(boolean isCorrect) {
        mTimer.stop();
        mTimerBarAnimator.cancel();
        final boolean shouldContinue = mGameManager.shouldAskAnother(mScoreBar.getRemainingTime(),
                isCorrect);
        NextButtonFragment nextFragment = NextButtonFragment.createFragment(isCorrect,
            new NextButtonFragment.OnNextPressedListener() {
                @Override
                public void onPressed() {
                    mResultPopup.cancel();
                    if (shouldContinue) {
                        showNextQuestion();
                    } else {
                        Intent intent = new Intent(GameActivity.this, SummaryActivity.class);

                        ScorePair finalScore = mGameManager.getScore();
                        intent.putExtra(SummaryActivity.SUMMARY_SCORE_TAG, (Parcelable) finalScore);
                        intent.putExtra(SummaryActivity.SUMMARY_HIGHSCORE, (Parcelable) mHighscore);
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

    private void resetUIState() {
        mScoreBar.resetTimer();
        mTimerBar.setProgress(mTimerBar.getMax());
    }

    private void initGameManager() {
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
    }

    private void updateScoreBoard() {
        mScoreBar.setScore(mGameManager.getScore());
    }

    private void showPopupMark(boolean isCorrect) {
        Resources res = getResources();
        TextView mark = new TextView(getApplicationContext());
        mark.setText(isCorrect ? getString(R.string.correct_mark) : getString(R.string.wrong_mark));
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
