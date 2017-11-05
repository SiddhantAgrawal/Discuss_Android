package com.discuss.data.impl;

import android.util.Log;
import android.util.Pair;

import com.discuss.data.DataRetriever;
import com.discuss.data.QuestionRepository;
import com.discuss.data.SortBy;
import com.discuss.data.SortOrder;
import com.discuss.datatypes.Question;
import com.discuss.data.StateDiff;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */
public class QuestionRepositoryImpl implements QuestionRepository {


    private final DataRetriever dataRetriever;
    private final StateDiff stateDiff;
    private final int userID;
    private final State state;
    private final class State {
        private volatile boolean updateInProcess;
        private volatile int maxRank;
        private Map<Integer, Observable<Question>> questionRankMap;
        private volatile SortBy sortBy;
        private volatile SortOrder sortOrder;
        private Map<Integer, Question> questionIDMap;
        State() {
            this.questionRankMap = new ConcurrentHashMap<>();
            this.questionIDMap = new ConcurrentHashMap<>();
            this.sortBy = SortBy.LIKES;
            this.sortOrder = SortOrder.DESC;
            this.updateInProcess = false;
            this.maxRank = -1;
        }
        synchronized Observable<Question> putIfAbsent(int rank, Observable<Question> questionObservable) {
            if(null == questionRankMap.putIfAbsent(rank, questionObservable)) {
                questionObservable.subscribe(question -> {
                    questionIDMap.put(question.getQuestionId(), question);
                });
            }
            maxRank = Math.max(rank, maxRank);
            return questionRankMap.get(rank);
        }

        public synchronized void clear() {
            this.sortBy = null;
            this.sortOrder = null;
            this.questionRankMap = new ConcurrentHashMap<>();
            this.updateInProcess = false;
            this.maxRank = -1;
            //stateDiff.flushAll();
        }
        synchronized void updateType(SortOrder sortOrder, SortBy sortBy) {
            this.maxRank = -1;
            this.sortBy = sortBy;
            this.sortOrder = sortOrder;
            this.updateInProcess = false;
            this.questionRankMap = new ConcurrentHashMap<>();
            //stateDiff.flushAll();
        }
        synchronized SortOrder getSortOrder() {
            return this.sortOrder;
        }
        synchronized SortBy getSortBy() {
            return this.sortBy;
        }

        synchronized Optional<Question> getQuestion(final int id) {
            return Optional.ofNullable(questionIDMap.get(id));
        }
        synchronized void putInCachedQuestions(Question question) {
            questionIDMap.put(question.getQuestionId(), question);
        }
    }
    public QuestionRepositoryImpl(DataRetriever dataRetriever,
                                  StateDiff stateDiff,
                                  final int userID) {
        this.dataRetriever = dataRetriever;
        this.stateDiff = stateDiff;
        this.state = new State();
        this.userID = userID;
    }


    @Override
    public Observable<Question> kthQuestion(int kth, SortBy sortBy, SortOrder sortOrder) {

        if (this.state.getSortOrder() == sortOrder && this.state.getSortBy() == sortBy) {
            return this.state.putIfAbsent(kth, dataRetriever.kthQuestion(kth, userID, sortBy.name(), sortOrder.name()).cache());
        } else {
           this.state.updateType(sortOrder, sortBy);
           ensureKMoreQuestions(10, () -> {});
           return dataRetriever.kthQuestion(kth, userID, sortBy.name(), sortOrder.name())
                    .cache()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

    @Override
    public Observable<Question> getQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        return question.map(Observable::just)
                .orElseGet(() -> dataRetriever.getQuestion(questionID, userID)
                .doOnNext(this.state::putInCachedQuestions)
                .cache());

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
    public void init(Action0 onCompleted, SortBy sortBy, SortOrder sortOrder) {
        this.state.updateType(sortOrder, sortBy);
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
        dataRetriever.getQuestions(offset, k, userID, this.state.sortBy.name(), this.state.sortOrder.name())
                .flatMap(Observable::from)
                .zipWith(Observable.range(offset, k), (question, id) -> new Pair<Integer, Question>(id, question))
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pair<Integer, Question>>() {
                    @Override
                    public void onCompleted() {
                        QuestionRepositoryImpl.this.state.updateInProcess = false;
                        onCompleted.call();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("QuestionRepo", e.toString());
                        onCompleted.call();
                    }

                    @Override
                    public void onNext(Pair<Integer, Question> rankQuestionPair) {
                        QuestionRepositoryImpl.this.state.putIfAbsent(rankQuestionPair.first, Observable.just(rankQuestionPair.second).cache());
                    }
                });

    }
}
