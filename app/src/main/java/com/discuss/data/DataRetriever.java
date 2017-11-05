package com.discuss.data;


import com.discuss.datatypes.Category;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.datatypes.Response;
import com.discuss.datatypes.UserCategoryPreference;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

import java.util.List;

/**
 * @author Deepak Thakur
 *
 */

public interface DataRetriever {
    Observable<List<Question>> getQuestions(final int offset, final int limit, final int userId, final String sortBy, final String sortOrder);

    Observable<Question> kthQuestion(final int kth, final int userId, final String sortBy, final String sortOrder);

    Observable<List<Comment>> getCommentsForQuestion(final int questionId, final int offset, final int limit, final int userId, final String sortBy, final String sortOrder);

    Observable<Comment> kthCommentForQuestion(int kth, final int questionId, final int userId, final String sortBy, final String sortOrder);

    Observable<List<Question>> getBookMarkedQuestions(final int offset, final int limit, final int userId);

    Observable<Question> kthBookMarkedQuestion(int kth, final int userId);

    Observable<List<Question>> getLikedQuestions(final int offset, final int limit, final int userId);

    Observable<Question> kthLikedQuestion(final int kth, final int userId);

    Observable<List<Question>> getCommentedQuestions(final int offset, final int limit, final int userId);

    Observable<Question> kthCommentedQuestion(final int kth, final int userId);

    Observable<List<Comment>> getUserAddedComments(final int offset, final int limit, final String userId);

    Observable<Question> getQuestion(final int questionId, final int userId);

    Observable<List<Category>> getCategory();

    Observable<List<UserCategoryPreference>> getUserCategoryPreference(final String userId);

}
