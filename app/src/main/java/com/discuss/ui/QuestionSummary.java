package com.discuss.ui;

import com.discuss.datatypes.Question;

/**
 *
 * @author Deepak Thakur
 */
public class QuestionSummary {
    private int questionId;
    private String text;
    private String imageUrl;
    private int likes;
    private int views;
    private int userId;
    private String userName;
    private String difficulty;
    private boolean liked;
    private boolean bookmarked;

    public int getQuestionId() {
        return this.questionId;
    }

    public String getText() {
        return this.text;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public int getLikes() {
        return this.likes;
    }

    public int getViews() {
        return this.views;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public boolean isLiked() {
        return this.liked;
    }

    public boolean isBookmarked() {
        return this.bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    private QuestionSummary(QuestionSummaryBuilder QuestionSummaryBuilder) {
        this.questionId = QuestionSummaryBuilder.questionId;
        this.text = QuestionSummaryBuilder.text;
        this.imageUrl = QuestionSummaryBuilder.imageUrl;
        this.likes = QuestionSummaryBuilder.likes;
        this.views = QuestionSummaryBuilder.views;
        this.userId = QuestionSummaryBuilder.userId;
        this.userName = QuestionSummaryBuilder.userName;
    }

    public static class QuestionSummaryBuilder {
        private int questionId;
        private String text;
        private String imageUrl;
        private int likes;
        private int views;
        private int userId;
        private String userName;
        private String difficulty;
        private boolean liked;
        private boolean bookmarked;

        public QuestionSummaryBuilder() {
        }

        public QuestionSummaryBuilder setQuestionId(int questionId) {
            this.questionId = questionId;
            return this;
        }

        public QuestionSummaryBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public QuestionSummaryBuilder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public QuestionSummaryBuilder setLikes(int likes) {
            this.likes = likes;
            return this;
        }

        public QuestionSummaryBuilder setViews(int views) {
            this.views = views;
            return this;
        }

        public QuestionSummaryBuilder setUserId(int userId) {
            this.userId = userId;
            return this;
        }

        public QuestionSummaryBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public QuestionSummaryBuilder setDifficulty(String difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public QuestionSummaryBuilder setLiked(boolean liked) {
            this.liked = liked;
            return this;
        }

        public QuestionSummaryBuilder setBookmarked(boolean bookmarked) {
            this.bookmarked = bookmarked;
            return this;
        }

        public QuestionSummary build() {
            return new QuestionSummary(this);
        }
    }
}
