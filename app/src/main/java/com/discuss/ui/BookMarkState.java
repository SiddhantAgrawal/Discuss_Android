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
public class BookMarkState {
    private final int questionID;
    private final boolean isInitiallyBookmarked;
    private final ImageView bookmarkImageView;
    private final Drawable bookmarkDrawable;
    private final Drawable bookmarkedDrawable;
    private volatile boolean currentlyBookmarked;
    private final QuestionPresenter questionPresenter;
    public BookMarkState(int questionID,
                     boolean isInitiallyBookmarked,
                     ImageView bookmarkImageView,
                     Drawable bookmarkDrawable,
                     Drawable bookmarkedDrawable,
                     QuestionPresenter questionPresenter) {
        this.questionID = questionID;
        this.isInitiallyBookmarked = isInitiallyBookmarked;
        this.bookmarkImageView = bookmarkImageView;
        this.bookmarkDrawable = bookmarkDrawable;
        this.bookmarkedDrawable = bookmarkedDrawable;
        this.questionPresenter = questionPresenter;
        this.currentlyBookmarked = isInitiallyBookmarked;
    }

    @SuppressLint("SetTextI18n")
    public void pressUpdate() {
        if (currentlyBookmarked) {
            bookmarkImageView.setImageDrawable(bookmarkDrawable);
            if (isInitiallyBookmarked) {
                questionPresenter.unlikeQuestionWithID(questionID);
            }
        } else {
            bookmarkImageView.setImageDrawable(bookmarkedDrawable);
            if (!isInitiallyBookmarked) {
                questionPresenter.likeQuestionWithID(questionID);
            }
        }
        currentlyBookmarked = !currentlyBookmarked;
    }
}
