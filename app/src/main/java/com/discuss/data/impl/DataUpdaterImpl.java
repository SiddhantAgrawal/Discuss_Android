package com.discuss.data.impl;

import android.util.Pair;

import com.discuss.data.DataUpdater;
import com.discuss.data.DiscussService;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Response;
import com.discuss.datatypes.request.CommentAdditionRequest;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;


public class DataUpdaterImpl implements DataUpdater {
    private final DiscussService discussService;

    @Inject
    public DataUpdaterImpl(final DiscussService discussService) {
        this.discussService = discussService;
    }
    @Override
    public Single<Pair<Integer, Boolean>> likeQuestion(int questionId, int userId) {
        //return discussService.likeQuestion("question/upvote?questionId=" + questionId + "&userId=" + userId).map(r -> new Pair<>(questionId, r.getData()));
        return Single.just(new Pair<>(questionId, true));
    }

    @Override
    public Single<Pair<Integer, Boolean>> likeComment(int questionId, int userId) {
        //return discussService.likeComment("comment/upvote?questionId=" + questionId + "&userId=" + userId).map(r -> new Pair<>(questionId, r.getData()));
        return Single.just(new Pair<>(questionId, true));
    }

    @Override
    public Single<Pair<Integer, Boolean>> bookmarkQuestion(int questionId, int userId) {
        //return discussService.bookmarkQuestion("bookmark/question?questionId=" + questionId + "&userId=" + userId).map(r -> new Pair<>(questionId, r.getData()));
        return Single.just(new Pair<>(questionId, true));
    }

    @Override
    public Single<Pair<Integer, Boolean>> unlikeQuestion(int questionId, int userId) {
        return Single.just(new Pair<>(questionId, true));
    }

    @Override
    public Single<Pair<Integer, Boolean>> unlikeComment(int questionId, int userId) {
        return Single.just(new Pair<>(questionId, true));
    }

    @Override
    public Single<Pair<Integer, Boolean>> unbookmarkQuestion(int questionId, int userId) {
        return Single.just(new Pair<>(questionId, true));
    }

    @Override
    public Single<Comment> newUserComment(CommentAdditionRequest commentAdditionRequest) {
        return Single.just(Comment.builder().build());
    }
}
