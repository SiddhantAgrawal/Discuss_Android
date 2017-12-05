package com.discuss.data.impl;

import android.util.Log;
import android.util.Pair;

import com.discuss.data.CommentRepository;
import com.discuss.data.DataRetriever;
import com.discuss.data.DataUpdater;
import com.discuss.data.QuestionRepository;
import com.discuss.data.SortBy;
import com.discuss.data.SortOrder;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.data.StateDiff;
import com.discuss.datatypes.request.CommentAdditionRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */
public class CommentRepositoryImpl implements CommentRepository {
    private final DataRetriever dataRetriever;
    private final DataUpdater dataUpdater;
    private final StateDiff stateDiff;
    private final int userID;
    private final QuestionRepository questionRepository;
    private final State state;

    public CommentRepositoryImpl(DataRetriever dataRetriever,
                                 DataUpdater dataUpdater,
                                 StateDiff stateDiff,
                                 QuestionRepository questionRepository,
                                 final int userID) {
        this.dataRetriever = dataRetriever;
        this.dataUpdater = dataUpdater;
        this.stateDiff = stateDiff;
        this.state = new State();
        this.userID = userID;
        this.questionRepository = questionRepository;
    }

    @Override
    public Single<Comment> kthCommentForQuestion(int kth, int questionID, SortBy sortBy, SortOrder sortOrder) {

        Observable<Comment> alternative = this.stateDiff.flushAll()
                .doOnSuccess((b) -> this.state.updateType(sortOrder, sortBy, questionID))
                .flatMap(aBoolean -> state.kthComment(kth)).toObservable();

        return Observable.just(this.state)
                .filter(s -> s.getSortOrder() == sortOrder && s.getSortBy() == sortBy)
                .flatMap(state -> state.question.map(q -> new Pair<State, Question>(state, q)).toObservable())
                .filter(p -> p.second.getQuestionId() == questionID)
                .flatMap(p -> p.first.kthComment(kth).toObservable())
                .switchIfEmpty(alternative)
                .toSingle();
    }

    @Override
    public Single<Question> getQuestion(int kth, SortBy sortBy, SortOrder sortOrder) {
        return this.state.question;
    }

    @Override
    public Single<Question> getQuestionWithID(int id) {
        return questionRepository.getQuestionWithID(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<Boolean> likeCommentWithID(int commentID) {
        Optional<Comment> comment = this.state.getComment(commentID);
        if(comment.isPresent()) {
            Comment comment1 = comment.get();
            comment1.setLiked(true);
        }
        stateDiff.likeComment(commentID);
        return Single.just(true);
    }

    @Override
    public Single<Boolean> unlikeCommentWithID(int commentID) {
        Optional<Comment> comment = this.state.getComment(commentID);
        if(comment.isPresent()) {
            Comment comment1 = comment.get();
            comment1.setLiked(false);
        }
        stateDiff.undoLikeForComment(commentID);
        return Single.just(true);
    }

    @Override
    public Single<Comment> userAddedComment(final int questionID) {
        Observable<Comment> alternative = this.stateDiff.flushAll()
                .doOnSuccess((b) -> this.state.updateType(this.state.sortOrder, this.state.sortBy, questionID))
                .flatMap(aBoolean -> state.userAddedComment())
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();

        return Observable.just(this.state)
                .flatMap(state -> state.question.map(q -> new Pair<State, Question>(state, q)).toObservable())
                .filter(p -> p.second.getQuestionId() == questionID)
                .flatMap(p -> p.first.userAddedComment().toObservable().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .switchIfEmpty(alternative)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .toSingle();
    }

    @Override
    public Single<Comment> newUserComment(CommentAdditionRequest comment) {
        return this.stateDiff.flushAll().flatMap(aBoolean -> CommentRepositoryImpl.this.state.newUserComment(comment));
    }

    @Override
    public void init(Action0 onCompleted, SortBy sortBy, SortOrder sortOrder, int questionID) {
        this.stateDiff.flushAll().subscribe(a -> {
            this.state.updateType(sortOrder, sortBy, questionID);
            ensureKMoreComments(onCompleted, () -> {});
        });
    }

    @Override
    public Single<Boolean> save() {
        return this.stateDiff.flushAll();
    }


    @Override
    public synchronized void ensureKMoreComments(Action0 onCompleted, Action0 onNoUpdate) {
        this.state.ensureMoreComments(onCompleted, onNoUpdate);
    }

    @Override
    public int estimatedSize() {
        return this.state.size;
    }

    @Override
    public Single<Comment> updateCommentText(int commentID, String text) {
        return this.state.updateCommentText(commentID, text).doOnSuccess(new Action1<Comment>() {
            @Override
            public void call(Comment comment) {
                CommentRepositoryImpl.this.stateDiff.updateCommentText(commentID, text);
            }
        });
    }

    @Override
    public boolean isFurtherLoadingPossible() {
        return this.state.isFurtherLoadingPossible();
    }

    private final class State {
        private volatile int slab;
        private Map<Integer, Single<List<Comment>>> commentRankMap;
        private volatile SortBy sortBy;
        private volatile SortOrder sortOrder;
        private Map<Integer, Comment> commentIDMap;
        private volatile Single<Question> question;
        private volatile Single<Comment> userComment;
        private volatile int size;
        private volatile boolean isFurtherLoadingPossible;

        State() {
            this.commentRankMap = new ConcurrentHashMap<>();
            commentIDMap = new ConcurrentHashMap<>();
            this.sortBy = SortBy.LIKES;
            this.sortOrder = SortOrder.DESC;
            this.slab = 0;
            this.size = 0;
            this.isFurtherLoadingPossible = true;
            this.userComment = null;
        }

        synchronized boolean isFurtherLoadingPossible() {
            return this.isFurtherLoadingPossible;
        }

        Single<List<Comment>> getComments(int slabId) {
            return question.flatMap(new Func1<Question, Single<List<Comment>>>() {
                @Override
                public Single<List<Comment>> call(Question question) {
                    return dataRetriever.getCommentsForQuestion(question.getQuestionId(), slabId * 10, 10, userID, sortBy.name(), sortOrder.name());
                };
            }).doOnSuccess(list -> list.forEach(comment -> commentIDMap.put(comment.getCommentId(), comment)))
                    .doOnSuccess(list -> slab = Math.max(slab, slabId + 1))
                    .doOnSuccess(list -> size += list.size())
                    .doOnSuccess(list -> isFurtherLoadingPossible = (list.size() == 10))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toObservable()
                    .cacheWithInitialCapacity(1)
                    .toSingle();
        }

        public synchronized void clear() {
            this.sortBy = null;
            this.sortOrder = null;
            this.commentRankMap = new ConcurrentHashMap<>();
            this.slab = 0;
            this.size = 0;
            this.isFurtherLoadingPossible = true;
            this.userComment = null;
        }
        synchronized void updateType(SortOrder sortOrder, SortBy sortBy, int questionID) {
            this.sortBy = sortBy;
            this.sortOrder = sortOrder;
            this.commentRankMap = new ConcurrentHashMap<>();
            this.slab = 0;
            this.size = 0;
            this.isFurtherLoadingPossible = true;
            this.question = CommentRepositoryImpl.this
                    .questionRepository
                    .getQuestionWithID(questionID);
            this.userComment = dataRetriever.getUserAddedComment(questionID, userID)
                    .toObservable()
                    .cache()
                    .toSingle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        synchronized SortOrder getSortOrder() {
            return this.sortOrder;
        }
        synchronized SortBy getSortBy() {
            return this.sortBy;
        }

        synchronized Single<Comment> kthComment(final int rank) {
            final int mapIndex = rank/10;
            final int localIndex = rank%10;
            final Single<List<Comment>> commentObservable = getComments(mapIndex);
            commentRankMap.putIfAbsent(mapIndex, commentObservable);
            return commentRankMap.get(mapIndex).map(list -> list.size() > 0 ? list.get(localIndex) : null);
        }

        synchronized void ensureMoreComments(Action0 onCompleted, Action0 onNoUpdate) {
            if (!this.isFurtherLoadingPossible) {
               onNoUpdate.call();
               return;
            }
            int currentSlabId = this.slab;
            final Single<List<Comment>> questionObservable = getComments(currentSlabId);
            commentRankMap.putIfAbsent(currentSlabId, questionObservable);
            commentRankMap.get(currentSlabId).subscribe(a -> onCompleted.call(), e -> {});
        }

        synchronized Optional<Comment> getComment(final int id) {
            return Optional.ofNullable(commentIDMap.get(id));
        }

        synchronized void putInCachedComments(Comment comment) {
            commentIDMap.put(comment.getCommentId(), comment);
        }

        public Single<Comment> userAddedComment() {
            return userComment;
        }

        public Single<Comment> updateCommentText(int commentID, String text) {
            return this.userComment.doOnSuccess(c -> c.setText(text));
        }

        synchronized Single<Comment> newUserComment(CommentAdditionRequest commentAdditionRequest) {
            this.userComment = dataUpdater.newUserComment(commentAdditionRequest);
            return this.userComment;
        }

    }

}