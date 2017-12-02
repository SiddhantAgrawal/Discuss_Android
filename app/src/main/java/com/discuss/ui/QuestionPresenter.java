package com.discuss.ui;

import rx.Observable;
import rx.Single;

/**
 *
 * @author Deepak Thakur
 */
public interface QuestionPresenter {

    Single<Boolean> likeQuestionWithID(final int questionID);

    Single<Boolean> unlikeQuestionWithID(final int questionID);

    Single<Boolean> bookmarkQuestionWithID(final int questionID);

    Single<Boolean> unbookmarkQuestionWithID(final int questionID);

}
