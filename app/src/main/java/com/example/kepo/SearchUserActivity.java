package com.example.kepo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kepo.adapter.TodoAdapter;
import com.example.kepo.adapter.UserAdapter;
import com.example.kepo.databinding.ActivityMyTodoBinding;
import com.example.kepo.databinding.ActivitySearchUserBinding;
import com.example.kepo.model.Todo;
import com.example.kepo.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity implements UserAdapter.OnUserListener {

    private UserAdapter userAdapter;
    private ArrayList<User> result;
    private SharedPref pref;
    private final String BASE_URL = "https://it-division-kepo.herokuapp.com/searchUser";
    private ActivitySearchUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_user);
        getSupportActionBar().hide();
        pref = new SharedPref(this);
        result = new ArrayList<>();
        initAdapter();
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        binding.btnSearch.setOnClickListener(v -> {
            if(binding.editSearchUser.getText().length()==0||binding.editSearchUser.getText().equals("")){
                Toast.makeText(this, "Text field cannot be empty", Toast.LENGTH_SHORT).show();
            }
            else {
                loadData();
            }
        });

    }

    private void initAdapter(){
        userAdapter = new UserAdapter(this,this);
        binding.rvUser.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUser.setAdapter(userAdapter);
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
                        binding.txtResult.setText("Result for \""+binding.editSearchUser.getText()+"\"");
                        if(data.length()==0) {
                            binding.rvUser.setVisibility(View.INVISIBLE);
                            binding.txtNoData.setVisibility(View.VISIBLE);
                            return;
                        }
                        binding.rvUser.setVisibility(View.VISIBLE);
                        binding.txtNoData.setVisibility(View.INVISIBLE);
                        for (int i=0;i<data.length();i++) {
                            JSONObject jsonObject = data.getJSONObject(i);
                            User user = new User();
                            user.setId(jsonObject.getString("user_id"));
                            user.setUsername(jsonObject.getString("username"));
                            user.setName(jsonObject.getString("name"));
                            result.add(user);
                        }
                        userAdapter.updateData(result);
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

    private JSONObject requestBody(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", pref.getId());
            jsonObject.put("searchQuery", binding.editSearchUser.getText());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onUserClick(int position) {
        result.get(position);
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("user_id", result.get(position).getId());
        intent.putExtra("username",result.get(position).getUsername());
        intent.putExtra("name", result.get(position).getName());
        startActivity(intent);
    }
}