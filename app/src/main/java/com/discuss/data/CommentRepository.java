package com.discuss.data;


import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.datatypes.request.CommentAdditionRequest;

import java.util.Optional;
import java.util.function.Consumer;

import rx.Observable;
import rx.Single;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 */

public interface CommentRepository {
    Single<Comment> kthCommentForQuestion(final int kth, final int questionID, final SortBy sortBy, final SortOrder sortOrder);
    Single<Question> getQuestion(final int kth, final SortBy sortBy, final SortOrder sortOrder);
    Single<Question> getQuestionWithID(final int id);
    Single<Boolean> likeCommentWithID(final int questionID);
    Single<Boolean> unlikeCommentWithID(final int questionID);
    Single<Comment> userAddedComment(final int questionID);
    Single<Comment> newUserComment(final CommentAdditionRequest comment);
    int estimatedSize();

    void updateCommentText(int commentID, String text);
    boolean isFurtherLoadingPossible();
    void ensureKMoreComments(Action0 onCompleted, Action0 onNoUpdate);
    void init(Action0 onCompleted, SortBy sortBy, SortOrder sortOrder, int questionID);
    Single<Boolean> save();

}
