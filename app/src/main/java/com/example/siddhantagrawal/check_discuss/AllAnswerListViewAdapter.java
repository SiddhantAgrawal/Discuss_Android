package com.example.siddhantagrawal.check_discuss;

/**
 * Created by siddhant.agrawal on 8/24/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AllAnswerListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> answers_data;
    ImageLoader imageLoader;
    HashMap<String, String> specific_answer = new HashMap<String, String>();

    public AllAnswerListViewAdapter(Context context,
                                    ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        answers_data = arraylist;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return answers_data.size();
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
        // Declare Variables
        TextView answer_views;
        TextView answer_text;
        ImageView answer_image;
        TextView answer_likes;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item_answer, parent, false);
        // Get the position
        specific_answer = answers_data.get(position);

        // Locate the TextViews in listview_item.xml
        answer_views = (TextView) itemView.findViewById(R.id.all_answer_views);
        answer_text = (TextView) itemView.findViewById(R.id.all_answer_text);
        answer_likes = (TextView) itemView.findViewById(R.id.all_answer_likes_button);


        // Locate the ImageView in listview_item.xml
        answer_image = (ImageView) itemView.findViewById(R.id.all_answer_image);


        // Capture position and set results to the TextViews

//        answer_views.setText(specific_answer.get(MainActivity.VIEWS));
        if (null != specific_answer.get(MainActivity.TEXT))
            answer_text.setText(specific_answer.get(MainActivity.TEXT));
        answer_likes.setText(specific_answer.get(MainActivity.LIKES));


        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class

        if (null != specific_answer.get(MainActivity.IMAGE))
            imageLoader.DisplayImage(specific_answer.get(MainActivity.IMAGE), answer_image);

        return itemView;
    }
}
