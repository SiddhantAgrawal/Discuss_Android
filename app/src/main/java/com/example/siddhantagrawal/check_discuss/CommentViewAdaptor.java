package com.example.siddhantagrawal.check_discuss;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.discuss.datatypes.Comment;

import java.util.List;


public class CommentViewAdaptor extends BaseAdapter {
    // Declare Variables
    private Context context;
    private List<Comment> data;


    CommentViewAdaptor(Context context,
                    List<Comment> arraylist) {
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

    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.comment_short, parent, false); /* TODO(Deepak): See if this has performance issues */
        final Comment comment = data.get(position);

        TextView questionText =  (TextView) itemView.findViewById(R.id.comment_short_question);
        TextView likes = (TextView) itemView.findViewById(R.id.comment_short_like_value);
        TextView postedBy = (TextView) itemView.findViewById(R.id.comment_short_user_value);

        questionText.setText(comment.getText());
        likes.setText(Integer.toString(comment.getLikes()));
        postedBy.setText(comment.getUserName());

        return itemView;
    }
}
