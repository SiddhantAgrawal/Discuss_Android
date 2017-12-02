package com.discuss.ui;

import rx.Observable;
import rx.Single;

/**
 *
 * @author Deepak Thakur
 */
public interface CommentPresenter {

    Single<Boolean> likeCommentWithID(final int questionID);

    Single<Boolean> unlikeCommentWithID(final int questionID);

}
