package com.example.siddhantagrawal.check_discuss;

import android.util.Log;

import com.example.siddhantagrawal.check_discuss.Population;
import com.example.siddhantagrawal.check_discuss.DataFetcher;
import com.github.leonardoxh.asyncokhttpclient.AsyncHttpResponse;
import com.github.leonardoxh.asyncokhttpclient.AsyncOkHttpClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;


/**
 * @author Deepak Thakur
 *
 */

public class DataFetcherImpl implements DataFetcher<Population> {
    private static final Gson gson = new Gson();

    @Override
    public Observable<Population> questions() {
        return Observable.<Population>create(new Observable.OnSubscribe<Population>() {
            @Override
            public void call(Subscriber<? super Population> subscriber) {
                try {

                    Log.e("DataFetch", "now will call");
                    // subscriber.onCompleted();

                    ////
                    AsyncOkHttpClient client = new AsyncOkHttpClient();
                    client.get("http://www.google.com", new AsyncHttpResponse() {

                        @Override
                        public void onSuccess(int statusCode, String content) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(content);
                                Log.e("data1", jsonObject.toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("worldpopulation");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Population population = gson.fromJson(jsonArray.get(i).toString(), Population.class);
                                    Log.e("data", population.country);
                                    subscriber.onNext(population);
                                }

                                subscriber.onCompleted();
                            } catch (JSONException e) {
                                Log.e("errrr", e.toString());
                                subscriber.onError(e);
                            }
                        }

                        @Override
                        public void onError(Throwable error, String content) {
                            Log.e("errrr", error.toString());
                            subscriber.onError(error);
                        }

                    });
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
