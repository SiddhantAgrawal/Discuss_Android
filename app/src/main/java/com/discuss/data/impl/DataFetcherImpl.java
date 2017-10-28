package com.discuss.data.impl;


import com.discuss.data.DiscussService;
import com.discuss.datatypes.Category;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.datatypes.Response;
import com.discuss.data.DataFetcher;
import com.discuss.datatypes.UserCategoryPreference;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * @author Deepak Thakur
 */

public class DataFetcherImpl implements DataFetcher {
    private final DiscussService discussService;

    @Inject
    public DataFetcherImpl(final String endpoint) {
        discussService = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(DiscussService.class);
    }

    @Override
    public Observable<List<Question>> getQuestions(int offset, int limit, String userId) {
        return discussService.getQuestions("questions/list?offset=" + offset + "&limit=" + limit + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<List<Comment>> getCommentsForQuestion(int questionId, int offset, int limit, String userId) {
        return discussService.getCommentsForQuestion("question/comments?questionId=" + questionId + "&offset=" + offset + "&limit=" + limit + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<List<Question>> getBookMarkedQuestions(int offset, int limit, String userId) {
        return discussService.getBookMarkedQuestions("user/bookmarked/questions?offset=" + offset + "&limit=" + limit + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<List<Question>> getLikedQuestions(int offset, int limit, String userId) {
        return discussService.getLikedQuestions("questions/liked?offset=" + offset + "&limit=" + limit).map(Response::getData);
    }

    @Override
    public Observable<List<Question>> getCommentedQuestions(int offset, int limit, String userId) {
        return discussService.getCommentedQuestions("questions/liked?offset=" + offset + "&limit=" + limit).map(Response::getData);
    }

    @Override
    public Observable<List<Comment>> getUserAddedComments(int offset, int limit, String userId) {
        return discussService.getUserAddedComments("user/comments?offset=" + offset + "&limit=" + limit + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<Question> getQuestion(String questionId, String userId) {
        return discussService.getQuestion("question/info?questionId=" + questionId + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<List<Category>> getCategory() {
        return discussService.getCategory("category/list").map(Response::getData);
    }

    @Override
    public Observable<List<UserCategoryPreference>> getUserCategoryPreference(String userId) {
        return discussService.getUserCategoryPreference("category/pref?userId=" + userId).map(Response::getData);
    }

    @Override
    public boolean likeQuestion(String questionId, String userId) {
        return discussService.likeQuestion("question/upvote?questionId=" + questionId + "&userId=" + userId);
    }

    @Override
    public boolean likeComment(String questionId, String userId) {
        return discussService.likeComment("comment/upvote?questionId=" + questionId + "&userId=" + userId);
    }

    @Override
    public boolean bookmarkQuestion(String questionId, String userId) {
        return discussService.bookmarkQuestion("bookmark/question?questionId=" + questionId + "&userId=" + userId);
    }

}
