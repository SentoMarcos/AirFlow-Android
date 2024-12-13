package com.example.smariba_upv.airflow;

import android.app.Application;

import org.osmdroid.config.Configuration;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Configurar el UserAgent para osmdroid
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
    }
}
