package com.discuss.data.impl;

import com.discuss.data.DataRetriever;
import com.discuss.data.QuestionsAnsweredRepository;
import com.discuss.data.StateDiff;
import com.discuss.datatypes.Question;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 *
 * @author Deepak Thakur
 */
public class QuestionsAnsweredRepositoryImpl implements QuestionsAnsweredRepository {
    private final DataRetriever dataRetriever;
    private final StateDiff stateDiff;
    private final int userID;
    private final QuestionsAnsweredRepositoryImpl.State state;

    private final class State {
        private volatile int slab;
        private Map<Integer, Observable<List<Question>>> questionRankMap;
        private Map<Integer, Question> questionIDMap;

        State() {
            this.questionRankMap = new ConcurrentHashMap<>();
            this.questionIDMap = new ConcurrentHashMap<>();
            this.slab = 0;
        }

        Observable<List<Question>> getQuestions(int slabId) {
            return Observable.create((Observable.OnSubscribe<List<Question>>) subscriber -> dataRetriever.getCommentedQuestions(slabId*10, 10, userID).
                    subscribe(subscriber)).doOnNext(list -> list.forEach(question -> questionIDMap.put(question.getQuestionId(), question))).
                    doOnNext(list -> slab = Math.max(slab, slabId + 1)).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    cache();
        }

        synchronized Observable<Question> kthQuestion(final int rank) {
            final int mapIndex = rank/10;
            final int localIndex = rank%10;
            final Observable<List<Question>> questionObservable = getQuestions(mapIndex);
            questionRankMap.putIfAbsent(mapIndex, questionObservable);
            return questionRankMap.get(mapIndex).map(list -> list.get(localIndex));
        }

        synchronized void ensureMoreQuestions(Action0 onCompleted) {
            int currentSlabId = this.slab;
            final Observable<List<Question>> questionObservable = getQuestions(currentSlabId);
            questionRankMap.putIfAbsent(currentSlabId, questionObservable);
            questionRankMap.get(currentSlabId).subscribe(a -> {}, e -> {}, onCompleted);
        }

        public synchronized void clear() {
            this.questionRankMap = new ConcurrentHashMap<>();
            this.slab = 0;
            //stateDiff.flushAll();
        }

        synchronized void updateType() {
            this.slab = 0;
            this.questionRankMap = new ConcurrentHashMap<>();
            //stateDiff.flushAll();
        }

        synchronized Optional<Question> getQuestion(final int id) {
            return Optional.ofNullable(questionIDMap.get(id));
        }

        synchronized void putInCachedQuestions(Question question) {
            questionIDMap.put(question.getQuestionId(), question);
        }
    }

    public QuestionsAnsweredRepositoryImpl(DataRetriever dataRetriever,
                                        StateDiff stateDiff,
                                        final int userID) {
        this.dataRetriever = dataRetriever;
        this.stateDiff = stateDiff;
        this.state = new QuestionsAnsweredRepositoryImpl.State();
        this.userID = userID;
    }


    @Override
    public synchronized Observable<Question> kthQuestion(final int kth) {
        return this.state.kthQuestion(kth);
    }

    @Override
    public Observable<Question> getQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        return question.map(Observable::just)
                .orElseGet(() -> dataRetriever.kthCommentedQuestion(questionID, userID)
                        .doOnNext(this.state::putInCachedQuestions)
                        .cache());

    }

    @Override
    public Observable<Boolean> likeQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        if (question.isPresent()) {
            Question question1 = question.get();
            question1.setLiked(true);
        }
        stateDiff.likeQuestion(questionID);
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> unlikeQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        if (question.isPresent()) {
            Question question1 = question.get();
            question1.setLiked(false);
        }
        stateDiff.undoLikeForQuestion(questionID);
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> bookmarkQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        if (question.isPresent()) {
            Question question1 = question.get();
            question1.setBookmarked(false);
        }
        stateDiff.bookmarkQuestion(questionID);
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> unbookmarkQuestionWithID(int questionID) {
        Optional<Question> question = this.state.getQuestion(questionID);
        if (question.isPresent()) {
            Question question1 = question.get();
            question1.setBookmarked(false);
        }
        stateDiff.undoBookmarkForQuestion(questionID);
        return Observable.just(true);
    }

    public int estimatedSize() {
        return this.state.slab * 10;
    }

    @Override
    public void init(Action0 onCompleted) {
        this.state.updateType();
        ensureKMoreQuestions(onCompleted);
    }

    @Override
    public synchronized void ensureKMoreQuestions(Action0 onCompleted) {
        this.state.ensureMoreQuestions(onCompleted);
    }
}
