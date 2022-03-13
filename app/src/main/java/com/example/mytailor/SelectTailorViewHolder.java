package com.example.mytailor;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectTailorViewHolder extends RecyclerView.ViewHolder {
    TextView shopName,phone,email,categories,address,tailorRating;
    ImageView shopimage;
    public SelectTailorViewHolder(@NonNull View itemView) {
        super(itemView);
        shopName=itemView.findViewById(R.id.shopName);
        phone=itemView.findViewById(R.id.phone);
        email=itemView.findViewById(R.id.email);
        categories=itemView.findViewById(R.id.categories);
        address=itemView.findViewById(R.id.tailoraddress);
        shopimage=itemView.findViewById(R.id.shopimage);
        tailorRating=itemView.findViewById(R.id.tailorRating);
    }
}
