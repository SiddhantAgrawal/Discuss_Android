package com.example.siddhantagrawal.check_discuss;


import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.discuss.datatypes.Question;


/**
 *
 * @author siddhant.agrawal
 * @author Deepak Thakur
 *
 */
class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    private Context context;
    private List<Question> data;

    ListViewAdapter(Context context,
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

        View itemView = inflater.inflate(R.layout.question_short, parent, false); /* @todo See if this has performance issues */
        final Question question = data.get(position);

        TextView questionText =  (TextView) itemView.findViewById(R.id.question_short_question);
        TextView likes = (TextView) itemView.findViewById(R.id.question_short_like_value);
        TextView postedBy = (TextView) itemView.findViewById(R.id.question_short_user_value);

        questionText.setText(question.getText());
        likes.setText(Integer.toString(question.getLikes()));
        postedBy.setText(question.getUserName());

        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Question questionInfo = data.get(position);
                Intent intent = new Intent(context, SingleItemView.class);

                intent.putExtra("questionText", questionInfo.getText());
                intent.putExtra("likes", questionInfo.getLikes());
                intent.putExtra("postedBy", questionInfo.getUserName());

                context.startActivity(intent);

            }
        });
        return itemView;
    }
}
