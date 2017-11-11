package com.discuss.ui;

import rx.Observable;

/**
 *
 * @author Deepak Thakur
 */
public interface CommentPresenter {

    Observable<Boolean> likeCommentWithID(final int questionID);

    Observable<Boolean> unlikeCommentWithID(final int questionID);

}
