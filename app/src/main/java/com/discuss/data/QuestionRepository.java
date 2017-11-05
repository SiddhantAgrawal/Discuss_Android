package com.discuss.data;


import com.discuss.datatypes.Question;

import java.util.function.Consumer;

import rx.Observable;
import rx.functions.Action0;


public interface QuestionRepository {

    Observable<Question> kthQuestion(final int kth, final SortBy sortBy, final SortOrder sortOrder);

    Observable<Question> getQuestionWithID(final int questionID);

    Observable<Boolean> likeQuestionWithID(final int questionID);

    Observable<Boolean> unlikeQuestionWithID(final int questionID);

    Observable<Boolean> bookmarkQuestionWithID(final int questionID);

    Observable<Boolean> unbookmarkQuestionWithID(final int questionID);

    void ensureKMoreQuestions(int k, Action0 onCompleted);

    int estimatedSize();

    void init(Action0 onCompleted, SortBy sortBy, SortOrder sortOrder);
}