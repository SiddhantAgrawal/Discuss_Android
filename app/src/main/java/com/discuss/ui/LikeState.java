package com.discuss.ui;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.discuss.ui.feed.MainFeedPresenter;

/**
 *
 * @author Deepak Thakur
 */
public abstract class LikeState {
    protected final int id;
    private final int initialLikes;
    private final boolean isInitiallyLiked;
    private final ImageView likeButtonImageView;
    private final TextView likeTextView;
    private final Drawable likeButtonDrawable;
    private final Drawable likedButtonDrawable;
    private volatile boolean currentlyLiked;
    public LikeState(int id,
                     int initialLikes,
                     boolean isInitiallyLiked,
                     ImageView likeButtonImageView,
                     TextView likeTextView,
                     Drawable likeButtonDrawable,
                     Drawable likedButtonDrawable) {
        this.id = id;
        this.initialLikes = initialLikes;
        this.isInitiallyLiked = isInitiallyLiked;
        this.likeButtonImageView = likeButtonImageView;
        this.likeTextView = likeTextView;
        this.likeButtonDrawable = likeButtonDrawable;
        this.likedButtonDrawable = likedButtonDrawable;
        this.currentlyLiked = isInitiallyLiked;
        this.likeButtonImageView.setImageDrawable(isInitiallyLiked ? likedButtonDrawable : likeButtonDrawable);
    }

    @SuppressLint("SetTextI18n")
    public void pressUpdate() {
        if (currentlyLiked) {
            likeButtonImageView.setImageDrawable(likeButtonDrawable);
            if (isInitiallyLiked) {
                doOnUnLike();
                likeTextView.setText(Integer.toString(initialLikes - 1));
            } else {
                likeTextView.setText(Integer.toString(initialLikes ));
            }
        } else {
            likeButtonImageView.setImageDrawable(likedButtonDrawable);
            if (isInitiallyLiked) {
                likeTextView.setText(Integer.toString(initialLikes ));
            } else {
                doOnLike();
                likeTextView.setText(Integer.toString(initialLikes + 1));
            }
        }
        currentlyLiked = !currentlyLiked;
    }
    public abstract void doOnLike();
    public abstract void doOnUnLike();

}
