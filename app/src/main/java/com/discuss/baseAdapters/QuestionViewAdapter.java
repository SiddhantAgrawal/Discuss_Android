package com.discuss.baseAdapters;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.fetcher.impl.DataFetcherImpl;
import com.example.siddhantagrawal.check_discuss.R;
import com.discuss.views.QuestionView;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 *
 * @author siddhant.agrawal
 * @author Deepak Thakur
 *
 */
public class QuestionViewAdapter extends BaseAdapter {

    // Declare Variables
    private Context context;
    private List<Question> data;

    public QuestionViewAdapter(Context context,
                        List<Question> arraylist) {
        this.context = context;
        data = arraylist;
    }

    @Override
    public int getCount() {
        return data.size();
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
        final Question question = data.get(position);

        TextView questionText =  (TextView) itemView.findViewById(R.id.question_short_question);
        TextView likes = (TextView) itemView.findViewById(R.id.question_short_like_value);
        TextView postedBy = (TextView) itemView.findViewById(R.id.question_short_postedby_value);
        TextView difficulty = (TextView) itemView.findViewById(R.id.question_short_difficulty_value) ;

        questionText.setText(question.getText());
        likes.setText(Integer.toString(question.getLikes()));
        postedBy.setText(question.getUserName());
        difficulty.setText(question.getDifficulty());

        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Question questionInfo = data.get(position);
                ArrayList<Comment> comments = new ArrayList<Comment>(); /* TODO(Deepak): find a better way */
                new DataFetcherImpl().
                        getCommentsForQuestion(questionInfo.getQuestionId(),0,0,"").  /* TODO(Deepak): add proper values */
                        onBackpressureBuffer().
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Subscriber<List<Comment>>() {
                            @Override
                            public void onCompleted() {
                                Intent intent = new Intent(context, QuestionView.class);
                                intent.putExtra("questionText", questionInfo.getText());
                                intent.putExtra("likes", questionInfo.getLikes());
                                intent.putExtra("postedBy", questionInfo.getUserName());
                                intent.putExtra("difficulty", questionInfo.getDifficulty());
                                intent.putExtra("comments", comments);
                                Log.e("got comments", "and opening question ");
                                context.startActivity(intent);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(List<Comment> fetchedComments) {
                                Log.e("got comments", "and comments are " + fetchedComments.toString());
                                comments.addAll(fetchedComments);

                            }
                        }); /* TODO(Deepak):  do we really need a new Subscriber each time ??  */

            }
        });
        return itemView;
    }
}
