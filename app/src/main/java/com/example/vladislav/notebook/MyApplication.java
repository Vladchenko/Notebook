package com.example.vladislav.notebook;

import android.app.Application;

import com.example.vladislav.notebook.database.DBHelper;


/**
 * Created by vladislav on 14.03.17.
 * Used to start a DBHelper before all other application classes.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DBHelper.createInstance(getApplicationContext());
    }
}
