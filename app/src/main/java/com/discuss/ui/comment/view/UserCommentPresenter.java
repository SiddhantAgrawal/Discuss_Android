package com.discuss.ui.comment.view;

import com.discuss.ui.CommentSummary;

import rx.Observable;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 */
public interface UserCommentPresenter {
    Observable<CommentSummary> init(int questionID);
    void setEditedComment(final String editedComment);
    String getEditedComment();
    void save();
}
