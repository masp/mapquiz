package com.asp.mapquiz.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.asp.mapquiz.game.Mode;
import com.asp.mapquiz.R;
import com.asp.mapquiz.fragments.MenuFragment;
import com.asp.mapquiz.menu.Menu;
import com.asp.mapquiz.menu.Menus;
import com.asp.mapquiz.question.QuestionFactory;
import com.asp.mapquiz.state.StateQuestionFactory;

public class MenuActivity extends AppCompatActivity {

    private MenuFragment mMenuFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        modifyTitleColors();

        if (!isNetworkAvailable()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.no_network_title)
                    .setMessage(R.string.no_network_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MenuActivity.this.finish();
                        }
                    }).create();
            dialog.show();
        }

        QuestionFactory.addQuestionFactory(QuestionFactory.QuizType.US_STATE,
                new StateQuestionFactory(this));

        showRootOptionMenu();
    }

    @Override
    public void onBackPressed() {
        boolean isRootMenu = !mMenuFragment.prevMenu();
        if (isRootMenu) {
            finish();
        }
    }

    private void modifyTitleColors() {
        TextView title = (TextView) findViewById(R.id.menu_title);
        String html = "<font color='#ffffff'>Map</font>Quiz";
        Spanned updatedTitle = Html.fromHtml(html);
        title.setText(updatedTitle);
    }

    private void showRootOptionMenu() {
        final Mode.ModeBuilder builder = new Mode.ModeBuilder();
        Menu.MenuBuilder rootMenu = Menus.buildRootMenu(this, builder);
        Menu.MenuBuilder quizTypeMenu = Menus.buildQuizTypeMenu(builder);
        Menu.MenuBuilder difficultyMenu = Menus.buildDifficultyMenu(builder);

        mMenuFragment = MenuFragment.createFragment(this, new MenuFragment.MenuCompletionListener() {
            @Override
            public void onComplete() {
                Mode mode = builder.build();
                Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                intent.putExtra(GameActivity.DIFFICULTY_TAG, mode.getDifficulty());
                intent.putExtra(GameActivity.GAME_TYPE_TAG, mode.getGameType());
                intent.putExtra(GameActivity.QUIZ_TYPE_TAG, mode.getQuizType());

                startActivity(intent);
            }
        }, rootMenu, quizTypeMenu, difficultyMenu);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.menu_fragment_container, mMenuFragment);
        ft.commit();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
