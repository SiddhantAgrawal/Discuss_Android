package com.example.siddhantagrawal.check_discuss;


import rx.Observable;

/**
 * @author Deepak Thakur
 *
 */

public interface DataFetcher<T> {
    Observable<T> questions();
}
