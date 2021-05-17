package com.example.kepo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kepo.databinding.ActivityInsertUpdateTodoBinding;
import com.example.kepo.databinding.ActivityTodoDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class InsertUpdateTodoActivity extends AppCompatActivity {

    private ActivityInsertUpdateTodoBinding binding;
    private SharedPref pref;
    private int maxChar = 100;
    private String menuTitle;
    private String BASE_URL;
    private int method;
    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @SuppressLint("ResourceAsColor")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            binding.txtCounter.setText(String.valueOf(s.length())+"/"+maxChar);
            if(s.length()>=maxChar){
                binding.txtCounter.setTextColor(getResources().getColor(R.color.design_default_color_error));
            }else{
                binding.txtCounter.setTextColor(R.color.material_on_background_disabled);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_insert_update_todo);
        getSupportActionBar().hide();
        pref = new SharedPref(this);
        menuTitle = getIntent().getExtras().getString("title");

        if(menuTitle.equals("Update Todo")){
            BASE_URL = "https://it-division-kepo.herokuapp.com/user/"+pref.getId()+"/todo/"+getIntent().getExtras().getString("todo_id");
            method = Request.Method.PUT;
        }else {
            BASE_URL = "https://it-division-kepo.herokuapp.com/user/"+pref.getId()+"/todo";
            method = Request.Method.POST;
        }

        binding.txtTitle.setText(menuTitle);
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        binding.editDesc.addTextChangedListener(watcher);
        if(menuTitle.equals("Update Todo")){
            binding.editName.setText(getIntent().getExtras().getString("todo"));
            binding.editDesc.setText(getIntent().getExtras().getString("desc"));
        }
        binding.btnConfirm.setOnClickListener(v -> {
            if(confirmEdit()){
                putRequest();
            }
        });
    }

    private void putRequest(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                BASE_URL,
                request(),
                response -> {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    try {
                        String message = response.getString("message");
                        if(message.equals("Update todo success")||message.equals("Todo created successfully")){
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            somethingWrong();
                        }
                    } catch (JSONException e) {
                        somethingWrong();
                    }
                },
                error -> {
                    somethingWrong();
                    error.printStackTrace();
                }

        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private JSONObject request(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", binding.editName.getText());
            jsonObject.put("description", binding.editDesc.getText());
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    private boolean confirmEdit(){
        if(binding.editName.length()==0||binding.editDesc.length()==0){
            binding.txtError.setText("Text Fields cannot be empty");
            binding.txtError.setVisibility(View.VISIBLE);
            return false;
        }
        else if(binding.editDesc.length()>=maxChar){
            binding.txtError.setText("Your Description exceeded the maximum words");
            binding.txtError.setVisibility(View.VISIBLE);
            return false;
        }
        else return true;
    }

    private void somethingWrong(){
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.txtError.setText("Something wrong occurred");
        binding.txtError.setVisibility(View.VISIBLE);
    }

}