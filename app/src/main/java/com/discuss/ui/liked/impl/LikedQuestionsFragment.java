package com.discuss.ui.liked.impl;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.ListView;

import com.discuss.DiscussApplication;
import com.discuss.datatypes.Question;
import com.discuss.ui.BookMarkState;
import com.discuss.ui.LikeState;
import com.discuss.ui.QuestionLikeState;
import com.discuss.ui.QuestionSummary;
import com.discuss.ui.bookmark.impl.BookMarkFragment;
import com.discuss.ui.feed.impl.MainActivity;
import com.discuss.ui.liked.LikedPresenter;
import com.discuss.utils.EndlessScrollListener;
import com.discuss.ui.question.view.QuestionView;
import com.discuss.utils.UIUtil;
import com.example.siddhantagrawal.check_discuss.R;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.functions.Action0;

public class LikedQuestionsFragment extends Fragment implements com.discuss.ui.View {

    @Inject
    LikedPresenter likedPresenter;

    @Inject
    public LikedQuestionsFragment() {
    }

    @Override
    public void init(Action0 onCompleteAction) {
        likedPresenter.init(onCompleteAction);
    }

    QuestionViewAdapter adapter;

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("Liked");
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((DiscussApplication) getActivity().getApplication()).getMainComponent().inject(this);
        View itemView = inflater.inflate(R.layout.fragment_liked_questions, container, false);
        ListView listView = (ListView) itemView.findViewById(R.id.fragment_liked_questions);

        adapter = new QuestionViewAdapter(getActivity(), likedPresenter);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new EndlessScrollListener(() -> likedPresenter.update(() -> adapter.notifyDataSetChanged()), 4));

        return itemView;
    }

    public static class QuestionViewAdapter extends BaseAdapter {

        // Declare Variables
        private Context context;
        private LikedPresenter likedPresenter;

        public QuestionViewAdapter(Context context, LikedPresenter likedPresenter) {
            this.context = context;
            this.likedPresenter = likedPresenter;
        }

        @Override
        public int getCount() {
            return likedPresenter.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.question_short, parent, false);
            final QuestionSummary questionSummary = likedPresenter.get(position).toBlocking().first();

            UIUtil.setTextView(itemView, R.id.question_short_question, questionSummary.getText());
            UIUtil.setTextView(itemView, R.id.question_short_like_value, Integer.toString(questionSummary.getLikes()));
            UIUtil.setTextView(itemView, R.id.question_short_difficulty_value, questionSummary.getDifficulty());
            UIUtil.setTextView(itemView, R.id.question_short_view_value, Integer.toString(questionSummary.getViews()));
            UIUtil.setImageView(context, itemView, R.id.question_short_image, questionSummary.getImageUrl());

            ImageView likeImage = itemView.findViewById(R.id.question_short_like);
            TextView textView = itemView.findViewById(R.id.question_short_like_value);

            final LikeState likeState = new QuestionLikeState(questionSummary.getQuestionId(),
                    questionSummary.getLikes(),
                    questionSummary.isLiked(),
                    likeImage,
                    textView,
                    ContextCompat.getDrawable(QuestionViewAdapter.this.context, R.drawable.like_icon),
                    ContextCompat.getDrawable(QuestionViewAdapter.this.context, R.drawable.liked),
                    likedPresenter);

            likeImage.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    likeState.pressUpdate();
                }
                return true;
            });

            ImageView bookmarkImage = itemView.findViewById(R.id.question_short_bookmark);

            final BookMarkState bookMarkState = new BookMarkState(questionSummary.getQuestionId(),
                    questionSummary.isBookmarked(),
                    bookmarkImage,
                    ContextCompat.getDrawable(QuestionViewAdapter.this.context, R.drawable.bookmark),
                    ContextCompat.getDrawable(QuestionViewAdapter.this.context, R.drawable.bookmark),
                    likedPresenter);

            bookmarkImage.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    bookMarkState.pressUpdate();
                }
                return true;
            });


            itemView.setOnClickListener(arg0 -> {
                Intent intent = new Intent(context, QuestionView.class);
                intent.putExtra("questionId", questionSummary.getQuestionId());
                context.startActivity(intent);
            });
            return itemView;
        }
    }

}
