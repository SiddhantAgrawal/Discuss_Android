package com.example.siddhantagrawal.check_discuss;

/**
 * Created by siddhant.agrawal on 8/24/17.
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> questions_data;
    ImageLoader imageLoader;
    HashMap<String, String> specific_question = new HashMap<String, String>();

    public ListViewAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        questions_data = arraylist;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return questions_data.size();
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
        TextView question_views;
        TextView question_difficulty;
        TextView question_text;
        ImageView question_image;
        TextView question_likes_button;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        specific_question = questions_data.get(position);

        // Locate the TextViews in listview_item.xml
        question_views = (TextView) itemView.findViewById(R.id.question_views);
        question_difficulty = (TextView) itemView.findViewById(R.id.question_difficulty);
        question_text = (TextView) itemView.findViewById(R.id.question_text);
        question_likes_button = (TextView) itemView.findViewById(R.id.question_likes_button);


        // Locate the ImageView in listview_item.xml
        question_image = (ImageView) itemView.findViewById(R.id.question_image);


        // Capture position and set results to the TextViews
        question_views.setText(specific_question.get(MainActivity.VIEWS));
        question_difficulty.setText(specific_question.get(MainActivity.DIFFICULTY));
        if (null != specific_question.get(MainActivity.TEXT))
            question_text.setText(specific_question.get(MainActivity.TEXT));
        question_likes_button.setText(specific_question.get(MainActivity.LIKES));


        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class

        if (null != specific_question.get(MainActivity.IMAGE))
            imageLoader.DisplayImage(specific_question.get(MainActivity.IMAGE), question_image);
        // Capture ListView item click
        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                specific_question = questions_data.get(position);
                Intent intent = new Intent(context, SingleItemView.class);
                // Pass all answers_data rank
                intent.putExtra(MainActivity.VIEWS, specific_question.get(MainActivity.VIEWS));
                // Pass all answers_data country
                intent.putExtra(MainActivity.DIFFICULTY, specific_question.get(MainActivity.DIFFICULTY));
                // Pass all answers_data population
                if (null != specific_question.get(MainActivity.TEXT))
                    intent.putExtra(MainActivity.TEXT, specific_question.get(MainActivity.TEXT));
                // Pass all answers_data flag

                if (null != specific_question.get(MainActivity.IMAGE))
                    intent.putExtra(MainActivity.IMAGE, specific_question.get(MainActivity.IMAGE));

                intent.putExtra(MainActivity.LIKES, specific_question.get(MainActivity.LIKES));
                // Start SingleItemView Class
                context.startActivity(intent);

            }
        });
        return itemView;
    }
}
