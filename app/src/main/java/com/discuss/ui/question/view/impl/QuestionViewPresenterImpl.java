package com.discuss.ui.question.view.impl;


import android.util.Log;

import com.discuss.data.CommentRepository;
import com.discuss.data.DataRetriever;
import com.discuss.data.SortBy;
import com.discuss.data.SortOrder;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.ui.question.view.QuestionViewPresenter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class QuestionViewPresenterImpl implements QuestionViewPresenter<Comment>{

    private int questionID = 0;
    private final CommentRepository commentRepository;
    private final SortBy sortBy;
    private final SortOrder sortOrder;

    @Inject
    public QuestionViewPresenterImpl(CommentRepository commentRepository) {
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
    public void update(Action0 onCompletedAction) {
        commentRepository.ensureKMoreComments(onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {}, questionID);
        return Observable.just(true);
    }

    @Override
    public Observable<Comment> getComment(int kth) {
        return commentRepository.kthCommentForQuestion(kth, questionID, sortBy, sortOrder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public int size() {
        return commentRepository.estimatedSize();
    }

    @Override
    public Observable<Question> getQuestion() {
        return commentRepository.getQuestionWithID(questionID);
    }
}
