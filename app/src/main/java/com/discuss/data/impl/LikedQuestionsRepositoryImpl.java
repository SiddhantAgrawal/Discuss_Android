package com.discuss.data.impl;

import android.util.Log;
import android.util.Pair;

import com.discuss.data.DataRetriever;
import com.discuss.data.LikedQuestionsRepository;
import com.discuss.data.StateDiff;
import com.discuss.datatypes.Question;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 *
 * @author Deepak Thakur
 */
public class LikedQuestionsRepositoryImpl implements LikedQuestionsRepository{
    private final DataRetriever dataRetriever;
    private final StateDiff stateDiff;
    private final int userID;
    private final State state;
    private final class State {
        private volatile boolean updateInProcess;
        private volatile int maxRank;
        private Map<Integer, Observable<Question>> questionRankMap;
        private Map<Integer, Question> questionIDMap;
        State() {
            this.questionRankMap = new ConcurrentHashMap<>();
            questionIDMap = new ConcurrentHashMap<>();
            this.updateInProcess = false;
            this.maxRank = -1;
        }
        synchronized Observable<Question> putIfAbsent(int rank, Observable<Question> questionObservable) {
            questionRankMap.putIfAbsent(rank, questionObservable);
            maxRank = Math.max(rank, maxRank);
            questionObservable.doOnNext(question -> questionIDMap.put(question.getQuestionId(), question));
            return questionRankMap.get(rank);
        }

        public synchronized void clear() {
            this.questionRankMap = new ConcurrentHashMap<>();
            this.updateInProcess = false;
            this.maxRank = -1;
            LikedQuestionsRepositoryImpl.this.stateDiff.flushAll();
        }
        synchronized void updateType() {
            this.updateInProcess = false;
            this.questionRankMap = new ConcurrentHashMap<>();
            LikedQuestionsRepositoryImpl.this.stateDiff.flushAll();
        }

        synchronized Optional<Question> getQuestion(final int id) {
            return Optional.ofNullable(questionIDMap.get(id));
        }
        synchronized void putInCachedQuestions(Question question) {
            questionIDMap.put(question.getQuestionId(), question);
        }
    }
    public LikedQuestionsRepositoryImpl(DataRetriever dataRetriever,
                                          StateDiff stateDiff,
                                          final int userID) {
        this.dataRetriever = dataRetriever;
        this.stateDiff = stateDiff;
        this.state = new State();
        this.userID = userID;
    }


    @Override
    public Observable<Question> kthQuestion(int kth) {
        return this.state.putIfAbsent(kth, dataRetriever.kthLikedQuestion(kth, userID).cache());
    }

    @Override
    public Observable<Question> getQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        return question.map(Observable::just)
                .orElseGet(() -> dataRetriever.getQuestion(questionID, userID)
                        .doOnNext(this.state::putInCachedQuestions)
                        .cache())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())  ;

    }

    @Override
    public Observable<Boolean> likeQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        if(question.isPresent()) {
            Question question1 = question.get();
            question1.setLiked(true);
        }
        stateDiff.likeQuestion(questionID);
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> unlikeQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        if(question.isPresent()) {
            Question question1 = question.get();
            question1.setLiked(false);
        }
        stateDiff.undoLikeForQuestion(questionID);
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> bookmarkQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        if(question.isPresent()) {
            Question question1 = question.get();
            question1.setBookmarked(false);
        }
        stateDiff.bookmarkQuestion(questionID);
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> unbookmarkQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        if(question.isPresent()) {
            Question question1 = question.get();
            question1.setBookmarked(false);
        }
        stateDiff.undoBookmarkForQuestion(questionID);
        return Observable.just(true);
    }

    public int estimatedSize() {
        return this.state.maxRank;
    }

    @Override
    public void init(Action0 onCompleted) {
        this.state.updateType();
        ensureKMoreQuestions(10, onCompleted);
    }

    @Override
    public synchronized void ensureKMoreQuestions(int k, Action0 onCompleted) {
        if(this.state.updateInProcess) {
            onCompleted.call();
            return;
        }
        this.state.updateInProcess = true;
        int offset = this.state.maxRank + 1;
        dataRetriever.getLikedQuestions(offset, k, userID)
                .flatMap(Observable::from)
                .zipWith(Observable.range(offset, k), (question, id) -> new Pair<Integer, Question>(id, question))
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pair<Integer, Question>>() {
                    @Override
                    public void onCompleted() {
                        LikedQuestionsRepositoryImpl.this.state.updateInProcess = false;
                        onCompleted.call();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Pair<Integer, Question> rankQuestionPair) {
                        LikedQuestionsRepositoryImpl.this.state.putIfAbsent(rankQuestionPair.first, Observable.just(rankQuestionPair.second).cache());
                    }
                });
    }
}
