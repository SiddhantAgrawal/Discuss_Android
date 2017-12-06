package com.discuss.data;


import rx.Observable;

/**
 *
 * @author Deepak Thakur
 */
public interface FetchStrategy<T> {
    Observable<T> execute();
}
