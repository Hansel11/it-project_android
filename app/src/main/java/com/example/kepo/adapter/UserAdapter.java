package com.example.kepo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kepo.databinding.UserItemLayoutBinding;
import com.example.kepo.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final Context context;
    private ArrayList<User> users;
    private UserAdapter.OnUserListener onUserListener;

    public UserAdapter(Context context, UserAdapter.OnUserListener onUserListener){
        this.users = new ArrayList<>();
        this.context = context;
        this.onUserListener = onUserListener;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        UserItemLayoutBinding binding = UserItemLayoutBinding.inflate(layoutInflater, parent, false);
        return new UserAdapter.UserViewHolder(binding, onUserListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.binding.setUser(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateData(ArrayList<User> newUsers){
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private UserItemLayoutBinding binding;
        UserAdapter.OnUserListener onUserListener;

        public UserViewHolder(@NonNull UserItemLayoutBinding binding, UserAdapter.OnUserListener onUserListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onUserListener = onUserListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onUserListener.onUserClick(getAdapterPosition());
        }
    }

    public interface OnUserListener{
        void onUserClick(int position);
    }
    
}
