package com.discuss;

import com.discuss.data.DataRetriever;
import com.discuss.di.modules.DataModule;
import com.discuss.di.modules.DataModule_ProvidesDataFetcherFactory;
import com.discuss.di.modules.DataModule_ProvidesEndpointFactory;
import com.discuss.di.modules.PresenterModule;
import com.discuss.di.modules.PresenterModule_ProvidesBookMarkPresenterFactory;
import com.discuss.di.modules.PresenterModule_ProvidesCategorySelectorPresenterFactory;
import com.discuss.di.modules.PresenterModule_ProvidesCommentedPresenterFactory;
import com.discuss.di.modules.PresenterModule_ProvidesLikedPresenterFactory;
import com.discuss.di.modules.PresenterModule_ProvidesMainFeedPresenterFactory;
import com.discuss.di.modules.PresenterModule_ProvidesQuestionPostPresenterFactory;
import com.discuss.di.modules.PresenterModule_ProvidesQuestionViewPresenterFactory;
import com.discuss.ui.bookmark.BookMarkPresenter;
import com.discuss.ui.bookmark.impl.BookMarkFragment;
import com.discuss.ui.bookmark.impl.BookMarkFragment_Factory;
import com.discuss.ui.bookmark.impl.BookMarkFragment_MembersInjector;
import com.discuss.ui.category.CategorySelector;
import com.discuss.ui.category.CategorySelectorPresenter;
import com.discuss.ui.category.CategorySelector_MembersInjector;
import com.discuss.ui.commented.CommentedPresenter;
import com.discuss.ui.commented.impl.CommentedQuestionFragment;
import com.discuss.ui.commented.impl.CommentedQuestionFragment_Factory;
import com.discuss.ui.commented.impl.CommentedQuestionFragment_MembersInjector;
import com.discuss.ui.feed.MainFeedPresenter;
import com.discuss.ui.feed.impl.MainActivity;
import com.discuss.ui.feed.impl.MainActivity_MembersInjector;
import com.discuss.ui.liked.LikedPresenter;
import com.discuss.ui.liked.impl.LikedQuestionsFragment;
import com.discuss.ui.liked.impl.LikedQuestionsFragment_Factory;
import com.discuss.ui.liked.impl.LikedQuestionsFragment_MembersInjector;
import com.discuss.ui.question.post.QuestionPostPresenter;
import com.discuss.ui.question.post.impl.AskQuestionView;
import com.discuss.ui.question.post.impl.AskQuestionView_MembersInjector;
import com.discuss.ui.question.view.QuestionView;
import com.discuss.ui.question.view.QuestionViewPresenter;
import com.discuss.ui.question.view.QuestionView_MembersInjector;
import dagger.MembersInjector;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
        value = "dagger.internal.codegen.ComponentProcessor",
        comments = "https://google.github.io/dagger"
)
public final class MainComponentImpl implements MainComponent {
    private Provider<String> providesEndpointProvider;

    private Provider<DataRetriever> providesDataFetcherProvider;

    private Provider<MainFeedPresenter> providesMainFeedPresenterProvider;

    private Provider<BookMarkPresenter> providesBookMarkPresenterProvider;

    private MembersInjector<BookMarkFragment> bookMarkFragmentMembersInjector;

    private Provider<BookMarkFragment> bookMarkFragmentProvider;

    private Provider<LikedPresenter> providesLikedPresenterProvider;

    private MembersInjector<LikedQuestionsFragment> likedQuestionsFragmentMembersInjector;

    private Provider<LikedQuestionsFragment> likedQuestionsFragmentProvider;

    private Provider<CommentedPresenter> providesCommentedPresenterProvider;

    private MembersInjector<CommentedQuestionFragment> commentedQuestionFragmentMembersInjector;

    private Provider<CommentedQuestionFragment> commentedQuestionFragmentProvider;

    private MembersInjector<MainActivity> mainActivityMembersInjector;

    private Provider<CategorySelectorPresenter> providesCategorySelectorPresenterProvider;

    private MembersInjector<CategorySelector> categorySelectorMembersInjector;

    private Provider<QuestionPostPresenter> providesQuestionPostPresenterProvider;

    private MembersInjector<AskQuestionView> askQuestionViewMembersInjector;

    private Provider<QuestionViewPresenter> providesQuestionViewPresenterProvider;

    private MembersInjector<QuestionView> questionViewMembersInjector;

    private MainComponentImpl(Builder builder) {
        assert builder != null;
        initialize(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MainComponent create() {
        return new Builder().build();
    }

    @SuppressWarnings("unchecked")
    private void initialize(final Builder builder) {

        this.providesEndpointProvider =
                DoubleCheck.provider(DataModule_ProvidesEndpointFactory.create(builder.dataModule));

        this.providesDataFetcherProvider =
                DoubleCheck.provider(
                        DataModule_ProvidesDataFetcherFactory.create(
                                builder.dataModule, providesEndpointProvider));

        this.providesMainFeedPresenterProvider =
                DoubleCheck.provider(
                        PresenterModule_ProvidesMainFeedPresenterFactory.create(
                                builder.presenterModule, providesDataFetcherProvider));

        this.providesBookMarkPresenterProvider =
                DoubleCheck.provider(
                        PresenterModule_ProvidesBookMarkPresenterFactory.create(
                                builder.presenterModule, providesDataFetcherProvider));

        this.bookMarkFragmentMembersInjector =
                BookMarkFragment_MembersInjector.create(providesBookMarkPresenterProvider);

        this.bookMarkFragmentProvider =
                BookMarkFragment_Factory.create(bookMarkFragmentMembersInjector);

        this.providesLikedPresenterProvider =
                DoubleCheck.provider(
                        PresenterModule_ProvidesLikedPresenterFactory.create(
                                builder.presenterModule, providesDataFetcherProvider));

        this.likedQuestionsFragmentMembersInjector =
                LikedQuestionsFragment_MembersInjector.create(providesLikedPresenterProvider);

        this.likedQuestionsFragmentProvider =
                LikedQuestionsFragment_Factory.create(likedQuestionsFragmentMembersInjector);

        this.providesCommentedPresenterProvider =
                DoubleCheck.provider(
                        PresenterModule_ProvidesCommentedPresenterFactory.create(
                                builder.presenterModule, providesDataFetcherProvider));

        this.commentedQuestionFragmentMembersInjector =
                CommentedQuestionFragment_MembersInjector.create(providesCommentedPresenterProvider);

        this.commentedQuestionFragmentProvider =
                CommentedQuestionFragment_Factory.create(commentedQuestionFragmentMembersInjector);

        this.mainActivityMembersInjector =
                MainActivity_MembersInjector.create(
                        providesMainFeedPresenterProvider,
                        bookMarkFragmentProvider,
                        likedQuestionsFragmentProvider,
                        commentedQuestionFragmentProvider);

        this.providesCategorySelectorPresenterProvider =
                DoubleCheck.provider(
                        PresenterModule_ProvidesCategorySelectorPresenterFactory.create(
                                builder.presenterModule, providesDataFetcherProvider));

        this.categorySelectorMembersInjector =
                CategorySelector_MembersInjector.create(providesCategorySelectorPresenterProvider);

        this.providesQuestionPostPresenterProvider =
                DoubleCheck.provider(
                        PresenterModule_ProvidesQuestionPostPresenterFactory.create(
                                builder.presenterModule, providesDataFetcherProvider));

        this.askQuestionViewMembersInjector =
                AskQuestionView_MembersInjector.create(providesQuestionPostPresenterProvider);

        this.providesQuestionViewPresenterProvider =
                DoubleCheck.provider(
                        PresenterModule_ProvidesQuestionViewPresenterFactory.create(
                                builder.presenterModule, providesDataFetcherProvider));

        this.questionViewMembersInjector =
                QuestionView_MembersInjector.create(providesQuestionViewPresenterProvider);
    }

    @Override
    public void inject(MainActivity mainActivity) {
        mainActivityMembersInjector.injectMembers(mainActivity);
    }

    @Override
    public void inject(BookMarkFragment bookMarkFragment) {
        bookMarkFragmentMembersInjector.injectMembers(bookMarkFragment);
    }

    @Override
    public void inject(CategorySelector categorySelector) {
        categorySelectorMembersInjector.injectMembers(categorySelector);
    }

    @Override
    public void inject(CommentedQuestionFragment commentedQuestionFragment) {
        commentedQuestionFragmentMembersInjector.injectMembers(commentedQuestionFragment);
    }

    @Override
    public void inject(LikedQuestionsFragment likedQuestionsFragment) {
        likedQuestionsFragmentMembersInjector.injectMembers(likedQuestionsFragment);
    }

    @Override
    public void inject(AskQuestionView askQuestionView) {
        askQuestionViewMembersInjector.injectMembers(askQuestionView);
    }

    @Override
    public void inject(QuestionView questionView) {
        questionViewMembersInjector.injectMembers(questionView);
    }

    public static final class Builder {
        private DataModule dataModule;

        private PresenterModule presenterModule;

        private Builder() {}

        public MainComponent build() {
            if (dataModule == null) {
                this.dataModule = new DataModule();
            }
            if (presenterModule == null) {
                this.presenterModule = new PresenterModule();
            }
            return new MainComponentImpl(this);
        }

        public Builder presenterModule(PresenterModule presenterModule) {
            this.presenterModule = Preconditions.checkNotNull(presenterModule);
            return this;
        }

        public Builder dataModule(DataModule dataModule) {
            this.dataModule = Preconditions.checkNotNull(dataModule);
            return this;
        }
    }
}
