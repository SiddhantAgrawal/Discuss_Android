package com.discuss.fragment.factory;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

import rx.Observable;
import rx.functions.Func2;

public abstract class FragmentFactory<T extends Serializable> {
    private T t;
    public Observable<Fragment> createFragment() {
        Observable<Fragment> fragmentObservable = getRawFragment();
        Observable<ArrayList<T>> dataObservable = getData();
        Observable<Fragment> stitchedFragment = Observable.zip(fragmentObservable, dataObservable, new Func2<Fragment, ArrayList<T>, Fragment>() {
            @Override
            public Fragment call(Fragment fragment, ArrayList<T> data) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", data);
                Log.e("fac", data.toString());
                fragment.setArguments(bundle);
                return fragment;
            }
        });
        return stitchedFragment;
    }

    public abstract Observable<ArrayList<T>> getData();

    public abstract Observable<Fragment> getRawFragment();
}
