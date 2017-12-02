package com.discuss.ui.bookmark.impl;

import com.discuss.data.BookMarkRepository;
import com.discuss.data.DataRetriever;
import com.discuss.datatypes.Question;
import com.discuss.ui.QuestionSummary;
import com.discuss.ui.bookmark.BookMarkPresenter;

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
public class BookMarkPresenterImpl implements BookMarkPresenter {
    private final BookMarkRepository bookMarkRepository;

    @Inject
    public BookMarkPresenterImpl(BookMarkRepository bookMarkRepository) {
        this.bookMarkRepository = bookMarkRepository;
    }

    @Override
    public void init(Action0 onCompletedAction) {
        bookMarkRepository.init(onCompletedAction);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        bookMarkRepository.ensureKMoreQuestions(onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Single<QuestionSummary> get(int kth) {
        return bookMarkRepository.kthQuestion(kth).map(new Func1<Question, QuestionSummary>() {
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
        return bookMarkRepository.estimatedSize();
    }

    @Override
    public Single<Boolean> likeQuestionWithID(int questionID) {
        return bookMarkRepository.likeQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> unlikeQuestionWithID(int questionID) {
        return bookMarkRepository.unlikeQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> bookmarkQuestionWithID(int questionID) {
        return bookMarkRepository.bookmarkQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> unbookmarkQuestionWithID(int questionID) {
        return bookMarkRepository.unbookmarkQuestionWithID(questionID);
    }

}
