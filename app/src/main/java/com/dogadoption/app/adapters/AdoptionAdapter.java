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
import com.dogadoption.app.models.AdoptionRecord;

import java.util.List;

public class AdoptionAdapter extends RecyclerView.Adapter<AdoptionAdapter.AdoptionViewHolder> {

    private final Context context;
    private final List<AdoptionRecord> records;

    public AdoptionAdapter(Context context, List<AdoptionRecord> records) {
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public AdoptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_adoption_record, parent, false);
        return new AdoptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdoptionViewHolder holder, int position) {
        AdoptionRecord record = records.get(position);

        holder.tvDogName.setText(record.getDogName() != null ? record.getDogName() : "Unknown Dog");
        holder.tvDogBreed.setText(record.getDogBreed() != null ? record.getDogBreed() : "Unknown Breed");
        holder.tvAdoptionDate.setText("Adopted on: " + (record.getAdoptionDate() != null ? record.getAdoptionDate() : "N/A"));
        holder.tvNotes.setText(record.getNotes() != null ? record.getNotes() : "");

        if (record.getDogPhotoPath() != null && !record.getDogPhotoPath().isEmpty()) {
            Glide.with(context).load(record.getDogPhotoPath()).centerCrop()
                    .placeholder(R.drawable.ic_dog_placeholder)
                    .error(R.drawable.ic_dog_placeholder)
                    .into(holder.ivDogPhoto);
        } else {
            holder.ivDogPhoto.setImageResource(R.drawable.ic_dog_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class AdoptionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDogPhoto;
        TextView tvDogName, tvDogBreed, tvAdoptionDate, tvNotes;

        AdoptionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDogPhoto = itemView.findViewById(R.id.iv_dog_photo);
            tvDogName = itemView.findViewById(R.id.tv_dog_name);
            tvDogBreed = itemView.findViewById(R.id.tv_dog_breed);
            tvAdoptionDate = itemView.findViewById(R.id.tv_adoption_date);
            tvNotes = itemView.findViewById(R.id.tv_notes);
        }
    }
}
