package com.pankaj.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.pankaj.todolist.bean.TodoItem;
import com.pankaj.todolist.db.DbManager;


/**
 * An {@link android.app.Activity}, which is being used to
 * add or edit a todo-item.
 *
 * If a todo-item to be edited, pass that item from calling
 * activity in intent {@link TodoActivity#EXTRA_TODO},
 * or call {@link TodoActivity#getTodoIntent(Context, TodoItem)}.
 */
public class TodoActivity extends AppCompatActivity {

    private static final String EXTRA_TODO = "todo_item";
    private TextView mTodoTitleTv;
    private TextView mTodoDescription;
    private TodoItem mTodoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_item_layout);

        mTodoTitleTv = (TextView) findViewById(R.id.todo_title);
        mTodoDescription = (TextView) findViewById(R.id.todo_description);

        mTodoItem = getIntent().getParcelableExtra(EXTRA_TODO);
        if (mTodoItem != null) {
            mTodoTitleTv.setText(mTodoItem.getTitle());
            mTodoDescription.setText(mTodoItem.getDescription());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_edit_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTodoItem == null) {
                    mTodoItem = new TodoItem();
                }
                mTodoItem.setTitle(mTodoTitleTv.getText().toString());
                mTodoItem.setDescription(mTodoDescription.getText().toString());

                DbManager.addOrUpdateTodo(mTodoItem);
                finish();
            }
        });
    }

    /**
     * Get TodoActivity's intent to launch this activity.
     * @param context
     * @param todoItem
     * @return
     */
    public static Intent getTodoIntent(Context context, TodoItem todoItem) {
        Intent intent = new Intent(context, TodoActivity.class);
        if (todoItem != null) {
            intent.putExtra(EXTRA_TODO, todoItem);
        }

        return intent;
    }
}