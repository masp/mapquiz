package com.asp.mapquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asp.mapquiz.R;
import com.asp.mapquiz.game.GameType;
import com.asp.mapquiz.game.Mode;
import com.asp.mapquiz.question.QuestionFactory;

public class SummaryActivity extends AppCompatActivity {
    public static final String SUMMARY_SCORE_TAG = "GAME_SCORE_MAPQUIZ";
    public static final String SUMMARY_WRONG_TAG = "GAME_WRONG_MAPQUIZ";

    private int mFinalScore;
    private int mFinalWrong;

    private QuestionFactory.QuizType mPastQuizType;
    private GameType.Name mPastGameType;
    private Mode.Difficulty mPastDifficulty;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_activity);

        mFinalScore = getIntent().getIntExtra(SUMMARY_SCORE_TAG, 0);
        mFinalWrong = getIntent().getIntExtra(SUMMARY_WRONG_TAG, 0);
        mPastDifficulty = (Mode.Difficulty) getIntent()
                .getSerializableExtra(GameActivity.DIFFICULTY_TAG);
        mPastGameType = (GameType.Name) getIntent()
                .getSerializableExtra(GameActivity.GAME_TYPE_TAG);
        mPastQuizType = (QuestionFactory.QuizType) getIntent()
                .getSerializableExtra(GameActivity.QUIZ_TYPE_TAG);

        TextView wrongNum = (TextView) findViewById(R.id.summary_wrong);
        wrongNum.setText(Integer.toString(mFinalWrong));
        TextView correctNum = (TextView) findViewById(R.id.summary_correct);
        correctNum.setText(Integer.toString(mFinalScore));

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
}
