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

import com.discuss.datatypes.Question;
import com.discuss.ui.bookmark.BookMarkPresenter;
import com.discuss.ui.feed.impl.MainActivity;
import com.discuss.utils.EndlessScrollListener;
import com.discuss.ui.question.view.QuestionView;
import com.example.siddhantagrawal.check_discuss.R;

import rx.functions.Action0;

public class BookMarkFragment extends Fragment implements com.discuss.ui.View {

    final private BookMarkPresenter<Question> bookMarkPresenter;
    QuestionViewAdapter adapter;
    public BookMarkFragment() {
        bookMarkPresenter = new BookMarkPresenterImpl();
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("BookMarked");
    }

    @Override
    public void init(Action0 onCompletedAction) {
        bookMarkPresenter.init(onCompletedAction);
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        private BookMarkPresenter<Question> bookMarkPresenter;

        public QuestionViewAdapter(Context context, BookMarkPresenter<Question> bookMarkPresenter) {
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

            View itemView = inflater.inflate(R.layout.question_short, parent, false); /* TODO(Deepak): See if this has performance issues */
            final Question question = bookMarkPresenter.get(position).toBlocking().first();

            TextView questionText =  (TextView) itemView.findViewById(R.id.question_short_question);
            TextView likes = (TextView) itemView.findViewById(R.id.question_short_like_value);
            TextView postedBy = (TextView) itemView.findViewById(R.id.question_short_postedby_value);
            TextView difficulty = (TextView) itemView.findViewById(R.id.question_short_difficulty_value) ;

            questionText.setText(question.getText());
            likes.setText(Integer.toString(question.getLikes()));
            postedBy.setText(question.getUserName());
            difficulty.setText(question.getDifficulty());
            ImageView imageView = itemView.findViewById(R.id.question_short_like_button);
            if (question.isLiked())
                imageView.setImageDrawable(ContextCompat.getDrawable(BookMarkFragment.QuestionViewAdapter.this.context, R.drawable.liked));
            else
                imageView.setImageDrawable(ContextCompat.getDrawable(BookMarkFragment.QuestionViewAdapter.this.context, R.drawable.like_icon));
            final boolean questionOrigionallyLiked = question.isLiked();
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.e("user has liked :- ", String.valueOf(question.isLiked()));
                        if (question.isLiked()) {
                            imageView.setImageDrawable(ContextCompat.getDrawable(BookMarkFragment.QuestionViewAdapter.this.context, R.drawable.like_icon));
                            if(questionOrigionallyLiked)
                                likes.setText(Integer.toString(question.getLikes() - 1));
                            else
                                likes.setText(Integer.toString(question.getLikes()));
                        } else {
                            imageView.setImageDrawable(ContextCompat.getDrawable(BookMarkFragment.QuestionViewAdapter.this.context, R.drawable.liked));
                            if(!questionOrigionallyLiked)
                                likes.setText(Integer.toString(question.getLikes() + 1));
                            else
                                likes.setText(Integer.toString(question.getLikes()));
                        }
                        question.setLiked(!question.isLiked());
                    }
                    return true;
                }
            });

            itemView.setOnClickListener(arg0 -> {
                Intent intent = new Intent(context, QuestionView.class);
                intent.putExtra("question", question);
                context.startActivity(intent);
            });
            return itemView;
        }
    }

}