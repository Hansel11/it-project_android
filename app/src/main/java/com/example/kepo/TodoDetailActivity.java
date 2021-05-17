package com.example.kepo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kepo.databinding.ActivityTodoDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoDetailActivity extends AppCompatActivity {

    private ActivityTodoDetailBinding binding;
    private SharedPref pref;
    private String todoTitle;
    private String todoDesc;
    private String BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_detail);
        pref = new SharedPref(this);
        getSupportActionBar().hide();
        BASE_URL = "https://it-division-kepo.herokuapp.com/user/"+getIntent().getExtras().getString("user_id")+"/todo/"+getIntent().getExtras().getString("todo_id");

        if(!getIntent().getExtras().getBoolean("editable")){
            binding.btnEdit.hide();
        }

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        loadDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDetail();
    }

    private void loadDetail(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    try {
                        JSONObject data = response.getJSONObject("data");
                        todoTitle = data.getString("title");
                        todoDesc = data.getString("description");
                        binding.txtTitle.setText(todoTitle);
                        binding.txtDesc.setText(todoDesc);
                        binding.txtDate.setText(formatDate(data.getString("last_edited")));
                        binding.btnEdit.setOnClickListener(v -> {
                            Intent intent = new Intent(this, InsertUpdateTodoActivity.class);
                            intent.putExtra("title", "Update Todo");
                            intent.putExtra("todo_id", getIntent().getExtras().getString("todo_id"));
                            intent.putExtra("todo", todoTitle);
                            intent.putExtra("desc", todoDesc);
                            startActivity(intent);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private String formatDate(String string) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = format.parse(string);
        format.applyPattern("dd MMM yyyy HH:mm");
        return format.format(date);
    }

}