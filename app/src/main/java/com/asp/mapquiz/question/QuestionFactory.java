package com.asp.mapquiz.question;

import com.asp.mapquiz.question.Question;

import java.util.HashMap;
import java.util.Map;

public class QuestionFactory {
    public static interface QuestionTypeConstructor {
        Question createQuestion();
    }

    public enum QuizType {
        STATE(0);

        private int mId;

        private QuizType(int i) {
            mId = i;
        }

        public int getId() {
            return mId;
        }
    }

    private static Map<QuizType, QuestionTypeConstructor> mFactories =
            new HashMap<QuizType, QuestionTypeConstructor>();

    public static Question createQuestion(QuizType type) {
        return mFactories.get(type).createQuestion();
    }

    public static void addQuestionFactory(QuizType type, QuestionTypeConstructor factory) {
        mFactories.put(type, factory);
    }
}
