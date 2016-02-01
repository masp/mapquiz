package com.asp.mapquiz.question;

/**
 * An option represents possible answers to a question. For example, a possible option for a
 * <see>StateQuestion</see> would be California.
 */
public interface Option {
    Option NULL = new Option() {
        @Override
        public String getName() {
            return "NULL";
        }
    };

    public String getName();
}
