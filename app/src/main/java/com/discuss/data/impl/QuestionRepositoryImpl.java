package com.discuss.data.impl;

import android.util.Pair;

import com.discuss.data.DataRetriever;
import com.discuss.data.DataUpdater;
import com.discuss.data.QuestionRepository;
import com.discuss.data.SortBy;
import com.discuss.data.SortOrder;
import com.discuss.datatypes.Question;
import com.discuss.state.StateDiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

/**
 * @author Deepak Thakur
 */
public class QuestionRepositoryImpl implements QuestionRepository {

    private final List<Question> questionList;
    private final Map<Integer, Integer> questionMap;
    private final DataRetriever dataRetriever;
    private final DataUpdater dataUpdater;
    private final StateDiff stateDiff;
    private final String userName;
    private final int userID;
    private volatile SortBy sortBy;
    private volatile SortOrder sortOrder;
    private final Semaphore semaphore;
    public QuestionRepositoryImpl(DataUpdater dataUpdater,
                                  DataRetriever dataRetriever,
                                  StateDiff stateDiff,
                                  final String userName,
                                  final int userID) {
        this.dataUpdater = dataUpdater;
        this.dataRetriever = dataRetriever;
        this.stateDiff = stateDiff;
        this.questionList = new ArrayList<>();
        this.questionMap = new ConcurrentHashMap<>();
        this.userName = userName;
        this.userID = userID;

        sortBy = SortBy.LIKES;
        sortOrder = SortOrder.DESC;

        semaphore = new Semaphore(1); /* @todo(deepak): think of better ways */
    }

    @Override
    public void getQuestionWithRank(int kth, SortBy sortBy, SortOrder sortOrder, Consumer<Question> onData) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            onData.accept(null);
            //Thread.currentThread().interrupt();
            return;
        }

        if (this.sortOrder == sortOrder && this.sortBy == sortBy) {
            if (kth < questionList.size()) {
                onData.accept(questionList.get(kth));
                semaphore.release();
            } else {
                dataRetriever.getQuestions(questionList.size(), kth - questionList.size() + 1, userID, sortBy.name(), sortOrder.name()).flatMap(Observable::from).subscribe(new Subscriber<Question>() {
                    @Override
                    public void onCompleted() {
                        onData.accept(questionList.get(kth));
                        semaphore.release();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onData.accept(null); /* @todo(deepak): fix this */
                        semaphore.release();
                    }

                    @Override
                    public void onNext(Question question) {
                        questionList.add(question);
                        questionMap.put(question.getQuestionId(), questionList.size()-1);
                    }
                });
            }
        } else {
            refreshAndLoad(kth, sortBy, sortOrder).subscribe(new Subscriber<Question>() {
                @Override
                public void onCompleted() {
                    onData.accept(questionList.get(kth));
                    semaphore.release();
                }

                @Override
                public void onError(Throwable e) {
                    onData.accept(null); /* @todo(deepak): fix this */
                    semaphore.release();
                }

                @Override
                public void onNext(Question question) {
                    questionList.add(question);
                    questionMap.put(question.getQuestionId(), questionList.size()-1);
                }
            });
        }
    }

    private Observable<Question> refreshAndLoad(int kth, SortBy sortBy, SortOrder sortOrder) {
        questionList.clear();
        questionMap.clear();
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        return dataRetriever.getQuestions(0, kth + 1, userID, sortBy.name(), sortOrder.name()).flatMap(Observable::from);

    }

    @Override
    public void getQuestionWithID(int questionID, Consumer<Question> onData) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            onData.accept(null);
            //Thread.currentThread().interrupt();
            return;
        }

        if (questionMap.containsKey(questionID)) {
            onData.accept(questionList.get(questionMap.get(questionID)));
            semaphore.release();
        } else {
            dataRetriever.getQuestion(questionID, userID).subscribe(new Subscriber<Question>() {
                @Override
                public void onCompleted() {
                    semaphore.release();
                }

                @Override
                public void onError(Throwable e) {
                    onData.accept(null);
                    semaphore.release();
                }

                @Override
                public void onNext(Question question) {
                    onData.accept(question);
                }
            });
        }
    }

    @Override
    public void likeQuestionWithID(int questionID, Action0 onCompleted) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            onCompleted.call(); /* @todo(deepak): fix this */
            //Thread.currentThread().interrupt();
            return;
        }
        if (questionMap.containsKey(questionID)) {
            questionList.get(questionMap.get(questionID)).setLiked(true);
            stateDiff.likeQuestion(questionID);
            onCompleted.call();
            semaphore.release();
        } else {
            dataUpdater.likeQuestion(questionID, userID).subscribe(new Subscriber<Pair<Integer, Boolean>>() {
                @Override
                public void onCompleted() {
                    onCompleted.call();
                    semaphore.release();
                }

                @Override
                public void onError(Throwable e) {
                    semaphore.release();
                }

                @Override
                public void onNext(Pair<Integer, Boolean> integerBooleanPair) {
                }
            });
        }
    }

    @Override
    public void unlikeQuestionWithID(int questionID, Action0 onCompleted) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            onCompleted.call(); /* @todo(deepak): fix this */
            //Thread.currentThread().interrupt();
            return;
        }
        if (questionMap.containsKey(questionID)) {
            questionList.get(questionMap.get(questionID)).setLiked(false);
            stateDiff.undoLikeForQuestion(questionID);
            onCompleted.call();
            semaphore.release();
        } else {
            dataUpdater.unlikeQuestion(questionID, userID).subscribe(new Subscriber<Pair<Integer, Boolean>>() {
                @Override
                public void onCompleted() {
                    onCompleted.call();
                    semaphore.release();
                }

                @Override
                public void onError(Throwable e) {
                    semaphore.release();
                }

                @Override
                public void onNext(Pair<Integer, Boolean> integerBooleanPair) {
                }
            });
        }
    }

    @Override
    public void bookmarkQuestionWithID(int questionID, Action0 onCompleted) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            onCompleted.call(); /* @todo(deepak): fix this */
            //Thread.currentThread().interrupt();
            return;
        }
        if (questionMap.containsKey(questionID)) {
            questionList.get(questionMap.get(questionID)).setBookmarked(true);
            stateDiff.bookmarkQuestion(questionID);
            onCompleted.call();
            semaphore.release();
        } else {
            dataUpdater.bookmarkQuestion(questionID, userID).subscribe(new Subscriber<Pair<Integer, Boolean>>() {
                @Override
                public void onCompleted() {
                    onCompleted.call();
                    semaphore.release();
                }

                @Override
                public void onError(Throwable e) {
                    semaphore.release();
                }

                @Override
                public void onNext(Pair<Integer, Boolean> integerBooleanPair) {
                }
            });
        }
    }

    @Override
    public void unbookmarkQuestionWithID(int questionID, Action0 onCompleted) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            onCompleted.call(); /* @todo(deepak): fix this */
            //Thread.currentThread().interrupt();
            return;
        }
        if (questionMap.containsKey(questionID)) {
            questionList.get(questionMap.get(questionID)).setBookmarked(false);
            stateDiff.undoBookmarkForQuestion(questionID);
            onCompleted.call();
            semaphore.release();
        } else {
            dataUpdater.unbookmarkQuestion(questionID, userID).subscribe(new Subscriber<Pair<Integer, Boolean>>() {
                @Override
                public void onCompleted() {
                    onCompleted.call();
                    semaphore.release();
                }

                @Override
                public void onError(Throwable e) {
                    semaphore.release();
                }

                @Override
                public void onNext(Pair<Integer, Boolean> integerBooleanPair) {
                }
            });
        }
    }
}
