package com.discuss;


import com.discuss.di.modules.DataModule;
import com.discuss.di.modules.PresenterModule;
import com.discuss.di.modules.RepositoryModule;
import com.discuss.ui.bookmark.impl.BookMarkFragment;
import com.discuss.ui.category.CategorySelector;
import com.discuss.ui.comment.post.UserCommentPost;
import com.discuss.ui.comment.view.UserComment;
import com.discuss.ui.commented.impl.CommentedQuestionFragment;
import com.discuss.ui.feed.impl.MainActivity;
import com.discuss.ui.liked.impl.LikedQuestionsFragment;
import com.discuss.ui.question.post.impl.AskQuestionView;
import com.discuss.ui.question.view.QuestionView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Deepak Thakur
 */
@Singleton
@Component(modules = {PresenterModule.class, DataModule.class, RepositoryModule.class})
public interface MainComponent {
    void inject(MainActivity mainActivity);
    void inject(BookMarkFragment bookMarkFragment);
    void inject(CategorySelector categorySelector);
    void inject(CommentedQuestionFragment commentedQuestionFragment);
    void inject(LikedQuestionsFragment likedQuestionsFragment);
    void inject(AskQuestionView askQuestionView);
    void inject(QuestionView questionView);
    void inject(UserComment userComment);
    void inject(UserCommentPost userCommentPost);
}
