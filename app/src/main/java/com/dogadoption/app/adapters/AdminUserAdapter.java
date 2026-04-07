package com.dogadoption.app.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dogadoption.app.R;
import com.dogadoption.app.models.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    public interface OnDeleteListener { void onDelete(User user); }

    private final Context context;
    private List<User> users = new ArrayList<>();
    private final OnDeleteListener deleteListener;

    public AdminUserAdapter(Context context, OnDeleteListener listener) {
        this.context = context;
        this.deleteListener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_admin_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        User user = users.get(position);
        h.tvName.setText(user.getName());
        h.tvEmail.setText(user.getEmail());
        h.tvPhone.setText(user.getPhone() != null && !user.getPhone().isEmpty()
                ? user.getPhone() : "No phone");
        h.tvAddress.setText(user.getAddress() != null && !user.getAddress().isEmpty()
                ? user.getAddress() : "No address");

        if (user.getPhotoPath() != null && !user.getPhotoPath().isEmpty()) {
            Glide.with(context).load(Uri.parse(user.getPhotoPath())).circleCrop()
                    .placeholder(R.drawable.ic_person_placeholder)
                    .error(R.drawable.ic_person_placeholder).into(h.ivPhoto);
        } else {
            h.ivPhoto.setImageResource(R.drawable.ic_person_placeholder);
        }

        h.btnDelete.setOnClickListener(v -> deleteListener.onDelete(user));
    }

    @Override public int getItemCount() { return users.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivPhoto;
        TextView tvName, tvEmail, tvPhone, tvAddress;
        ImageButton btnDelete;
        ViewHolder(@NonNull View v) {
            super(v);
            ivPhoto   = v.findViewById(R.id.iv_user_photo);
            tvName    = v.findViewById(R.id.tv_user_name);
            tvEmail   = v.findViewById(R.id.tv_user_email);
            tvPhone   = v.findViewById(R.id.tv_user_phone);
            tvAddress = v.findViewById(R.id.tv_user_address);
            btnDelete = v.findViewById(R.id.btn_delete_user);
        }
    }
}
