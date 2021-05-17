package com.example.kepo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kepo.MyTodoActivity;
import com.example.kepo.R;
import com.example.kepo.SharedPref;
import com.example.kepo.databinding.TodoItemLayoutBinding;
import com.example.kepo.databinding.UserTodoLayoutBinding;
import com.example.kepo.model.Todo;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private ArrayList<Todo> todos;
    private ArrayList<Todo> checked;
    private OnTodoListener onTodoListener;
    private int layoutType;
    private Snackbar snackbar;
    private SharedPref pref;
    private String BASE_URL;
    private MyTodoActivity activity;

    public TodoAdapter(Context context, OnTodoListener onTodoListener, int layoutType){
        this.layoutType = layoutType;
        this.todos = new ArrayList<>();
        this.checked = new ArrayList<>();
        this.context = context;
        this.onTodoListener = onTodoListener;
        this.pref = new SharedPref(context);
        this.BASE_URL = "https://it-division-kepo.herokuapp.com/user/"+pref.getId()+"/deleteTodo";
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TodoItemLayoutBinding binding1 = TodoItemLayoutBinding.inflate(layoutInflater, parent, false);
        UserTodoLayoutBinding binding2 = UserTodoLayoutBinding.inflate(layoutInflater, parent, false);
        switch (layoutType){
            case 1:
                return new TodoViewHolder(binding1, onTodoListener);
            case 2:
                return new UserTodoViewHolder(binding2, onTodoListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Todo todo = todos.get(position);
        switch (layoutType){
            case 1:
                TodoViewHolder todoViewHolder = (TodoViewHolder)holder;
                todoViewHolder.binding.setTodo(todo);
                todoViewHolder.binding.checkBoxTodo.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked){
                        checked.add(todo);
                    }
                    else {
                        checked.remove(todo);
                    }
                    if(checked.size()>0){
                        snackbar = Snackbar.make(todoViewHolder.binding.getRoot(),checked.size() + " item(s)", Snackbar.LENGTH_INDEFINITE)
                                .setAction("DELETE", v -> {
                                    confirmDelete();
                                }).setActionTextColor(Color.RED).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                        snackbar.show();
                    }
                    else {
                        snackbar.dismiss();
                    }
                });
                break;
            case 2:
                UserTodoViewHolder userTodoViewHolder = (UserTodoViewHolder)holder;
                userTodoViewHolder.binding.setTodo(todo);
                userTodoViewHolder.binding.setName(todo.getUser().getName());
                break;
        }
    }

    private void confirmDelete(){
        new AlertDialog.Builder(context)
                .setTitle("Delete Todo")
                .setMessage("Are you sure you want to delete all these todos?")
                .setPositiveButton("YES", (dialog, which) -> {
                    deleteTodo();
                })
                .setNegativeButton("NO", ((dialog, which) -> {
                    snackbar.show();
                }))
                .show();
    }

    private void deleteTodo(){
        activity = (MyTodoActivity)context;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL,
                deleteRequest(),
                response -> {
                    try {
                        Toast.makeText(context, ""+response.getString("message"), Toast.LENGTH_SHORT).show();
//                        updateData(todos);
                        activity.reloadActivity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }

        );
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
        activity.binding.progressBar.setVisibility(View.VISIBLE);
    }

    private JSONObject deleteRequest(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Todo item:checked) {
            jsonArray.put(item.getId());
        }
        try {
            jsonObject.put("todos",jsonArray);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void updateData(ArrayList<Todo> newTodos){
        todos.clear();
        todos.addAll(newTodos);
        notifyDataSetChanged();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TodoItemLayoutBinding binding;
        OnTodoListener onTodoListener;

        public TodoViewHolder(@NonNull TodoItemLayoutBinding binding, OnTodoListener onTodoListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onTodoListener = onTodoListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTodoListener.onTodoClick(getAdapterPosition());
        }
    }

    class UserTodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private UserTodoLayoutBinding binding;
        OnTodoListener onTodoListener;

        public UserTodoViewHolder(@NonNull UserTodoLayoutBinding binding, OnTodoListener onTodoListener){
            super(binding.getRoot());
            this.binding = binding;
            this.onTodoListener = onTodoListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTodoListener.onTodoClick(getAdapterPosition());
        }
    }

    public interface OnTodoListener{
        void onTodoClick(int position);
    }

}
