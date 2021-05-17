package com.example.kepo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kepo.databinding.ActivityLoginBinding;
import com.example.kepo.databinding.BottomDialogBinding;
import com.example.kepo.model.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SharedPref pref;
    private static final String BASE_URL = "https://it-division-kepo.herokuapp.com/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        getSupportActionBar().hide();
        binding.setUser(new User());
        binding.btnLogin.setOnClickListener(v -> {
            User user = binding.getUser();
            login();
        });
        pref = new SharedPref(this);
    }

    private void login() {
        User user = binding.getUser();
        if (user.getUsername() == null || user.getPassword() == null) {
            errorMessage("Please input username and password");
        }
        else {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL,
                    getLoginBody(user),
                    response -> {
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        try {
                            String message = response.getString("message");
                            if(message.equals("Login success")){
                                String id = response.getJSONObject("data").getString("user_id");
                                String name = response.getJSONObject("data").getString("name");
                                pref.save(id, user.getUsername(), name);
                                Intent intent = new Intent(this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                errorMessage(message);
                            }
                        } catch (Exception e) {
                            errorMessage("Something wrong occurred while logging in");
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
    }

    private JSONObject getLoginBody(User user){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username",user.getUsername());
            jsonObject.put("password", user.getPassword());
            return jsonObject;
        } catch (Exception e){
            return null;
        }
    }

    private void errorMessage(String message){
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog,null);
        TextView tvMessage = view.findViewById(R.id.txtMessage);
        Button btnClose = view.findViewById(R.id.btnDismiss);
        tvMessage.setText(message);
        dialog.setContentView(view);
        dialog.show();
        btnClose.setOnClickListener(v -> dialog.hide());
    }
}