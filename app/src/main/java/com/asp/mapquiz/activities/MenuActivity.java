package com.asp.mapquiz.activities;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.asp.mapquiz.database.MapQuizContract;
import com.asp.mapquiz.database.MapQuizDbHelper;
import com.asp.mapquiz.database.tasks.FetchUserIDTask;
import com.asp.mapquiz.game.Mode;
import com.asp.mapquiz.R;
import com.asp.mapquiz.fragments.MenuFragment;
import com.asp.mapquiz.menu.Menu;
import com.asp.mapquiz.menu.Menus;
import com.asp.mapquiz.question.QuestionFactory;
import com.asp.mapquiz.state.StateQuestionFactory;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1212;

    private MenuFragment mMenuFragment;
    private SharedPreferences mPreferences;

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



        mPreferences = getSharedPreferences(MapQuizContract.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        if (!hasOpenedBefore()) {
            Log.d(TAG, "Storing new user email...");
            pickUserAccount();
        }

        initializeQuestionFactories();
        showRootOptionMenu();
    }

    @Override
    public void onBackPressed() {
        boolean isRootMenu = !mMenuFragment.prevMenu();
        if (isRootMenu) {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                storeEmail(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.pick_email, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void modifyTitleColors() {
        TextView title = (TextView) findViewById(R.id.menu_title);
        String html = "<font color='#ffffff'>Map</font>Quiz";
        Spanned updatedTitle = Html.fromHtml(html);
        title.setText(updatedTitle);
    }

    private void initializeQuestionFactories() {
        StateQuestionFactory stateFactory = new StateQuestionFactory(this);
        try {
            stateFactory.loadFileData(R.xml.states);
        } catch (Exception ex) {
            Log.d(TAG, "Error while loading states XML data.");
            ex.printStackTrace();
        }
        QuestionFactory.addQuestionFactory(QuestionFactory.QuizType.US_STATE, stateFactory);
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[] {"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    private void storeEmail(String email) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(MapQuizContract.UserEntry.COLUMN_NAME_USER_EMAIL_HASH, email);
        editor.apply();

        FetchUserIDTask fetchIdTask = new FetchUserIDTask(getApplication());
        fetchIdTask.execute(email);
    }

    private void showRootOptionMenu() {
        final Mode.ModeBuilder builder = new Mode.ModeBuilder();
        builder.withQuizType(QuestionFactory.QuizType.US_STATE); // TODO: v1 only
        Menu.MenuBuilder rootMenu = Menus.buildRootMenu(this, builder);
        //Menu.MenuBuilder quizTypeMenu = Menus.buildQuizTypeMenu(builder);
        Menu.MenuBuilder difficultyMenu = Menus.buildDifficultyMenu(builder);

        mMenuFragment = MenuFragment.createFragment(this, rootMenu,
            new MenuFragment.MenuCompletionListener() {
                @Override
                public void onComplete() {
                    Mode mode = builder.build();
                    Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                    intent.putExtra(GameActivity.DIFFICULTY_TAG, mode.getDifficulty());
                    intent.putExtra(GameActivity.GAME_TYPE_TAG, mode.getGameType());
                    intent.putExtra(GameActivity.QUIZ_TYPE_TAG, mode.getQuizType());

                    startActivity(intent);
                }
            });
        //mMenuFragment.addMenu(this, quizTypeMenu);
        mMenuFragment.addMenu(this, difficultyMenu);

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

    private boolean hasOpenedBefore() {
        long userId = mPreferences.getLong(MapQuizContract.USER_ID_KEY, -1L);
        Log.d("DBTEST", "user id: " + userId);
        if (userId == -1) {
            return false;
        }
        return true;
    }
}
