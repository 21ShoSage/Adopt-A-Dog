package com.dogadoption.app.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dogadoption.app.R;
import com.dogadoption.app.models.AdoptionRecord;

import java.util.List;

public class AdminAdoptionAdapter extends RecyclerView.Adapter<AdminAdoptionAdapter.ViewHolder> {

    private final Context context;
    private final List<AdoptionRecord> records;

    public AdminAdoptionAdapter(Context context, List<AdoptionRecord> records) {
        this.context = context;
        this.records = records;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_admin_adoption, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AdoptionRecord r = records.get(position);
        h.tvDogName.setText(r.getDogName() != null ? r.getDogName() : "Unknown Dog");
        h.tvDogBreed.setText(r.getDogBreed() != null ? r.getDogBreed() : "Unknown Breed");
        h.tvAdopterName.setText("Adopted by: " + (r.getAdopterName() != null ? r.getAdopterName() : "—"));
        h.tvAdopterEmail.setText(r.getAdopterEmail() != null ? r.getAdopterEmail() : "—");
        h.tvDate.setText("Date: " + (r.getAdoptionDate() != null ? r.getAdoptionDate() : "—"));

        if (r.getDogPhotoPath() != null && !r.getDogPhotoPath().isEmpty()) {
            Glide.with(context).load(Uri.parse(r.getDogPhotoPath())).centerCrop()
                    .placeholder(R.drawable.ic_dog_placeholder)
                    .error(R.drawable.ic_dog_placeholder).into(h.ivDogPhoto);
        } else {
            h.ivDogPhoto.setImageResource(R.drawable.ic_dog_placeholder);
        }
    }

    @Override public int getItemCount() { return records.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView ivDogPhoto;
        TextView tvDogName, tvDogBreed, tvAdopterName, tvAdopterEmail, tvDate;
        ViewHolder(@NonNull View v) {
            super(v);
            ivDogPhoto     = v.findViewById(R.id.iv_dog_photo);
            tvDogName      = v.findViewById(R.id.tv_dog_name);
            tvDogBreed     = v.findViewById(R.id.tv_dog_breed);
            tvAdopterName  = v.findViewById(R.id.tv_adopter_name);
            tvAdopterEmail = v.findViewById(R.id.tv_adopter_email);
            tvDate         = v.findViewById(R.id.tv_adoption_date);
        }
    }
}
