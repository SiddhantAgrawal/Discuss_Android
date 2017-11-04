package com.discuss.data;

import android.util.Pair;

import rx.Observable;

public interface DataUpdater {

    Observable<Pair<Integer, Boolean>> likeQuestion(final int questionId, final int userId);

    Observable<Pair<Integer, Boolean>> likeComment( final int questionId, final int userId);

    Observable<Pair<Integer, Boolean>> bookmarkQuestion( final int questionId, final int userId);

    Observable<Pair<Integer, Boolean>> unlikeQuestion(final int questionId, final int userId);

    Observable<Pair<Integer, Boolean>> unlikeComment( final int questionId, final int userId);

    Observable<Pair<Integer, Boolean>> unbookmarkQuestion( final int questionId, final int userId);

}
