package com.pankaj.todolist;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class TodoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
