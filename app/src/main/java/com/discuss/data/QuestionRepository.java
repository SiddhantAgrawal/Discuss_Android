package com.discuss.data;


import com.discuss.datatypes.Question;

import java.util.function.Consumer;

import rx.functions.Action0;


public interface QuestionRepository {
    void getQuestionWithRank(final int kth, final SortBy sortBy, final SortOrder sortOrder, Consumer<Question> consumer);
    void getQuestionWithID(final int questionID, Consumer<Question> consumer);
    void likeQuestionWithID(final int questionID, final Action0 onCompleted);
    void unlikeQuestionWithID(final int questionID, final Action0 onCompleted);
    void bookmarkQuestionWithID(final int questionID, final Action0 onCompleted);
    void unbookmarkQuestionWithID(final int questionID, final Action0 onCompleted);
}
