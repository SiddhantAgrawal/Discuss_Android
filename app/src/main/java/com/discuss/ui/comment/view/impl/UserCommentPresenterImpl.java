package com.discuss.ui.comment.view.impl;

import android.util.Log;

import com.discuss.data.CommentRepository;
import com.discuss.datatypes.Comment;
import com.discuss.ui.CommentSummary;
import com.discuss.ui.comment.view.UserCommentPresenter;
import com.discuss.utils.Utils;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *
 * @author Deepak Thakur
 */
public class UserCommentPresenterImpl implements UserCommentPresenter {
    private int questionID;
    private CommentRepository commentRepository;
    private Single<Comment> comment;
    private volatile String editedComment;
    private volatile String initialComment;

    @Inject
    public UserCommentPresenterImpl(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    @Override
    public Single<CommentSummary> init(int questionID) {
        this.questionID = questionID;
        this.comment = commentRepository.userAddedComment(questionID);
        Single<CommentSummary> commentSummaryObservable = comment.map(commentCommentSummaryFunc1);

        commentSummaryObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commentSummary -> {
                    if (commentSummary != null) {
                        editedComment = initialComment = commentSummary.getText();
                    }
            });
        return commentSummaryObservable;
    }

    public void setEditedComment(final String editedComment) {
        Log.e("UCPI","set edit msg " + editedComment);
        this.editedComment = editedComment;
    }

    public String getEditedComment() {
        Log.e("UCPI","get edit msg " + editedComment);
        return editedComment;
    }

    @Override
    public void save() {
        int commentID = comment.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toBlocking()
                .value()
                .getCommentId();
        if(!Utils.isEqual(initialComment, editedComment)) {
            commentRepository.updateCommentText(commentID, editedComment);
        }
        commentRepository.save();
        this.comment = commentRepository.userAddedComment(questionID);
        Log.e("UserCommentPreImpl", "after save");
    }


    private Func1<Comment, CommentSummary> commentCommentSummaryFunc1 = userComment -> {
        if(null == userComment) {
            return null;
        }
        return CommentSummary.builder()
                .commentId(userComment.getCommentId())
                .imageUrl(userComment.getImageUrl())
                .liked(userComment.isLiked())
                .text(userComment.getText())
                .personId(userComment.getPersonId())
                .personName(userComment.getPersonName())
                .views(userComment.getViews())
                .likes(userComment.getLikes())
                .build();
    };
}
