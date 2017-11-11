package com.discuss.data.impl;

import android.util.Log;

import com.discuss.data.CommentRepository;
import com.discuss.data.DataRetriever;
import com.discuss.data.QuestionRepository;
import com.discuss.data.SortBy;
import com.discuss.data.SortOrder;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.data.StateDiff;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */
public class CommentRepositoryImpl implements CommentRepository {
    private final DataRetriever dataRetriever;
    private final StateDiff stateDiff;
    private final int userID;
    private final QuestionRepository questionRepository;
    private final State state;

    public CommentRepositoryImpl(DataRetriever dataRetriever,
                                 StateDiff stateDiff,
                                 QuestionRepository questionRepository,
                                 final int userID) {
        this.dataRetriever = dataRetriever;
        this.stateDiff = stateDiff;
        this.state = new State();
        this.userID = userID;
        this.questionRepository = questionRepository;
    }

    @Override
    public Observable<Comment> kthCommentForQuestion(int kth, int questionID, SortBy sortBy, SortOrder sortOrder) {
        if (this.state.getSortOrder() == sortOrder &&
            this.state.getSortBy() == sortBy &&
            this.state.question != null &&
            this.state.question.getQuestionId() == questionID) {
            return this.state.kthComment(kth);
        } else {
            this.state.updateType(sortOrder, sortBy, questionID);
            return this.state.kthComment(kth);
        }
    }

    @Override
    public Observable<Question> getQuestion(int kth, SortBy sortBy, SortOrder sortOrder) {
        return Observable.just(this.state.question);
    }

    @Override
    public Observable<Question> getQuestionWithID(int id) {
        return questionRepository.getQuestionWithID(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public Observable<Boolean> likeCommentWithID(int commentID) {
        Optional<Comment> comment = this.state.getComment(commentID);
        if(comment.isPresent()) {
            Comment comment1 = comment.get();
            comment1.setLiked(true);
        }
        stateDiff.likeComment(commentID);
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> unlikeCommentWithID(int commentID) {
        Optional<Comment> comment = this.state.getComment(commentID);
        if(comment.isPresent()) {
            Comment comment1 = comment.get();
            comment1.setLiked(false);
        }
        stateDiff.undoLikeForComment(commentID);
        return Observable.just(true);
    }

    @Override
    public void init(Action0 onCompleted, SortBy sortBy, SortOrder sortOrder, int questionID) {
        Log.e("commentRepo", "inside init");
        this.state.updateType(sortOrder, sortBy, questionID);
        ensureKMoreComments(onCompleted);
    }




    public synchronized void ensureKMoreComments(Action0 onCompleted) {
        this.state.ensureMoreComments(onCompleted);
    }

    @Override
    public int estimatedSize() {
        return this.state.slab*10;
    }

    private final class State {
        private volatile int slab;
        private Map<Integer, Observable<List<Comment>>> commentRankMap;
        private volatile SortBy sortBy;
        private volatile SortOrder sortOrder;
        private Map<Integer, Comment> commentIDMap;
        private volatile Question question;

        State() {
            this.commentRankMap = new ConcurrentHashMap<>();
            commentIDMap = new ConcurrentHashMap<>();
            this.sortBy = SortBy.LIKES;
            this.sortOrder = SortOrder.DESC;
            this.slab = 0;
        }

        Observable<List<Comment>> getComments(int slabId) {
            return Observable.create(new Observable.OnSubscribe<List<Comment>>() {
                @Override
                public void call(Subscriber<? super List<Comment>> subscriber) {
                    dataRetriever.getCommentsForQuestion(question.getQuestionId(), slabId * 10, 10, userID, sortBy.name(), sortOrder.name()).
                            subscribe(subscriber);
                }
            }).doOnNext(list -> list.forEach(comment -> commentIDMap.put(comment.getCommentId(), comment))).
                    doOnNext(list -> slab = Math.max(slab, slabId + 1)).
                    doOnNext(l -> Log.e("commentRepo", "in on next")).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    cache();
        }

        public synchronized void clear() {
            this.sortBy = null;
            this.sortOrder = null;
            this.commentRankMap = new ConcurrentHashMap<>();
            this.slab = 0;
        }
        synchronized void updateType(SortOrder sortOrder, SortBy sortBy, int questionID) {
            this.sortBy = sortBy;
            this.sortOrder = sortOrder;
            this.commentRankMap = new ConcurrentHashMap<>();
            this.slab = 0;
            this.question = CommentRepositoryImpl.this
                    .questionRepository
                    .getQuestionWithID(questionID)
                    .toBlocking()
                    .first();
        }
        synchronized SortOrder getSortOrder() {
            return this.sortOrder;
        }
        synchronized SortBy getSortBy() {
            return this.sortBy;
        }

        synchronized Observable<Comment> kthComment(final int rank) {
            final int mapIndex = rank/10;
            final int localIndex = rank%10;
            final Observable<List<Comment>> commentObservable = getComments(mapIndex);
            commentRankMap.putIfAbsent(mapIndex, commentObservable);
            return commentRankMap.get(mapIndex).map(list -> list.get(localIndex));
        }

        synchronized void ensureMoreComments(Action0 onCompleted) {
            Log.e("CommentRepo", "inside the ensure");
            int currentSlabId = this.slab;
            final Observable<List<Comment>> questionObservable = getComments(currentSlabId);
            commentRankMap.putIfAbsent(currentSlabId, questionObservable);
            commentRankMap.get(currentSlabId).subscribe(a -> {}, e -> {}, onCompleted);
        }

        synchronized Optional<Comment> getComment(final int id) {
            return Optional.ofNullable(commentIDMap.get(id));
        }
        synchronized void putInCachedComments(Comment comment) {
            commentIDMap.put(comment.getCommentId(), comment);
        }
    }

}