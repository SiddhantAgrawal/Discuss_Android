package com.discuss.data;


import com.discuss.datatypes.Category;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.PersonCategoryPreference;
import com.discuss.datatypes.Question;

import rx.Observable;
import rx.Single;

import java.util.List;

/**
 * @author Deepak Thakur
 *
 */

public interface DataRetriever {
    Single<List<Question>> getQuestions(final int offset, final int limit, final int userId, final String sortBy, final String sortOrder);

    Single<Question> kthQuestion(final int kth, final int userId, final String sortBy, final String sortOrder);

    Single<List<Comment>> getCommentsForQuestion(final int questionId, final int offset, final int limit, final int userId, final String sortBy, final String sortOrder);

    Single<Comment> kthCommentForQuestion(int kth, final int questionId, final int userId, final String sortBy, final String sortOrder);

    Single<List<Question>> getBookMarkedQuestions(final int offset, final int limit, final int userId);

    Single<Question> kthBookMarkedQuestion(int kth, final int userId);

    Single<List<Question>> getLikedQuestions(final int offset, final int limit, final int userId);

    Single<Question> kthLikedQuestion(final int kth, final int userId);

    Single<List<Question>> getCommentedQuestions(final int offset, final int limit, final int userId);

    Single<Question> kthCommentedQuestion(final int kth, final int userId);

    Single<Comment> getUserAddedComment(final int questionID, final int userId);

    Single<Question> getQuestion(final int questionId, final int userId);

    Single<List<Category>> getCategory();

    Single<List<PersonCategoryPreference>> getUserCategoryPreference(final String userId);

}
