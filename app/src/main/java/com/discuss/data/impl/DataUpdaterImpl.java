package com.discuss.data.impl;

import android.util.Pair;

import com.discuss.data.DataUpdater;
import com.discuss.data.DiscussService;
import com.discuss.datatypes.Response;

import javax.inject.Inject;

import rx.Observable;


public class DataUpdaterImpl implements DataUpdater {
    private final DiscussService discussService;

    @Inject
    public DataUpdaterImpl(final DiscussService discussService) {
        this.discussService = discussService;
    }
    @Override
    public Observable<Pair<Integer, Boolean>> likeQuestion(int questionId, int userId) {
        //return discussService.likeQuestion("question/upvote?questionId=" + questionId + "&userId=" + userId).map(r -> new Pair<>(questionId, r.getData()));
        return Observable.just(new Pair<>(questionId, true));
    }

    @Override
    public Observable<Pair<Integer, Boolean>> likeComment(int questionId, int userId) {
        //return discussService.likeComment("comment/upvote?questionId=" + questionId + "&userId=" + userId).map(r -> new Pair<>(questionId, r.getData()));
        return Observable.just(new Pair<>(questionId, true));
    }

    @Override
    public Observable<Pair<Integer, Boolean>> bookmarkQuestion(int questionId, int userId) {
        //return discussService.bookmarkQuestion("bookmark/question?questionId=" + questionId + "&userId=" + userId).map(r -> new Pair<>(questionId, r.getData()));
        return Observable.just(new Pair<>(questionId, true));
    }

    @Override
    public Observable<Pair<Integer, Boolean>> unlikeQuestion(int questionId, int userId) {
        return Observable.just(new Pair<>(questionId, true));
    }

    @Override
    public Observable<Pair<Integer, Boolean>> unlikeComment(int questionId, int userId) {
        return Observable.just(new Pair<>(questionId, true));
    }

    @Override
    public Observable<Pair<Integer, Boolean>> unbookmarkQuestion(int questionId, int userId) {
        return Observable.just(new Pair<>(questionId, true));
    }
}
