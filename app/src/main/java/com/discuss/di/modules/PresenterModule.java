package com.discuss.di.modules;

import com.discuss.data.DataRetriever;
import com.discuss.ui.bookmark.BookMarkPresenter;
import com.discuss.ui.bookmark.impl.BookMarkPresenterImpl;
import com.discuss.ui.category.CategorySelectorPresenter;
import com.discuss.ui.category.CategorySelectorPresenterImpl;
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

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterModule {

    @Provides
    @Singleton
    MainFeedPresenter providesMainFeedPresenter(DataRetriever dataRetriever) {
        return new MainFeedPresenterImpl(dataRetriever);
    }

    @Provides
    @Singleton
    LikedPresenter providesLikedPresenter(DataRetriever dataRetriever) {
        return new LikedPresenterImpl(dataRetriever);
    }

    @Provides
    @Singleton
    QuestionViewPresenter providesQuestionViewPresenter(DataRetriever dataRetriever) {
        return new QuestionViewPresenterImpl(dataRetriever);
    }

    @Provides
    @Singleton
    CommentedPresenter providesCommentedPresenter(DataRetriever dataRetriever) {
        return new CommentedPresenterImpl(dataRetriever);
    }

    @Provides
    @Singleton
    BookMarkPresenter providesBookMarkPresenter(DataRetriever dataRetriever) {
        return new BookMarkPresenterImpl(dataRetriever);
    }

    @Provides
    @Singleton
    CategorySelectorPresenter providesCategorySelectorPresenter(DataRetriever dataRetriever) {
        return new CategorySelectorPresenterImpl(dataRetriever);
    }

    @Provides
    @Singleton
    QuestionPostPresenter providesQuestionPostPresenter(DataRetriever dataRetriever) {
        return new QuestionPostPresenterImpl(dataRetriever);
    }

}
