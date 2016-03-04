package com.asp.placequiz.menu;

import android.app.Activity;

import com.asp.placequiz.game.GameType;
import com.asp.placequiz.game.Mode;
import com.asp.placequiz.R;
import com.asp.placequiz.question.QuestionFactory;

/**
 * Initialization classes for ease of use
 */
public final class Menus {
    public static Menu.MenuBuilder buildRootMenu(final Activity activity,
                                                 final Mode.ModeBuilder builder) {
        Menu.MenuBuilder rootMenuBuilder = new Menu.MenuBuilder()
                .addChoice(R.string.unlimited_button,
                        new MenuChoice() {
                            @Override
                            public void onSelect() {
                                builder.withGameType(GameType.Name.UNLIMITED);
                            }
                        })
                .addChoice(R.string.best_of_10_button,
                        new MenuChoice() {
                            @Override
                            public void onSelect() {
                                builder.withGameType(GameType.Name.BEST_OF_10);
                            }
                        })
                .addChoice(R.string.to_the_death_button,
                        new MenuChoice() {
                            @Override
                            public void onSelect() {
                                builder.withGameType(GameType.Name.CHALLENGE);
                            }
                        });
        return rootMenuBuilder;
    }

    public static Menu.MenuBuilder buildDifficultyMenu(final Mode.ModeBuilder builder) {
        Menu.MenuBuilder difficultyMenuBuilder = new Menu.MenuBuilder()
                .addChoice(R.string.easy_mode,
                        new MenuChoice() {
                            @Override
                            public void onSelect() {
                                builder.withDifficulty(Mode.Difficulty.EASY);
                            }
                        })
                .addChoice(R.string.moderate_mode,
                        new MenuChoice() {
                            @Override
                            public void onSelect() {
                                builder.withDifficulty(Mode.Difficulty.MODERATE);
                            }
                        })
                .addChoice(R.string.hard_mode,
                        new MenuChoice() {
                            @Override
                            public void onSelect() {
                                builder.withDifficulty(Mode.Difficulty.HARD);
                            }
                        });
        return difficultyMenuBuilder;
    }

    public static Menu.MenuBuilder buildQuizTypeMenu(final Mode.ModeBuilder builder) {
        Menu.MenuBuilder quizTypeMenu = new Menu.MenuBuilder()
                .addChoice(R.string.states_quiz_type,
                        new MenuChoice() {
                            @Override
                            public void onSelect() {
                                builder.withQuizType(QuestionFactory.QuizType.US_STATE);
                            }
                        })
                .addChoice(R.string.european_country_quiz_type,
                        new MenuChoice() {
                            @Override
                            public void onSelect() {
                                builder.withQuizType(QuestionFactory.QuizType.EUROPE_COUNTRY);
                            }
                        });
        return quizTypeMenu;
    }

}
