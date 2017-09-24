package com.discuss.fetcher.impl;


import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.datatypes.Response;
import com.discuss.fetcher.DataFetcher;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * @author Deepak Thakur
 *
 */

public class DataFetcherImpl implements DataFetcher {
    private static final String SERVICE_ENDPOINT = "http://192.168.0.5:8070/";

    private static final DiscussService discussService = new Retrofit.Builder()
                .baseUrl(DataFetcherImpl.SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(DiscussService.class);

    @Override
    public Observable<List<Question>> getQuestions(int category, int offset, int limit, String userId) {
        return discussService.getQuestions("questions/list?category=" + category + "&offset=" + offset + "&limit=" + limit + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<List<Comment>> getCommentsForQuestion(int questionId, int offset, int limit, String userId) {
        return discussService.getCommentsForQuestion("question/comments?questionId=" + questionId + "&offset=" + offset + "&limit=" + limit + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<List<Question>> getBookMarkedQuestions(int offset, int limit, String userId) {
        return discussService.getBookMarkedQuestions("user/bookmarked/questions?offset="+ offset + "&limit=" + limit + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<List<Comment>> getUserAddedComments(int offset, int limit, String userId) {
        return discussService.getUserAddedComments("user/comments?offset="+ offset + "&limit=" + limit + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<Question> getQuestion(String questionId, String userId) {
        return discussService.getQuestion("question/info?questionId=" + questionId + "&userId=" + userId).map(Response::getData);
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
