package com.example.siddhantagrawal.check_discuss;

import java.util.List;

import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author Deepak Thakur
 *
 */

public interface DiscussService {

    @GET
    Observable<Population> getPopulation(@Url final String pathParams) ;

    @GET
    public List<Question> getQuestions(@Url final String pathParams);

    @GET
    public List<Comment> getCommentsForQuestion(@Url final String pathParams);

    @GET
    public List<Question> getBookMarkedQuestions(@Url final String pathParams);

    @GET
    public List<Comment> getUserAddedComments(@Url final String pathParams);

    @GET
    public Question getQuestion(@Url final String pathParams);

    @GET
    public boolean likeQuestion(@Url final String pathParams); /** @todo Make it PUT  */

    @GET
    public boolean likeComment(@Url final String pathParams);  /** @todo Make it PUT  */

    @GET
    public boolean bookmarkQuestion(@Url final String pathParams);  /** @todo Make it PUT  */

}
