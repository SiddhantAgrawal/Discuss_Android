package com.discuss;

import android.app.Application;
import android.os.Bundle;

import com.discuss.di.modules.DataModule;
import com.discuss.di.modules.PresenterModule;
import com.discuss.di.modules.RepositoryModule;

/**
 * @author Deepak Thakur
 */
public class DiscussApplication extends Application {
    MainComponent mainComponent;
    public void onCreate() {
        super.onCreate();
        mainComponent = DaggerMainComponent.builder()
                // list of modules that are part of this component need to be created here too
                .dataModule(new DataModule()) // This also corresponds to the name of your module: %component_name%Module
                .presenterModule(new PresenterModule())
                .repositoryModule(new RepositoryModule())
                .build();

    }

    public MainComponent getMainComponent() {
        return mainComponent;
    }
}
