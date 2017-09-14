package com.example.siddhantagrawal.check_discuss;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;


/**
 * @author Deepak Thakur
 *
 */

public class DataFetcherImpl implements DataFetcher<Population> {
    private static final Gson gson = new Gson();
    private static final String SERVICE_ENDPOINT = "http://10.14.125.250:8070";


    public static Retrofit createRetrofit() {
         return new Retrofit.Builder()
                .baseUrl(DataFetcherImpl.SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Override
    public Observable<Population> questions() {
        DiscussService discussService = createRetrofit().create(DiscussService.class);
        return discussService.getPopulation();
    }

}
