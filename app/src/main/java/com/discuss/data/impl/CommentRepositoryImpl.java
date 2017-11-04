package com.discuss.data.impl;

import android.util.Pair;

import com.discuss.data.CommentRepository;
import com.discuss.data.DataRetriever;
import com.discuss.data.QuestionRepository;
import com.discuss.data.SortBy;
import com.discuss.data.SortOrder;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.data.StateDiff;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

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
                                 final String userName,
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
            return this.state.putIfAbsent(kth, dataRetriever.kthCommentForQuestion(kth, questionID, userID, sortBy.name(), sortOrder.name()).cache());
        } else {
            this.state.updateType(sortOrder, sortBy, questionID);
            return dataRetriever.getCommentsForQuestion(questionID,0, kth + 1, userID, sortBy.name(), sortOrder.name())
                    .flatMap(Observable::from)
                    .zipWith(Observable.range(0, kth), (comment, id) -> new Pair<Integer, Comment>(id, comment))
                    .doOnNext(pair -> this.state.putIfAbsent(pair.first + kth, Observable.just(pair.second).cache()))
                    .last()
                    .map(pair -> pair.second)
                    .cache();
        }
    }

    @Override
    public Observable<Question> getQuestion(int kth, SortBy sortBy, SortOrder sortOrder) {
        return Observable.just(this.state.question);
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
        this.state.updateType(sortOrder, sortBy, questionID);
        ensureKMoreComments(10, onCompleted);
    }


    public synchronized void ensureKMoreComments(int k, Action0 onCompleted) {
        if(this.state.updateInProcess) {
            onCompleted.call();
        }
        this.state.updateInProcess = true;
        int offset = this.state.maxRank + 1;
        dataRetriever.getCommentsForQuestion(this.state.question.getQuestionId(), offset, k, userID, this.state.sortBy.name(), this.state.sortOrder.name())
                .flatMap(Observable::from)
                .zipWith(Observable.range(offset, k), (comment, id) -> new Pair<Integer, Comment>(id, comment))
                .subscribe(new Subscriber<Pair<Integer, Comment>>() {
                    @Override
                    public void onCompleted() {
                        CommentRepositoryImpl.this.state.updateInProcess = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Pair<Integer, Comment> rankCommentPair) {
                        CommentRepositoryImpl.this.state.putIfAbsent(rankCommentPair.first, Observable.just(rankCommentPair.second).cache());
                    }
                });

    }

    @Override
    public int estimatedSize() {
        return this.state.maxRank;
    }

    private final class State {
        private volatile boolean updateInProcess;
        private volatile int maxRank;
        private Map<Integer, Observable<Comment>> commentRankMap;
        private volatile SortBy sortBy;
        private volatile SortOrder sortOrder;
        private Map<Integer, Comment> commentIDMap;
        private volatile Question question;

        State() {
            this.commentRankMap = new ConcurrentHashMap<>();
            commentIDMap = new ConcurrentHashMap<>();
            this.sortBy = SortBy.LIKES;
            this.sortOrder = SortOrder.DESC;
            this.updateInProcess = false;
            this.maxRank = -1;
        }
        synchronized Observable<Comment> putIfAbsent(int rank, Observable<Comment> commentObservable) {
            commentRankMap.putIfAbsent(rank, commentObservable);
            maxRank = Math.max(rank, maxRank);
            commentObservable.doOnNext(comment -> commentIDMap.put(comment.getCommentId(), comment));
            return commentRankMap.get(rank);
        }

        public synchronized void clear() {
            this.sortBy = null;
            this.sortOrder = null;
            this.commentRankMap = null;
            this.updateInProcess = false;
            this.maxRank = -1;
        }
        synchronized void updateType(SortOrder sortOrder, SortBy sortBy, int questionID) {
            this.sortBy = sortBy;
            this.sortOrder = sortOrder;
            this.commentRankMap = null;
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

        synchronized Optional<Comment> getComment(final int id) {
            return Optional.ofNullable(commentIDMap.get(id));
        }
        synchronized void putInCachedComments(Comment comment) {
            commentIDMap.put(comment.getCommentId(), comment);
        }
    }

}