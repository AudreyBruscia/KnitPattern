package xyz.audbru.knitpattern;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by User on 5/14/2016.
 */
public class KnitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
