package com.discuss.fetcher;


import com.discuss.datatypes.Category;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.datatypes.Response;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

import java.util.List;

/**
 * @author Deepak Thakur
 *
 */

public interface DataFetcher {
    Observable<List<Question>> getQuestions(final int category, final int offset, final int limit, final String userId);

    Observable<List<Comment>> getCommentsForQuestion(final int questionId, final int offset, final int limit, final String userId);

    Observable<List<Question>> getBookMarkedQuestions(final int offset, final int limit, final String userId);

    Observable<List<Question>> getLikedQuestions(final int offset, final int limit, final String userId);

    Observable<List<Comment>> getUserAddedComments(final int offset, final int limit, final String userId);

    Observable<Question> getQuestion(final String questionId, final String userId);

    Observable<List<Category>> getCategory();

    boolean likeQuestion(final String questionId, final String userId);

    boolean likeComment( final String questionId, final String userId);

    boolean bookmarkQuestion( final String questionId, final String userId);

}
