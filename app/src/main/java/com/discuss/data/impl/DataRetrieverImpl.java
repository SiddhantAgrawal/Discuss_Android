package com.discuss.data.impl;


import com.discuss.data.DiscussService;
import com.discuss.datatypes.Category;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.PersonCategoryPreference;
import com.discuss.datatypes.Question;
import com.discuss.datatypes.Response;
import com.discuss.data.DataRetriever;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * @author Deepak Thakur
 */

public class DataRetrieverImpl implements DataRetriever {
    private final DiscussService discussService;

    @Inject
    public DataRetrieverImpl(final DiscussService discussService) {
        this.discussService = discussService;
    }

    @Override
    public Observable<List<Question>> getQuestions(int offset, int limit, int userId, final String sortBy, final String sortOrder) {
        return discussService.getQuestions("questions/list?offset=" + offset + "&limit=" + limit + "&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<Question> kthQuestion(int kth, int userId, String sortBy, String sortOrder) {
        return getQuestions(kth, 1, userId, sortBy, sortOrder).map(l -> l.get(0));
    }

    @Override
    public Observable<List<Comment>> getCommentsForQuestion(int questionId, int offset, int limit, int userId, final String sortBy, final String sortOrder) {
        return discussService.getCommentsForQuestion("question/comments?questionId=" + questionId + "&offset=" + offset + "&limit=" + limit + "&userId=" + userId + "&sortOrder=" + sortOrder).map(Response::getData);
    }

    @Override
    public Observable<Comment> kthCommentForQuestion(int kth, int questionId, int userId, String sortBy, String sortOrder) {
        return getCommentsForQuestion(questionId, kth, 1, userId, sortBy, sortOrder).map(l -> l.get(0));
    }

    @Override
    public Observable<List<Question>> getBookMarkedQuestions(int offset, int limit, int userId) {
        return discussService.getBookMarkedQuestions("user/bookmarked/questions?offset=" + offset + "&limit=" + limit + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<Question> kthBookMarkedQuestion(int kth, int userId) {
        return getBookMarkedQuestions(kth, 1, userId).map(l -> l.get(0));
    }

    @Override
    public Observable<List<Question>> getLikedQuestions(int offset, int limit, int userId) {
        return discussService.getLikedQuestions("questions/liked?offset=" + offset + "&limit=" + limit).map(Response::getData);
    }

    @Override
    public Observable<Question> kthLikedQuestion(int kth, int userId) {
        return getLikedQuestions(kth, 1, userId).map(l -> l.get(0));
    }

    @Override
    public Observable<List<Question>> getCommentedQuestions(int offset, int limit, int userId) {
        return discussService.getCommentedQuestions("questions/commented?offset=" + offset + "&limit=" + limit).map(Response::getData);
    }

    @Override
    public Observable<Question> kthCommentedQuestion(int kth, int userId) {
        return getCommentedQuestions(kth, 1, userId).map(l -> l.get(0));
    }

    @Override
    public Observable<Comment> getUserAddedComment(int questionID, int userId) {
        return discussService.getUserAddedComments("user/comment?questionID=" + questionID + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<Question> getQuestion(int questionId, int userId) {
        return discussService.getQuestion("question/info?questionId=" + questionId + "&userId=" + userId).map(Response::getData);
    }

    @Override
    public Observable<List<Category>> getCategory() {
        return discussService.getCategory("category/list").map(Response::getData);
    }

    @Override
    public Observable<List<PersonCategoryPreference>> getUserCategoryPreference(String userId) {
        return discussService.getPersonCategoryPreference("category/pref?userId=" + userId).map(Response::getData);
    }

}
