package com.discuss.ui.question.view.impl;


import android.util.Log;

import com.discuss.data.CommentRepository;
import com.discuss.data.DataRetriever;
import com.discuss.data.QuestionRepository;
import com.discuss.data.SortBy;
import com.discuss.data.SortOrder;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.ui.CommentSummary;
import com.discuss.ui.QuestionSummary;
import com.discuss.ui.question.view.QuestionViewPresenter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class QuestionViewPresenterImpl implements QuestionViewPresenter {

    private int questionID = 0;
    private final QuestionRepository questionRepository;
    private final CommentRepository commentRepository;
    private final SortBy sortBy;
    private final SortOrder sortOrder;

    @Inject
    public QuestionViewPresenterImpl(QuestionRepository questionRepository, CommentRepository commentRepository) {
        this.questionRepository = questionRepository;
        this.commentRepository = commentRepository;
        this.sortBy = SortBy.LIKES;
        this.sortOrder = SortOrder.DESC;
    }

    @Override
    public void init(Action0 onCompletedAction, int questionID) {
        this.questionID = questionID;
        commentRepository.init(onCompletedAction, sortBy, sortOrder, questionID);
    }

    @Override
    public void update(Action0 onCompletedAction, Action0 onNoUpdate) {
        if(commentRepository.isFurtherLoadingPossible()) {
            commentRepository.ensureKMoreComments(onCompletedAction, onNoUpdate);
        } else {
            onNoUpdate.call();
        }
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {}, questionID);
        return Observable.just(true);
    }

    @Override
    public Single<CommentSummary> getComment(int kth) {
        return commentRepository.kthCommentForQuestion(kth, questionID, sortBy, sortOrder).map(new Func1<Comment, CommentSummary>() {
            @Override
            public CommentSummary call(Comment comment) {
                if (null == comment)
                    return null;
                return CommentSummary.builder()
                        .commentId(comment.getCommentId())
                        .imageUrl(comment.getImageUrl())
                        .liked(comment.isLiked())
                        .text(comment.getText())
                        .personId(comment.getPersonId())
                        .personName(comment.getPersonName())
                        .views(comment.getViews())
                        .likes(comment.getLikes())
                        .build();
            }
        });
    }

    @Override
    public int size() {
        return commentRepository.estimatedSize();
    }

    @Override
    public Single<QuestionSummary> getQuestion() {
        return commentRepository.getQuestionWithID(questionID).map(new Func1<Question, QuestionSummary>() {
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
    public void save() {
        this.commentRepository.save();
    }

    @Override
    public Single<Boolean> likeCommentWithID(int commentID) {
        return commentRepository.likeCommentWithID(commentID);
    }

    @Override
    public Single<Boolean> unlikeCommentWithID(int commentID) {
        return commentRepository.unlikeCommentWithID(commentID);
    }

    @Override
    public Single<Boolean> likeQuestionWithID(int questionID) {
        return questionRepository.likeQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> unlikeQuestionWithID(int questionID) {
        return questionRepository.unlikeQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> bookmarkQuestionWithID(int questionID) {
        return questionRepository.bookmarkQuestionWithID(questionID);
    }

    @Override
    public Single<Boolean> unbookmarkQuestionWithID(int questionID) {
        return questionRepository.unbookmarkQuestionWithID(questionID);
    }
}
