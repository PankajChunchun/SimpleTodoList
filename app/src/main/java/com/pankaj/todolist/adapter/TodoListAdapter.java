package com.pankaj.todolist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pankaj.todolist.R;
import com.pankaj.todolist.TodoActivity;
import com.pankaj.todolist.bean.TodoItem;

import java.util.List;

/**
 * RecyclerView's Adapter which shows list of to-do items.
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    private List<TodoItem> todos;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView;
        TextView descriptionTextView;
        private TodoItem todoItem;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.todo_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.todo_description);
            itemView.setOnClickListener(this);
        }

        public void bind(TodoItem todoItem) {
            this.todoItem = todoItem;
            titleTextView.setText(todoItem.getTitle());
            descriptionTextView.setText(todoItem.getDescription());
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            context.startActivity(TodoActivity.getTodoIntent(context, todoItem));
        }
    }

    public TodoListAdapter(List<TodoItem> todos) {
        this.todos = todos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_row_view, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(todos.get(position));
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void updateList(List<TodoItem> todos) {
        // Allow recyclerview animations to complete normally if we already know about data changes
        if (todos.size() != this.todos.size() || !this.todos.containsAll(todos)) {
            this.todos = todos;
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        todos.remove(position);
        notifyItemRemoved(position);
    }

    public TodoItem getItem(int position) {
        return todos.get(position);
    }
}