package com.discuss.fragment.factory;


import android.app.Fragment;

import com.discuss.datatypes.Question;
import com.discuss.fetcher.impl.DataFetcherImpl;
import com.discuss.fragment.BookMarkFragment;

import java.util.ArrayList;

import rx.Observable;

public class BookmarkedQuestionsFragmentFactory extends FragmentFactory<Question> {
    @Override
    public Observable<ArrayList<Question>> getData() {
        return new DataFetcherImpl().
                getBookMarkedQuestions(0,0,"").map(ArrayList::new).onBackpressureBuffer();
    }

    @Override
    public Observable<Fragment> getRawFragment() {
        return Observable.just(new BookMarkFragment());
    }
}
