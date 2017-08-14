package com.pankaj.todolist;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pankaj.todolist.adapter.TodoListAdapter;
import com.pankaj.todolist.bean.TodoItem;
import com.pankaj.todolist.db.DbManager;
import com.pankaj.todolist.listeners.DeletionListener;
import com.pankaj.todolist.listeners.TodoItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An AppCompatActivity which is being used to show TO-DO list
 * which also supports swipe to delete feature.
 * <p>
 * User can mark to-do as done, instead-of deleting a to-do.
 */
public class TodoListActivity extends AppCompatActivity implements DeletionListener {

    private DatabaseReference mDbRef;
    private TodoListAdapter mAdapter;
    private CoordinatorLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_item_list_layout);

        mRootView = (CoordinatorLayout) findViewById(R.id.root);
        mDbRef = FirebaseDatabase.getInstance().getReference();
        mAdapter = new TodoListAdapter(Collections.<TodoItem>emptyList());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.todo_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        final int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = offset;
                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new TodoItemTouchHelperCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, TodoListActivity.this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_new);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(TodoActivity.getTodoIntent(view.getContext(), null));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get logged-in user to get list of to-dos added for that user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        mDbRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<TodoItem> todos = new ArrayList<>();
                for (DataSnapshot todoDataSnapshot : dataSnapshot.getChildren()) {
                    TodoItem todoItem = todoDataSnapshot.getValue(TodoItem.class);
                    todos.add(todoItem);
                }

                mAdapter.updateList(todos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void itemRemoved(int position) {
        final TodoItem todoItem = mAdapter.getItem(position);
        mAdapter.removeItem(position);
        DbManager.removeTodo(todoItem);
        Snackbar.make(mRootView, todoItem.getTitle().trim() + " is deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(mRootView, todoItem.getTitle().trim() + " is restored!", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}