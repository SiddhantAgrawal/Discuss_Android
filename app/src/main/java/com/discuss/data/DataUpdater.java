package com.discuss.data;

import android.util.Pair;

import com.discuss.datatypes.Comment;
import com.discuss.datatypes.request.CommentAdditionRequest;

import rx.Observable;
import rx.Single;

public interface DataUpdater {

    Single<Pair<Integer, Boolean>> likeQuestion(final int questionId, final int userId);

    Single<Pair<Integer, Boolean>> likeComment( final int questionId, final int userId);

    Single<Pair<Integer, Boolean>> bookmarkQuestion( final int questionId, final int userId);

    Single<Pair<Integer, Boolean>> unlikeQuestion(final int questionId, final int userId);

    Single<Pair<Integer, Boolean>> unlikeComment( final int questionId, final int userId);

    Single<Pair<Integer, Boolean>> unbookmarkQuestion( final int questionId, final int userId);

    Single<Comment> newUserComment(CommentAdditionRequest commentAdditionRequest);

}
