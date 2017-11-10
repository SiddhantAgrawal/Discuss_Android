package com.discuss.data;

import com.discuss.datatypes.Question;

import rx.Observable;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 */
public interface QuestionsAnsweredRepository {

    Observable<Question> kthQuestion(final int kth);

    Observable<Question> getQuestionWithID(final int questionID);

    Observable<Boolean> likeQuestionWithID(final int questionID);

    Observable<Boolean> unlikeQuestionWithID(final int questionID);

    Observable<Boolean> bookmarkQuestionWithID(final int questionID);

    Observable<Boolean> unbookmarkQuestionWithID(final int questionID);

    void ensureKMoreQuestions(Action0 onCompleted);

    int estimatedSize();

    void init(Action0 onCompleted);
}
