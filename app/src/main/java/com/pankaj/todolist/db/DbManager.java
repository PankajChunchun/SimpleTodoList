package com.pankaj.todolist.db;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pankaj.todolist.bean.TodoItem;

/**
 * Database manager class which is being used to store to-do data
 * In Current to-do application is using Firebase realtime database
 * to store to-do item, which also supports offline data.
 */
public class DbManager {
    public static final String TODO_DATA = "todo";

    public static void addOrUpdateTodo(TodoItem todoItem) {
        // Get logged-in user to add to-do for that user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        if (todoItem.getUid() == null || TextUtils.isEmpty(todoItem.getUid().trim())) {
            // New TO-DO to be added
            todoItem.setUid(db.child(currentUser.getUid()).push().getKey());
            todoItem.setCompleted(false);
        }

        // db.child(TODO_DATA).child(todoItem.getUid()).setValue(todoItem);
        // Add to-do for particular user
        db.child(currentUser.getUid()).child(todoItem.getUid()).setValue(todoItem);
    }

    public static void removeTodo(TodoItem todoItem) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        // db.child(TODO_DATA).child(todoItem.getUid()).removeValue();
        // Add to-do for particular user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db.child(currentUser.getUid()).child(todoItem.getUid()).removeValue();
        }
    }
}
