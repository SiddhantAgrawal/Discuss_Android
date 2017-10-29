package com.discuss.state;


import com.discuss.datatypes.Comment;

import rx.Observable;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 *
 */
public interface StateDiff {


    void likeQuestion(final int questionId);

    void undoLikeForQuestion(final int questionId);

    void bookmarkQuestion(final int questionId);

    void undoBookmarkForQuestion(final int questionId);

    /**
     * like a comment.
     * @param commentId
     */
    void likeComment(final int commentId);
    /**
     * unlike a comment.
     * @param commentId
     */
    void undoLikeForComment(final int commentId);

    /**
     *
     * @param questionId
     *        the question Id
     * @return {@code true} if the question is liked after last sync with the backend servers and
     *         {@code false} otherwise
     */
    boolean isQuestionLiked(final int questionId);

    /**
     *
     * @param questionId
     *        the question Id
     * @return {@code true} if the question, previously liked is un liked after last sync with the backend servers and
     *         {@code false} otherwise
     */
    boolean isQuestionUnLiked(final int questionId);

    /**
     *
     * @param questionId
     *        the question Id
     * @return {@code true} if the question is bookmarked after last sync with the backend servers and
     *         {@code false} otherwise
     */

    boolean isQuestionBookMarked(final int questionId);

    /**
     *
     * @param questionId
     *        the question Id
     * @return {@code true} if the question,previously bookmarked is un bookmarked after last sync with the backend servers and
     *         {@code false} otherwise
     */

    boolean isQuestionUnBookMarked(final int questionId);

    boolean isCommentLiked(final int commentId);

    void flushLikeStateDiffForQuestions(Action0 onCompleted);

    void flushLikeStateDiffForComments(Action0 onCompleted);

    void flushBookmarkedStateDiffForQuestions(Action0 onCompleted);




}
