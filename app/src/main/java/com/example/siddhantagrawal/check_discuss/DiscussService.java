package com.example.siddhantagrawal.check_discuss;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * @author Deepak Thakur
 *
 */

public interface DiscussService {

    String SERVICE_ENDPOINT = "https://api.github.com";

    @GET("/file.json")
     Observable<Population> getPopulation() ;
}
