package com.discuss.di.modules;


import com.discuss.data.DataRetriever;
import com.discuss.data.DataUpdater;
import com.discuss.data.DiscussService;
import com.discuss.data.impl.DataRetrieverImpl;
import com.discuss.data.impl.DataUpdaterImpl;
import com.discuss.data.StateDiff;
import com.discuss.data.impl.StateImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class DataModule {

    @Provides
    @Singleton
    public DataRetriever providesDataRetriever(DiscussService discussService) {
        return new DataRetrieverImpl(discussService);
    }

    @Provides
    @Singleton
    public DataUpdater providesDataUpdater(DiscussService discussService) {
        return new DataUpdaterImpl(discussService);
    }

    @Provides
    @Singleton
    public StateDiff providesStateDiff(DataUpdater dataUpdater) {
        return new StateImpl(dataUpdater);
    }

    @Singleton
    @Provides
    @Named("server_end_point")
    public String providesEndpoint() {
        return "http://192.168.122.1:8070/";
    }



    @Singleton
    @Provides
    public DiscussService providesDiscussService(@Named("server_end_point") String endpoint) {
        return new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(DiscussService.class);
    }
}
