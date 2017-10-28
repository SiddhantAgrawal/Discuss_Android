package com.discuss.di.modules;


import com.discuss.data.DataFetcher;
import com.discuss.data.impl.DataFetcherImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    public DataFetcher providesDataFetcher(@Named("server_end_point") String endpoint) {
        return new DataFetcherImpl(endpoint);
    }

    @Singleton
    @Provides
    @Named("server_end_point")
    public String providesEndpoint() {
        return "http://192.168.122.1:8070/";
    }
}
