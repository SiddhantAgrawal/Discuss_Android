package com.example.siddhantagrawal.check_discuss;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by siddhant.agrawal on 8/27/17.
 */

public class AllAnswerActivity extends Activity {

    JSONObject answer_jsonobject;
    JSONArray answer_jsonarray;
    ListView listview;
    AllAnswerListViewAdapter adapter;

    ArrayList<HashMap<String, String>> arraylist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.listview_allanswer);

        new DownloadJSON2().execute();
    }

    // DownloadJSON AsyncTask
    private class DownloadJSON2 extends AsyncTask<Void, Void, Void> {

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

                for (int i = 0; i < answer_jsonarray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    answer_jsonobject = answer_jsonarray.getJSONObject(i);
                    // Retrive JSON Objects
                    map.put(MainActivity.VIEWS, answer_jsonobject.getString(MainActivity.VIEWS));

                    if (answer_jsonobject.has(MainActivity.TEXT))
//                    if (null != answer_jsonobject.get(TEXT))
                        map.put(MainActivity.TEXT, answer_jsonobject.getString(MainActivity.TEXT));
                    if (answer_jsonobject.has(MainActivity.IMAGE))
                        map.put(MainActivity.IMAGE, answer_jsonobject.getString(MainActivity.IMAGE));
                    map.put(MainActivity.LIKES, answer_jsonobject.getString(MainActivity.LIKES));
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
            listview = (ListView) findViewById(R.id.all_answer_listview);
            // Pass the results into ListViewAdapter.java
            adapter = new AllAnswerListViewAdapter(AllAnswerActivity.this, arraylist);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
        }
    }
}
