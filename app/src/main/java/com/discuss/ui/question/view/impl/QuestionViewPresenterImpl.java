package com.discuss.ui.question.view.impl;


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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class QuestionViewPresenterImpl implements QuestionViewPresenter<Comment>{

    private Question question = null;
    private final CommentRepository commentRepository;
    private List<Comment> comments;
    private Observable<List<Comment>> commentsObservable;
    private final ReentrantLock lock = new ReentrantLock();
    private final SortBy sortBy;
    private final SortOrder sortOrder;

    @Inject
    public QuestionViewPresenterImpl(CommentRepository commentRepository, Question question) {
        this.commentRepository = commentRepository;
        this.question = question;
        this.sortBy = SortBy.LIKES;
        this.sortOrder = SortOrder.DESC;
    }

    @Override
    public void init(Action0 onCompletedAction, Question question) {
        commentRepository.init(onCompletedAction, sortBy, sortOrder, question.getQuestionId());
    }

    @Override
    public void update(Action0 onCompletedAction) {
        commentRepository.ensureKMoreComments(10, onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {}, question);
        return Observable.just(true);
    }

    @Override
    public Observable<Comment> getComment(int kth) {
        return commentRepository.kthCommentForQuestion(kth, question.getQuestionId(), sortBy, sortOrder);
    }

    @Override
    public int size() {
        return commentRepository.estimatedSize();
    }

    @Override
    public Question getQuestion() {
        return question;
    }
}
