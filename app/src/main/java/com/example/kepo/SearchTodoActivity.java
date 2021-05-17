package com.example.kepo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kepo.adapter.TodoAdapter;
import com.example.kepo.adapter.UserAdapter;
import com.example.kepo.databinding.ActivitySearchTodoBinding;
import com.example.kepo.model.Todo;
import com.example.kepo.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchTodoActivity extends AppCompatActivity implements TodoAdapter.OnTodoListener {

    private ActivitySearchTodoBinding binding;
    private TodoAdapter todoAdapter;
    private ArrayList<Todo> result;
    private String BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_todo);
        getSupportActionBar().hide();
        initAdapter();
        BASE_URL = "https://it-division-kepo.herokuapp.com/searchTodos";
        result = new ArrayList<>();
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        binding.btnSearch.setOnClickListener(v -> {
            if(confirmSearch()){
                loadData();
            }
        });
    }

    private boolean confirmSearch(){
        if(binding.editSearchTodo.getText().length()==0){
            Toast.makeText(this, "Text field cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!binding.checkBoxTodo.isChecked()&&!binding.checkBoxUser.isChecked()){
            makeToast();
            return false;
        }
        else{
            return true;
        }
    }

    private void makeToast() {
        String message = "You must search either by user, todo, or both";
        Spannable centered = new SpannableString(message);
        centered.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),0, message.length() - 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        Toast toast = Toast.makeText(this, centered, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void initAdapter(){
        todoAdapter = new TodoAdapter(this, this, 2);
        binding.rvTodo.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTodo.setAdapter(todoAdapter);
    }

    private void loadData(){
        result.clear();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL,
                requestBody(),
                response -> {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    try {
                        JSONArray data = response.getJSONArray("data");
                        binding.txtResult.setText("Result for \""+binding.editSearchTodo.getText()+"\"");
                        if(data.length()==0) {
                            binding.rvTodo.setVisibility(View.INVISIBLE);
                            binding.txtNoData.setVisibility(View.VISIBLE);
                            return;
                        }
                        binding.rvTodo.setVisibility(View.VISIBLE);
                        binding.txtNoData.setVisibility(View.INVISIBLE);
                        for (int i=0;i<data.length();i++) {
                            JSONObject jsonObject = data.getJSONObject(i);
                            Todo todo = new Todo();
                            User user = new User();
                            todo.setId(jsonObject.getString("todo_id"));
                            todo.setTitle(jsonObject.getString("title"));
                            todo.setDate(formatDate(jsonObject.getString("last_edited")));
                            user.setId(jsonObject.getString("user_id"));
                            user.setName(jsonObject.getString("username"));
                            todo.setUser(user);
                            result.add(todo);
                        }
                        todoAdapter.updateData(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        );
        RequestQueue queue = Volley.newRequestQueue( this);
        queue.add(jsonObjectRequest);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private String formatDate(String string) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = format.parse(string);
        format.applyPattern("dd MMM yyyy HH:mm");
        return format.format(date);
    }

    private JSONObject requestBody(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("searchQuery", binding.editSearchTodo.getText());
            jsonObject.put("filterUser", binding.checkBoxUser.isChecked() ? 1 : 0);
            jsonObject.put("filterTodo", binding.checkBoxTodo.isChecked() ? 1 : 0);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onTodoClick(int position) {
        result.get(position);
        Intent intent = new Intent(this, TodoDetailActivity.class);
        intent.putExtra("user_id",result.get(position).getUser().getId());
        intent.putExtra("todo_id",result.get(position).getId());
        intent.putExtra("editable",false);
        startActivity(intent);
    }

}