package com.example.siddhantagrawal.check_discuss;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class MainActivity extends Activity {
    // Declare Variables
    JSONObject question_jsonobject;
    JSONArray question_jsonarray;
    ListView listview;
    ListViewAdapter adapter;
    ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, String>> arraylist;
    static String VIEWS = "view";
    static String DIFFICULTY = "difficulty";
    static String TEXT = "text";
    static String IMAGE = "image";
    static String LIKES = "like";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.listview_main);
        // Execute DownloadJSON AsyncTask
        new DownloadJSON().execute();
    }

    // DownloadJSON AsyncTask
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Loading question feeds");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            arraylist = new ArrayList<HashMap<String, String>>();
            // Retrieve JSON Objects from the given URL address
            question_jsonobject = JSONfunctions
                    .getJSONfromURL("http://supply-engg1002.common.blr1.inmobi.com:8082/data/");
//            answer_jsonobject = JSONfunctions
//                    .getJSONfromURL("http://127.0.0.1:8081/data/");

            Log.d("Message from server", question_jsonobject.toString());

            try {
                // Locate the array name in JSON
                question_jsonarray = question_jsonobject.getJSONArray("questions");

                for (int i = 0; i < question_jsonarray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    question_jsonobject = question_jsonarray.getJSONObject(i);
                    // Retrive JSON Objects
                    map.put(VIEWS, question_jsonobject.getString(VIEWS));
                    map.put(DIFFICULTY, question_jsonobject.getString(DIFFICULTY));

                    if (question_jsonobject.has(TEXT))
//                    if (null != answer_jsonobject.get(TEXT))
                        map.put(TEXT, question_jsonobject.getString(TEXT));
                    if (question_jsonobject.has(IMAGE))
                        map.put(IMAGE, question_jsonobject.getString(IMAGE));
                    map.put(LIKES, question_jsonobject.getString(LIKES));
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
            listview = (ListView) findViewById(R.id.questions_listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(MainActivity.this, arraylist);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
}