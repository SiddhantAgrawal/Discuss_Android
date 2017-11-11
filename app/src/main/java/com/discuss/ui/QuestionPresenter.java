package com.discuss.ui;

import rx.Observable;

/**
 *
 * @author Deepak Thakur
 */
public interface QuestionPresenter {

    Observable<Boolean> likeQuestionWithID(final int questionID);

    Observable<Boolean> unlikeQuestionWithID(final int questionID);

    Observable<Boolean> bookmarkQuestionWithID(final int questionID);

    Observable<Boolean> unbookmarkQuestionWithID(final int questionID);

}
