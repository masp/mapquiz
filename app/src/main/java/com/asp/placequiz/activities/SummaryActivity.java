package com.asp.placequiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asp.placequiz.R;
import com.asp.placequiz.database.tasks.StoreRoundTask;
import com.asp.placequiz.game.GameType;
import com.asp.placequiz.game.Mode;
import com.asp.placequiz.game.ScorePair;
import com.asp.placequiz.question.QuestionFactory;
import com.asp.placequiz.ui.ScoreBox;

public class SummaryActivity extends AppCompatActivity {
    public static final String SUMMARY_SCORE_TAG = "GAME_SCORE_MAPQUIZ";
    public static final String SUMMARY_HIGHSCORE = "SUMMARY_HIGHSCORE_MAPQUIZ";

    private ScorePair mHighscore;
    private ScorePair mFinalScore;

    private QuestionFactory.QuizType mPastQuizType;
    private GameType.Name mPastGameType;
    private Mode.Difficulty mPastDifficulty;
    private Mode mMode;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_activity);
        parseIntentData();

        TextView gamemodeHeading = (TextView) findViewById(R.id.gamemode_heading);
        gamemodeHeading.setText(getString(R.string.game_heading)
                    .replace("#gametype#", GameType.Name.getReadableName(mMode.getGameType())));

        StoreRoundTask roundTask = new StoreRoundTask(getApplication(), mMode);
        roundTask.execute(mFinalScore);
        if (mFinalScore.asPercent() > mHighscore.asPercent()) {
            mHighscore = mFinalScore;
        }

        ScoreBox finalBar = (ScoreBox) findViewById(R.id.final_score_progress_bar);
        finalBar.updateWithScore(mFinalScore);

        ScoreBox highscoreBar = (ScoreBox) findViewById(R.id.high_score_progress_bar);
        highscoreBar.updateWithScore(mHighscore);

        Button tryAgain = (Button) findViewById(R.id.summary_try_again);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SummaryActivity.this, GameActivity.class);
                intent.putExtra(GameActivity.QUIZ_TYPE_TAG, mPastQuizType);
                intent.putExtra(GameActivity.GAME_TYPE_TAG, mPastGameType);
                intent.putExtra(GameActivity.DIFFICULTY_TAG, mPastDifficulty);
                startActivity(intent);
            }
        });

        Button backToMenu = (Button) findViewById(R.id.summary_return_to_menu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void parseIntentData() {
        mHighscore = getIntent().getParcelableExtra(SUMMARY_HIGHSCORE);
        mFinalScore = getIntent().getParcelableExtra(SUMMARY_SCORE_TAG);
        mPastDifficulty = (Mode.Difficulty) getIntent()
                .getSerializableExtra(GameActivity.DIFFICULTY_TAG);
        mPastGameType = (GameType.Name) getIntent()
                .getSerializableExtra(GameActivity.GAME_TYPE_TAG);
        mPastQuizType = (QuestionFactory.QuizType) getIntent()
                .getSerializableExtra(GameActivity.QUIZ_TYPE_TAG);
        mMode = new Mode.ModeBuilder()
                .withGameType(mPastGameType)
                .withQuizType(mPastQuizType)
                .withDifficulty(mPastDifficulty).build();
    }
}
