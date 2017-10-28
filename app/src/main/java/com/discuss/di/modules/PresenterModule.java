package com.discuss.di.modules;

import com.discuss.data.DataFetcher;
import com.discuss.ui.bookmark.BookMarkPresenter;
import com.discuss.ui.bookmark.impl.BookMarkPresenterImpl;
import com.discuss.ui.category.CategorySelector;
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
    MainFeedPresenter providesMainFeedPresenter(DataFetcher dataFetcher) {
        return new MainFeedPresenterImpl(dataFetcher);
    }

    @Provides
    @Singleton
    LikedPresenter providesLikedPresenter(DataFetcher dataFetcher) {
        return new LikedPresenterImpl(dataFetcher);
    }

    @Provides
    @Singleton
    QuestionViewPresenter providesQuestionViewPresenter(DataFetcher dataFetcher) {
        return new QuestionViewPresenterImpl(dataFetcher);
    }

    @Provides
    @Singleton
    CommentedPresenter providesCommentedPresenter(DataFetcher dataFetcher) {
        return new CommentedPresenterImpl(dataFetcher);
    }

    @Provides
    @Singleton
    BookMarkPresenter providesBookMarkPresenter(DataFetcher dataFetcher) {
        return new BookMarkPresenterImpl(dataFetcher);
    }

    @Provides
    @Singleton
    CategorySelectorPresenter providesCategorySelectorPresenter(DataFetcher dataFetcher) {
        return new CategorySelectorPresenterImpl(dataFetcher);
    }

    @Provides
    @Singleton
    QuestionPostPresenter providesQuestionPostPresenter(DataFetcher dataFetcher) {
        return new QuestionPostPresenterImpl(dataFetcher);
    }

}
