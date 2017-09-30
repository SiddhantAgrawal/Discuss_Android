package com.discuss.fragment.factory;


import android.app.Fragment;
import android.util.Log;

import com.discuss.datatypes.Question;
import com.discuss.fetcher.impl.DataFetcherImpl;
import com.discuss.fragment.LikedQuestionsFragment;

import java.util.ArrayList;

import rx.Observable;

public class LikedQuestionsFragmentFactory extends FragmentFactory<Question>{
    @Override
    public Observable<ArrayList<Question>> getData() {
        Log.e("liked"," data");
        return new DataFetcherImpl().
                getLikedQuestions(0,0,"").map(ArrayList::new).onBackpressureBuffer();
    }

    @Override
    public Observable<Fragment> getRawFragment() {
        Log.e("liked", "object");
        return Observable.just(new LikedQuestionsFragment());
    }
}
