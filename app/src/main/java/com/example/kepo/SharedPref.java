package com.example.kepo;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kepo.model.User;

public class SharedPref {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SharedPref(Context context){
        pref = context.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void save(String id, String username, String name) {
        editor.putString("id", id);
        editor.putString("username", username);
        editor.putString("name",name);
        editor.apply();
    }

    public User getUser(){
        User user = new User();
        user.setUsername(pref.getString("username",""));
        user.setPassword(pref.getString("password",""));
        return user;
    }

    public String getId(){
        return pref.getString("id","");
    }

    public String getUsername(){
        return pref.getString("username","");
    }

    public String getName(){
        return pref.getString("name","");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

}
