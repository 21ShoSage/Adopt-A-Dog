package com.dogadoption.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dogadoption.app.R;
import com.dogadoption.app.models.Dog;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class DogAdapter extends RecyclerView.Adapter<DogAdapter.DogViewHolder> {

    public interface OnDogClickListener {
        void onDogClick(Dog dog);
    }

    private final Context context;
    private List<Dog> dogs = new ArrayList<>();
    private final OnDogClickListener listener;

    public DogAdapter(Context context, OnDogClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dog_card, parent, false);
        return new DogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        Dog dog = dogs.get(position);

        holder.tvName.setText(dog.getName());
        holder.tvBreed.setText(dog.getBreed() != null ? dog.getBreed() : "Unknown");
        holder.tvAge.setText(dog.getAge() > 0 ? dog.getAge() + " yr" : "?");

        if (dog.isAdopted()) {
            holder.chipStatus.setText("Adopted");
            holder.chipStatus.setChipBackgroundColorResource(R.color.status_adopted);
        } else {
            holder.chipStatus.setText("Available");
            holder.chipStatus.setChipBackgroundColorResource(R.color.status_available);
        }

        if (dog.getPhotoPath() != null && !dog.getPhotoPath().isEmpty()) {
            Glide.with(context).load(dog.getPhotoPath()).centerCrop()
                    .placeholder(R.drawable.ic_dog_placeholder)
                    .error(R.drawable.ic_dog_placeholder)
                    .into(holder.ivDogPhoto);
        } else {
            holder.ivDogPhoto.setImageResource(R.drawable.ic_dog_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onDogClick(dog));
    }

    @Override
    public int getItemCount() {
        return dogs.size();
    }

    static class DogViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDogPhoto;
        TextView tvName, tvBreed, tvAge;
        Chip chipStatus;

        DogViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDogPhoto = itemView.findViewById(R.id.iv_dog_photo);
            tvName = itemView.findViewById(R.id.tv_dog_name);
            tvBreed = itemView.findViewById(R.id.tv_dog_breed);
            tvAge = itemView.findViewById(R.id.tv_dog_age);
            chipStatus = itemView.findViewById(R.id.chip_status);
        }
    }
}
