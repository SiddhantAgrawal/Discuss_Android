package com.discuss.ui.liked.impl;

import com.discuss.data.LikedQuestionsRepository;
import com.discuss.datatypes.Question;
import com.discuss.ui.QuestionSummary;
import com.discuss.ui.liked.LikedPresenter;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * @author Deepak Thakur
 */
public class LikedPresenterImpl implements LikedPresenter {
    private final LikedQuestionsRepository likedQuestionsRepository;

    @Inject
    public LikedPresenterImpl(LikedQuestionsRepository likedQuestionsRepository) {
        this.likedQuestionsRepository = likedQuestionsRepository;
    }

    @Override
    public void init(Action0 onCompletedAction) {
        likedQuestionsRepository.init(onCompletedAction);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        likedQuestionsRepository.ensureKMoreQuestions(onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Single<QuestionSummary> get(int kth) {
        return likedQuestionsRepository.kthQuestion(kth).map(new Func1<Question, QuestionSummary>() {
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
        return likedQuestionsRepository.estimatedSize();
    }

    @Override
    public void save() {
        this.likedQuestionsRepository.save();
    }


    @Override
    public Single<Boolean> likeQuestionWithID(int questionID) {
        return likedQuestionsRepository.likeQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> unlikeQuestionWithID(int questionID) {
        return likedQuestionsRepository.unlikeQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> bookmarkQuestionWithID(int questionID) {
        return likedQuestionsRepository.bookmarkQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> unbookmarkQuestionWithID(int questionID) {
        return likedQuestionsRepository.unbookmarkQuestionWithID(questionID);
    }

}
