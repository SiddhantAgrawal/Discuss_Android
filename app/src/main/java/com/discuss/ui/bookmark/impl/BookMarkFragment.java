package com.discuss.ui.bookmark.impl;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.discuss.DiscussApplication;
import com.discuss.datatypes.Question;
import com.discuss.ui.BookMarkState;
import com.discuss.ui.LikeState;
import com.discuss.ui.QuestionLikeState;
import com.discuss.ui.QuestionSummary;
import com.discuss.ui.bookmark.BookMarkPresenter;
import com.discuss.ui.feed.impl.MainActivity;
import com.discuss.utils.EndlessScrollListener;
import com.discuss.ui.question.view.QuestionView;
import com.discuss.utils.UIUtil;
import com.example.siddhantagrawal.check_discuss.R;

import javax.inject.Inject;

import rx.functions.Action0;
import rx.functions.Action1;

public class BookMarkFragment extends Fragment implements com.discuss.ui.View {

    @Inject
    BookMarkPresenter bookMarkPresenter;
    QuestionViewAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("BookMarked");
    }
    @Inject
    public BookMarkFragment() {
    }

    @Override
    public void onStop() {
        super.onStop();
        bookMarkPresenter.save();
    }

    @Override
    public void init(Action0 onCompletedAction) {
        bookMarkPresenter.init(onCompletedAction);
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((DiscussApplication) getActivity().getApplication()).getMainComponent().inject(this);
        View itemView = inflater.inflate(R.layout.fragment_bookmarked_questions, container, false);
        ListView listView = (ListView) itemView.findViewById(R.id.fragment_bookmarked_questions);
        adapter = new QuestionViewAdapter(getActivity(), bookMarkPresenter);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new EndlessScrollListener(() -> bookMarkPresenter.update(() -> adapter.notifyDataSetChanged()), 4));

        return itemView;
    }

    public static class QuestionViewAdapter extends BaseAdapter {

        // Declare Variables
        private Context context;
        private BookMarkPresenter bookMarkPresenter;

        public QuestionViewAdapter(Context context, BookMarkPresenter bookMarkPresenter) {
            this.context = context;
            this.bookMarkPresenter = bookMarkPresenter;
        }

        @Override
        public int getCount() {
            return bookMarkPresenter.size();
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
            bookMarkPresenter.get(position).subscribe(new Action1<QuestionSummary>() {
                @Override
                public void call(QuestionSummary questionSummary) {
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
                            bookMarkPresenter);

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
                            bookMarkPresenter);

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
                }
            });
            return itemView;
        }
    }

}