package com.discuss.ui.feed.impl;

import android.util.Log;

import com.discuss.data.DataRetriever;
import com.discuss.data.QuestionRepository;
import com.discuss.data.SortBy;
import com.discuss.data.SortOrder;
import com.discuss.datatypes.Question;
import com.discuss.ui.QuestionSummary;
import com.discuss.ui.feed.MainFeedPresenter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */
public class MainFeedPresenterImpl implements MainFeedPresenter {
    private final QuestionRepository questionRepository;
    private final SortBy sortBy;
    private final SortOrder sortOrder;

    @Inject
    public MainFeedPresenterImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
        this.sortBy = SortBy.LIKES;
        this.sortOrder = SortOrder.DESC;
    }

    @Override
    public void init(Action0 onCompletedAction) {
        questionRepository.init(onCompletedAction, this.sortBy, this.sortOrder);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        questionRepository.ensureKMoreQuestions(onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Observable<QuestionSummary> get(int kth) {
        return questionRepository.kthQuestion(kth, sortBy, sortOrder).map(new Func1<Question, QuestionSummary>() {
            @Override
            public QuestionSummary call(Question question) {
                return new QuestionSummary.QuestionSummaryBuilder()
                        .setQuestionId(question.getQuestionId())
                        .setDifficulty(question.getDifficulty())
                        .setImageUrl(question.getImageUrl())
                        .setText(question.getText())
                        .setLikes(question.getLikes())
                        .setViews(question.getViews())
                        .setLiked(question.isLiked())
                        .setBookmarked(question.isBookmarked())
                        .setUserId(question.getUserId())
                        .setUserName(question.getUserName())
                        .build();
            }
        });
    }

    @Override
    public int size() {
        return questionRepository.estimatedSize();
    }

    @Override
    public Observable<Boolean> likeQuestionWithID(int questionID) {
        return questionRepository.likeQuestionWithID(questionID);
    }

    @Override
    public Observable<Boolean> unlikeQuestionWithID(int questionID) {
        return questionRepository.unlikeQuestionWithID(questionID);
    }

    @Override
    public Observable<Boolean> bookmarkQuestionWithID(int questionID) {
        return questionRepository.bookmarkQuestionWithID(questionID);
    }

    @Override
    public Observable<Boolean> unbookmarkQuestionWithID(int questionID) {
        return questionRepository.unbookmarkQuestionWithID(questionID);
    }

}
