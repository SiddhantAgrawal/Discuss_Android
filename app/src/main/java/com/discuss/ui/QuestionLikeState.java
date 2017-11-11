package com.discuss.ui;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.discuss.ui.feed.MainFeedPresenter;

/**
 *
 * @author Deepak Thakur
 */
public class QuestionLikeState extends LikeState {
    private final QuestionPresenter questionPresenter;
    public QuestionLikeState(int questionID,
                             int initialLikes,
                             boolean isInitiallyLiked,
                             ImageView likeButtonImageView,
                             TextView likeTextView,
                             Drawable likeButtonDrawable,
                             Drawable likedButtonDrawable,
                             QuestionPresenter questionPresenter) {
        super(questionID, initialLikes, isInitiallyLiked, likeButtonImageView, likeTextView, likeButtonDrawable, likedButtonDrawable);
        this.questionPresenter = questionPresenter;
    }

    @Override
    public void doOnLike() {
        questionPresenter.likeQuestionWithID(super.id);
    }

    @Override
    public void doOnUnLike() {
        questionPresenter.unlikeQuestionWithID(super.id);
    }
}
