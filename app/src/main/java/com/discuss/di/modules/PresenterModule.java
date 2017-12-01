package com.discuss.di.modules;

import com.discuss.data.BookMarkRepository;
import com.discuss.data.CommentRepository;
import com.discuss.data.DataRetriever;
import com.discuss.data.LikedQuestionsRepository;
import com.discuss.data.QuestionRepository;
import com.discuss.data.QuestionsAnsweredRepository;
import com.discuss.ui.bookmark.BookMarkPresenter;
import com.discuss.ui.bookmark.impl.BookMarkPresenterImpl;
import com.discuss.ui.category.CategorySelectorPresenter;
import com.discuss.ui.category.CategorySelectorPresenterImpl;
import com.discuss.ui.comment.view.UserCommentPresenter;
import com.discuss.ui.comment.view.impl.UserCommentPresenterImpl;
import com.discuss.ui.commented.CommentedPresenter;
import com.discuss.ui.commented.impl.CommentedPresenterImpl;
import com.discuss.ui.feed.MainFeedPresenter;
import com.discuss.ui.feed.impl.MainFeedPresenterImpl;
import com.discuss.ui.liked.LikedPresenter;
import com.discuss.ui.liked.impl.LikedPresenterImpl;
import com.discuss.ui.question.post.QuestionPostPresenter;
import com.discuss.ui.question.post.impl.QuestionPostPresenterImpl;
import com.discuss.ui.question.view.QuestionViewPresenter;
import com.discuss.ui.question.view.impl.QuestionViewPresenterImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterModule {

    @Provides
    MainFeedPresenter providesMainFeedPresenter(QuestionRepository questionRepository) {
        return new MainFeedPresenterImpl(questionRepository);
    }

    @Provides
    LikedPresenter providesLikedPresenter(LikedQuestionsRepository likedQuestionsRepository) {
        return new LikedPresenterImpl(likedQuestionsRepository);
    }

    @Provides
    QuestionViewPresenter providesQuestionViewPresenter(QuestionRepository questionRepository, CommentRepository commentRepository) {
        return new QuestionViewPresenterImpl(questionRepository, commentRepository);
    }

    @Provides
    CommentedPresenter providesCommentedPresenter(QuestionsAnsweredRepository answeredRepository) {
        return new CommentedPresenterImpl(answeredRepository);
    }

    @Provides
    BookMarkPresenter providesBookMarkPresenter(BookMarkRepository bookMarkRepository) {
        return new BookMarkPresenterImpl(bookMarkRepository);
    }

    @Provides
    CategorySelectorPresenter providesCategorySelectorPresenter(DataRetriever dataRetriever) {
        return new CategorySelectorPresenterImpl(dataRetriever);
    }

    @Provides
    QuestionPostPresenter providesQuestionPostPresenter(DataRetriever dataRetriever) {
        return new QuestionPostPresenterImpl(dataRetriever);
    }

    @Provides
    UserCommentPresenter providesUserCommentPresenter(CommentRepository commentRepository) {
        return new UserCommentPresenterImpl(commentRepository);
    }

    @Provides
    @Singleton
    @Named("user_name")
    public String providesUserName() {
        return "Janice";
    }

    @Provides
    @Singleton
    @Named("user_id")
    public int providesUserID() {
        return 10;
    }


}
