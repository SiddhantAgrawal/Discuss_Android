package com.discuss.ui;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * @author Deepak Thakur
 */
public class CommentLikeState extends LikeState {
    private final CommentPresenter commentPresenter;
    public CommentLikeState(int id,
                            int initialLikes,
                            boolean isInitiallyLiked,
                            ImageView likeButtonImageView,
                            TextView likeTextView,
                            Drawable likeButtonDrawable,
                            Drawable likedButtonDrawable,
                            CommentPresenter commentPresenter) {
        super(id, initialLikes, isInitiallyLiked, likeButtonImageView, likeTextView, likeButtonDrawable, likedButtonDrawable);
        this.commentPresenter = commentPresenter;
    }

    @Override
    public void doOnLike() {
        commentPresenter.likeCommentWithID(super.id);
    }

    @Override
    public void doOnUnLike() {
        commentPresenter.unlikeCommentWithID(super.id);
    }
}
