package com.discuss.ui.commented.impl;


import com.discuss.data.BookMarkRepository;
import com.discuss.data.DataRetriever;
import com.discuss.data.QuestionsAnsweredRepository;
import com.discuss.datatypes.Question;
import com.discuss.ui.QuestionSummary;
import com.discuss.ui.commented.CommentedPresenter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */
public class CommentedPresenterImpl implements CommentedPresenter {
    private final QuestionsAnsweredRepository answeredRepository;

    @Inject
    public CommentedPresenterImpl(QuestionsAnsweredRepository answeredRepository) {
        this.answeredRepository = answeredRepository;
    }

    @Override
    public void init(Action0 onCompletedAction) {
        answeredRepository.init(onCompletedAction);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        answeredRepository.ensureKMoreQuestions(onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Single<QuestionSummary> get(int kth) {
        return answeredRepository.kthQuestion(kth).map(new Func1<Question, QuestionSummary>() {
            @Override
            public QuestionSummary call(Question question) {
                return QuestionSummary.builder()
                        .questionId(question.getQuestionId())
                        .difficulty(question.getDifficulty())
                        .imageUrl(question.getImageUrl())
                        .text(question.getText())
                        .likes(question.getLikes())
                        .views(question.getViews())
                        .liked(question.isLiked())
                        .bookmarked(question.isBookmarked())
                        .personId(question.getPersonId())
                        .personName(question.getPersonName())
                        .build();
            }
        });
    }

    @Override
    public int size() {
        return answeredRepository.estimatedSize();
    }

    @Override
    public Single<Boolean> likeQuestionWithID(int questionID) {
        return answeredRepository.likeQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> unlikeQuestionWithID(int questionID) {
        return answeredRepository.unlikeQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> bookmarkQuestionWithID(int questionID) {
        return answeredRepository.bookmarkQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> unbookmarkQuestionWithID(int questionID) {
        return answeredRepository.unbookmarkQuestionWithID(questionID);
    }

}
