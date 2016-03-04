package com.asp.placequiz.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.asp.placequiz.R;
import com.asp.placequiz.database.tasks.FetchHighscoreTask;
import com.asp.placequiz.fragments.LoadingFragment;
import com.asp.placequiz.fragments.NextButtonFragment;
import com.asp.placequiz.fragments.QuestionFragment;
import com.asp.placequiz.fragments.QuestionMapFragment;
import com.asp.placequiz.game.GameType;
import com.asp.placequiz.game.GameTypeFactory;
import com.asp.placequiz.game.Mode;
import com.asp.placequiz.game.ScorePair;
import com.asp.placequiz.question.Option;
import com.asp.placequiz.question.Question;
import com.asp.placequiz.question.QuestionFactory;
import com.asp.placequiz.ui.GameTimer;
import com.asp.placequiz.ui.ScoreBar;
import com.asp.placequiz.ui.TimerBar;
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

    private TimerBar mTimerBar;

    private Toast mResultPopup;

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

        mTimerBar = (TimerBar) findViewById(R.id.game_timer_bar);

        mScoreBar = (ScoreBar) findViewById(R.id.score_bar);
        mScoreBar.setTimeLimit(TIME_LIMIT);
        mScoreBar.setGameType(mMode.getGameType());

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
            mTimerBar.resume(mTimer);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mTimer.isRunning()) {
            mTimer.pause();
            mTimerBar.pause();
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
                mTimerBar.start();
            }
        });
    }

    private void showNextButton(boolean isCorrect) {
        mTimer.stop();
        mTimerBar.stop();
        final boolean shouldContinue = mGameManager.shouldAskAnother(mScoreBar.getRemainingTime(),
                isCorrect);
        final boolean shouldShowDone = mMode.getGameType() == GameType.Name.UNLIMITED;
        NextButtonFragment nextFragment = NextButtonFragment.createFragment(shouldShowDone, isCorrect,
            new NextButtonFragment.OnNextPressedListener() {
                @Override
                public void onNextPressed() {
                    mResultPopup.cancel();
                    if (shouldContinue) {
                        showNextQuestion();
                    } else {
                        gotoSummary();
                    }
                }

                @Override
                public void onDonePressed() {
                    mResultPopup.cancel();
                    gotoSummary();
                }
            });
        showFragment(nextFragment);
        updateScoreBoard();
        showPopupMark(isCorrect);
    }

    private void gotoSummary() {
        Intent intent = new Intent(GameActivity.this, SummaryActivity.class);

        ScorePair finalScore = mGameManager.getScore();
        intent.putExtra(SummaryActivity.SUMMARY_SCORE_TAG, (Parcelable) finalScore);
        intent.putExtra(SummaryActivity.SUMMARY_HIGHSCORE, (Parcelable) mHighscore);
        intent.putExtra(GAME_TYPE_TAG, mMode.getGameType());
        intent.putExtra(QUIZ_TYPE_TAG, mMode.getQuizType());
        intent.putExtra(DIFFICULTY_TAG, mMode.getDifficulty());
        startActivity(intent);
    }

    private void resetUIState() {
        mScoreBar.resetTimer();
        mTimerBar.reset();
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
