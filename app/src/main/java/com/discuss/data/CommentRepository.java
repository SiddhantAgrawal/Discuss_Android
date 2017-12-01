package com.discuss.data;


import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;

import java.util.Optional;
import java.util.function.Consumer;

import rx.Observable;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 */

public interface CommentRepository {
    Observable<Comment> kthCommentForQuestion(final int kth, final int questionID, final SortBy sortBy, final SortOrder sortOrder);
    Observable<Question> getQuestion(final int kth, final SortBy sortBy, final SortOrder sortOrder);
    Observable<Question> getQuestionWithID(final int id);
    Observable<Boolean> likeCommentWithID(final int questionID);
    Observable<Boolean> unlikeCommentWithID(final int questionID);
    Observable<Comment> userAddedComment(final int questionID);
    int estimatedSize();

    void updateCommentText(int commentID, String text);
    boolean isFurtherLoadingPossible();
    void ensureKMoreComments(Action0 onCompleted, Action0 onNoUpdate);
    void init(Action0 onCompleted, SortBy sortBy, SortOrder sortOrder, int questionID);
}
