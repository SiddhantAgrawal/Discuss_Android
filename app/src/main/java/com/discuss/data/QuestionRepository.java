package com.discuss.data;

import com.discuss.datatypes.Question;

import rx.Observable;
import rx.Single;
import rx.functions.Action0;

/**
 * @author Deepak Thakur
 */
public interface QuestionRepository {

    Single<Question> kthQuestion(final int kth, final SortBy sortBy, final SortOrder sortOrder);

    Single<Question> getQuestionWithID(final int questionID);

    Single<Boolean> likeQuestionWithID(final int questionID);

    Single<Boolean> unlikeQuestionWithID(final int questionID);

    Single<Boolean> bookmarkQuestionWithID(final int questionID);

    Single<Boolean> unbookmarkQuestionWithID(final int questionID);

    void ensureKMoreQuestions(Action0 onCompleted);

    int estimatedSize();

    void init(Action0 onCompleted, SortBy sortBy, SortOrder sortOrder);
}
