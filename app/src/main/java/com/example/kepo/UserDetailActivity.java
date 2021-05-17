package com.example.kepo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kepo.adapter.TodoAdapter;
import com.example.kepo.databinding.ActivityUserDetailBinding;
import com.example.kepo.model.Todo;
import com.example.kepo.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserDetailActivity extends AppCompatActivity implements TodoAdapter.OnTodoListener {

    private ActivityUserDetailBinding binding;
    private TodoAdapter todoAdapter;
    private String BASE_URL;
    private ArrayList<Todo> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail);
        getSupportActionBar().hide();
        binding.txtUsername.setText(getIntent().getExtras().getString("username"));
        binding.txtName.setText(getIntent().getExtras().getString("name"));
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        result = new ArrayList<>();
        BASE_URL = "https://it-division-kepo.herokuapp.com/user/"+getIntent().getExtras().getString("user_id")+"/todo";
        initAdapter();
        loadData();
    }

    private void initAdapter(){
        todoAdapter = new TodoAdapter(this,this, 2);
        binding.rvTodo.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTodo.setAdapter(todoAdapter);
    }

    private void loadData(){
        result.clear();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    try {
                        JSONArray data = response.getJSONObject("data").getJSONArray("listTodo");
                        if(data.length()==0) {
                            binding.rvTodo.setVisibility(View.INVISIBLE);
                            binding.txtNoData.setVisibility(View.VISIBLE);
                            return;
                        }
                        binding.rvTodo.setVisibility(View.VISIBLE);
                        binding.txtNoData.setVisibility(View.INVISIBLE);
                        binding.txtTodo.setText("Todos : "+data.length());
                        for (int i=0;i<data.length();i++) {
                            JSONObject jsonObject = data.getJSONObject(i);
                            Todo todo = new Todo();
                            User user = new User();
                            todo.setId(jsonObject.getString("todo_id"));
                            todo.setTitle(jsonObject.getString("title"));
                            todo.setDate(formatDate(jsonObject.getString("last_edited")));
                            user.setName(getIntent().getExtras().getString("name"));
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

    @Override
    public void onTodoClick(int position) {
        result.get(position);
        Intent intent = new Intent(this, TodoDetailActivity.class);
        intent.putExtra("user_id",getIntent().getExtras().getString("user_id"));
        intent.putExtra("todo_id",result.get(position).getId());
        intent.putExtra("editable",false);
        startActivity(intent);
    }
}