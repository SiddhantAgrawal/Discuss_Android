package com.example.siddhantagrawal.check_discuss;


import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import rx.Observable;

import java.util.List;

/**
 * @author Deepak Thakur
 *
 */

public interface DataFetcher<T> {
    Observable<T> questions();
    List<Question> getQuestions(final int category, final int offset, final int limit, final String userId);

    List<Comment> getCommentsForQuestion(final String questionId, final int offset, final int limit, final String userId);

    List<Question> getBookMarkedQuestions(final int offset, final int limit, final String userId);

    List<Comment> getUserAddedComments(final int offset, final int limit, final String userId);

    Question getQuestion(final String questionId, final String userId);

    boolean likeQuestion(final String questionId, final String userId);

    boolean likeComment( final String questionId, final String userId);

    boolean bookmarkQuestion( final String questionId, final String userId);

}