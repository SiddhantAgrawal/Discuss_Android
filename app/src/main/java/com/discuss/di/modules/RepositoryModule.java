package com.discuss.di.modules;

import com.discuss.data.BookMarkRepository;
import com.discuss.data.CommentRepository;
import com.discuss.data.DataRetriever;
import com.discuss.data.DataUpdater;
import com.discuss.data.LikedQuestionsRepository;
import com.discuss.data.QuestionRepository;
import com.discuss.data.QuestionsAnsweredRepository;
import com.discuss.data.StateDiff;
import com.discuss.data.impl.BookMarkQuestionRepositoryImpl;
import com.discuss.data.impl.CommentRepositoryImpl;
import com.discuss.data.impl.LikedQuestionsRepositoryImpl;
import com.discuss.data.impl.QuestionRepositoryImpl;
import com.discuss.data.impl.QuestionsAnsweredRepositoryImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Deepak Thakur
 */
@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public QuestionRepository providesQuestionRepository(DataRetriever dataRetriever,
                                                         StateDiff stateDiff,
                                                         @Named("user_id") final int userID) {
        return new QuestionRepositoryImpl(dataRetriever, stateDiff, userID);
    }

    @Provides
    @Singleton
    public LikedQuestionsRepository providesLikedQuestionsRepository(DataRetriever dataRetriever,
                                                                     StateDiff stateDiff,
                                                                     @Named("user_id") final int userID) {
        return new LikedQuestionsRepositoryImpl(dataRetriever, stateDiff, userID);
    }

    @Provides
    @Singleton
    public BookMarkRepository providesBookMarkRepository(DataRetriever dataRetriever,
                                                         StateDiff stateDiff,
                                                         @Named("user_id") final int userID) {
        return new BookMarkQuestionRepositoryImpl(dataRetriever, stateDiff, userID);
    }

    @Provides
    @Singleton
    public CommentRepository providesCommentRepository(DataRetriever dataRetriever,
                                                       DataUpdater dataUpdater,
                                                       StateDiff stateDiff,
                                                       QuestionRepository questionRepository,
                                                       @Named("user_id") final int userID) {
        return new CommentRepositoryImpl(dataRetriever, dataUpdater, stateDiff, questionRepository, userID);
    }

    @Provides
    @Singleton
    public QuestionsAnsweredRepository providesQuestionsAnsweredRepository(DataRetriever dataRetriever,
                                                                           StateDiff stateDiff,
                                                                           @Named("user_id") final int userID) {
        return new QuestionsAnsweredRepositoryImpl(dataRetriever, stateDiff, userID);

    }

}
