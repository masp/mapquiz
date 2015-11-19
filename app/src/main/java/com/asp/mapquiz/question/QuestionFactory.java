package com.asp.mapquiz.question;

import com.asp.mapquiz.game.Mode;

import java.util.HashMap;
import java.util.Map;

public class QuestionFactory {
    public interface QuestionTypeFactory {
        Question createQuestion(Mode.Difficulty difficulty);
    }

    public enum QuizType {
        US_STATE(0), EUROPE_COUNTRY(1);
        private int mId;

        QuizType(int i) {
            mId = i;
        }

        public int getId() {
            return mId;
        }
    }

    private static Map<QuizType, QuestionTypeFactory> mFactories =
            new HashMap<QuizType, QuestionTypeFactory>();

    public static Question createQuestion(QuizType type, Mode.Difficulty difficulty) {
        return mFactories.get(type).createQuestion(difficulty);
    }

    public static void addQuestionFactory(QuizType type, QuestionTypeFactory factory) {
        mFactories.put(type, factory);
    }
}
