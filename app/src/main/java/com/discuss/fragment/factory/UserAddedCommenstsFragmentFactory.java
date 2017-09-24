package com.discuss.fragment.factory;


import android.app.Fragment;

import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.fetcher.impl.DataFetcherImpl;
import com.discuss.fragment.UserAddedCommentsFragment;

import java.util.ArrayList;

import rx.Observable;

public class UserAddedCommenstsFragmentFactory extends FragmentFactory<Comment>{
    @Override
    public Observable<ArrayList<Comment>> getData() {
        return new DataFetcherImpl().
                getUserAddedComments(0,0,"").map(ArrayList::new).onBackpressureBuffer();
    }

    @Override
    public Observable<Fragment> getRawFragment() {
        return Observable.just(new UserAddedCommentsFragment());
    }
}
