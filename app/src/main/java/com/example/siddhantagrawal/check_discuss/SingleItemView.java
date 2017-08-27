package com.example.siddhantagrawal.check_discuss;

/**
 * Created by siddhant.agrawal on 8/24/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleItemView extends Activity {
    // Declare Variables
    String views;
    String difficulty;
    String text;
    String image;
    ImageLoader imageLoader = new ImageLoader(this);
    ArrayList<HashMap<String, String>> arraylist;
    JSONObject answer_jsonobject;
    JSONArray answer_jsonarray;
    static String TEXT = "text";
    static String IMAGE = "image";
    static String LIKES = "like";
    ListView listview;
    SingleListViewAdapter adapter;
    Button allAnswerButton;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.singleitemview);

        Intent i = getIntent();
        // Get the result of rank
        views = i.getStringExtra(MainActivity.VIEWS);
        // Get the result of country
        difficulty = i.getStringExtra(MainActivity.DIFFICULTY);
        // Get the result of population
        text = i.getStringExtra(MainActivity.TEXT);
        // Get the result of flag
        image = i.getStringExtra(MainActivity.IMAGE);
        // Get the result of flag

        // Locate the TextViews in singleitemview.xml
        TextView siv_view = (TextView) findViewById(R.id.siv_question_views);
        TextView siv_difficulty = (TextView) findViewById(R.id.siv_question_difficulty);
        TextView siv_text = (TextView) findViewById(R.id.siv_question_text);

        // Locate the ImageView in singleitemview.xml
        ImageView siv_image = (ImageView) findViewById(R.id.siv_question_image);

        // Set results to the TextViews
        siv_view.setText(views);
        siv_difficulty.setText(difficulty);
        siv_text.setText(text);

        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class
        if (null != image)
            imageLoader.DisplayImage(image, siv_image);

        new DownloadJSON1().execute();

        allAnswerButton = (Button) findViewById(R.id.answer_allButton);

        allAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleItemView.this, AllAnswerActivity.class);
                startActivity(intent);
            }
        });
    }

    private class DownloadJSON1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            arraylist = new ArrayList<HashMap<String, String>>();
            // Retrieve JSON Objects from the given URL address
            answer_jsonobject = JSONfunctions
                    .getJSONfromURL("http://supply-engg1002.common.blr1.inmobi.com:8082/data/");
//            answer_jsonobject = JSONfunctions
//                    .getJSONfromURL("http://127.0.0.1:8081/data/");

            Log.d("Message from server", answer_jsonobject.toString());

            try {
                // Locate the array name in JSON
                answer_jsonarray = answer_jsonobject.getJSONArray("questions");

                for (int i = 0; i < 10; i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    answer_jsonobject = answer_jsonarray.getJSONObject(i);
                    // Retrive JSON Objects

                    if (answer_jsonobject.has(TEXT))
                        map.put(TEXT, answer_jsonobject.getString(TEXT));
                    if (answer_jsonobject.has(IMAGE))
                        map.put(IMAGE, answer_jsonobject.getString(IMAGE));
                    map.put(LIKES, answer_jsonobject.getString(LIKES));
                    // Set the JSON Objects into the array
                    arraylist.add(map);
                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.answers_listview);
            // Pass the results into ListViewAdapter.java
            adapter = new SingleListViewAdapter(SingleItemView.this, arraylist);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
        }
    }
}