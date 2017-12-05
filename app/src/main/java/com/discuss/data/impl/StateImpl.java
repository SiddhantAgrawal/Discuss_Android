package com.discuss.data.impl;

import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.util.Pair;

import com.discuss.data.DataUpdater;
import com.discuss.data.StateDiff;
import com.discuss.datatypes.Comment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */

public class StateImpl implements StateDiff {
    private static final Object PRESENT = new Object();

    private final Map<Integer, Object> likedQuestions;
    private final Map<Integer, Object> undoLikedQuestions;
    private final Map<Integer, Object> bookmarkedQuestions;
    private final Map<Integer, Object> undoBookmarkedQuestions;
    private final Map<Integer, Object> likedComments;
    private final Map<Integer, Object> undoLikedComments;
    private final Map<Integer, String> pendingEditedComments;
    private final int userId;
    private final DataUpdater dataUpdater;

    @Inject
    public StateImpl(final DataUpdater dataUpdater) {
        this.likedQuestions = new ConcurrentHashMap<>();
        this.undoLikedQuestions = new ConcurrentHashMap<>();
        this.bookmarkedQuestions = new ConcurrentHashMap<>();
        this.undoBookmarkedQuestions = new ConcurrentHashMap<>();
        this.likedComments = new ConcurrentHashMap<>();
        this.undoLikedComments = new ConcurrentHashMap<>();
        this.pendingEditedComments = new ConcurrentHashMap<>();
        this.userId = 0; /* @todo(deepak): Fix this **/
        this.dataUpdater = dataUpdater;
    }

    @Override
    public void likeQuestion(int questionId) {
        addToFirstAndRemoveFromSecond(questionId, likedQuestions, undoLikedQuestions);
    }

    @Override
    public void undoLikeForQuestion(int questionId) {
        addToFirstAndRemoveFromSecond(questionId, undoLikedQuestions, likedQuestions);
    }

    @Override
    public void bookmarkQuestion(int questionId) {
        addToFirstAndRemoveFromSecond(questionId, bookmarkedQuestions, undoBookmarkedQuestions);
    }

    @Override
    public void undoBookmarkForQuestion(int questionId) {
        addToFirstAndRemoveFromSecond(questionId, undoBookmarkedQuestions, bookmarkedQuestions);
    }

    @Override
    public void likeComment(int commentId) {
        addToFirstAndRemoveFromSecond(commentId, likedComments, undoLikedComments);
    }

    @Override
    public void undoLikeForComment(int commentId) {
        addToFirstAndRemoveFromSecond(commentId, undoLikedComments, likedComments);
    }

    @Override
    public boolean isQuestionLiked(int questionId) {
        return likedQuestions.containsKey(questionId);
    }

    @Override
    public boolean isQuestionUnLiked(int questionId) {
        return undoLikedQuestions.containsKey(questionId);
    }

    @Override
    public boolean isQuestionBookMarked(int questionId) {
        return bookmarkedQuestions.containsKey(questionId);
    }

    @Override
    public boolean isQuestionUnBookMarked(int questionId) {
        return undoBookmarkedQuestions.containsKey(questionId);
    }

    @Override
    public boolean isCommentLiked(int commentId) {
        return likedComments.containsKey(commentId);
    }

    @Override
    public void updateCommentText(int commentID, String comment) {
        this.pendingEditedComments.put(commentID, comment);
    }

    @Override
    public Observable<Pair<Integer,Boolean>> flushLikeStateDiffForQuestions() {
        return flush(() -> {},
                likedQuestions,
                questionId -> dataUpdater.likeQuestion(questionId, userId).toObservable(),
                undoLikedQuestions,
                questionId -> dataUpdater.unlikeComment(questionId, userId).toObservable());
    }

    @Override
    public Observable<Pair<Integer,Boolean>> flushLikeStateDiffForComments() {
        return flush(() -> {},
                likedComments,
                commentId -> dataUpdater.likeComment(commentId, userId).toObservable(),
                undoLikedComments,
                commentId -> dataUpdater.unlikeComment(commentId, userId).toObservable());
    }

    @Override
    public Observable<Pair<Integer,Boolean>> flushBookmarkedStateDiffForQuestions() {
        return flush(() -> {},
                bookmarkedQuestions,
                questionId -> dataUpdater.bookmarkQuestion(questionId, userId).toObservable(),
                undoBookmarkedQuestions,
                questionId -> dataUpdater.unbookmarkQuestion(questionId, userId).toObservable());
    }

    @Override
    public Single<Boolean> flushAll() {
        return Observable.merge(flushBookmarkedStateDiffForQuestions(), flushLikeStateDiffForComments(), flushLikeStateDiffForQuestions())
                .count()
                .map(a -> a >= 0)
                .toSingle();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    <K> Observable<Pair<K,Boolean>> flush(Action0 onCompleted, Map<K, Object> entity, Func1<K, Observable<Pair<K, Boolean>>> entityfunc1, Map<K, Object> undoEntity, Func1<K, Observable<Pair<K, Boolean>>> undoEntityfunc2) {
        Set<K> entityCopy = new HashSet<>(entity.keySet());
        Set<K> undoEntityCopy = new HashSet<>(undoEntity.keySet());
        entity.clear();
        undoEntity.clear();

        Observable<Pair<K, Boolean>> observable1 = Observable.from(entityCopy).
                flatMap(entityfunc1).doOnNext(new Action1<Pair<K, Boolean>>() {
            @Override
            public void call(Pair<K, Boolean> response) {
                if (!response.second) {
                    entity.put(response.first, PRESENT); /* will retry next time */
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        Observable<Pair<K, Boolean>> observable2 = Observable.from(undoEntityCopy).
                flatMap(undoEntityfunc2).doOnNext(new Action1<Pair<K, Boolean>>() {
            @Override
            public void call(Pair<K, Boolean> response) {
                if (!response.second) {
                    undoEntity.put(response.first, PRESENT); /* will retry next time */
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        Observable<Pair<K, Boolean>> mergedObservable = Observable.merge(observable1, observable2);
        return mergedObservable.cache();

    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    <K> void addToFirstAndRemoveFromSecond(K key, Map<K, Object> firstEntity, Map<K, Object> secondEntity) {
        firstEntity.putIfAbsent(key, PRESENT);
        secondEntity.remove(key);
    }
}
