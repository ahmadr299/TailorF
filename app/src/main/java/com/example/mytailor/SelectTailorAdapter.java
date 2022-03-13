package com.example.mytailor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SelectTailorAdapter extends RecyclerView.Adapter<SelectTailorViewHolder> {
    List<SelectTailorModelClass> selectTailorList=new ArrayList<>();
    Context context;
    LayoutInflater layoutInflater;
    StorageReference storageReference;
    public SelectTailorAdapter(List<SelectTailorModelClass> selectTailorList, Context context) {
        this.selectTailorList = selectTailorList;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
        storageReference= FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public SelectTailorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.select_tailor_custom,null);
        return new SelectTailorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectTailorViewHolder holder, int position) {
        holder.shopName.setText(selectTailorList.get(position).getShopName());
        holder.phone.setText("Contact: "+selectTailorList.get(position).getPhone());
        holder.email.setText("Name: "+selectTailorList.get(position).getOwnerName());
        holder.address.setText("Address: "+selectTailorList.get(position).getAddress());
        holder.categories.setText("Category: "+selectTailorList.get(position).getCategories());
        holder.tailorRating.setText("Rating: "+selectTailorList.get(position).getRating());
        Log.e("Owner Name in Adapter",selectTailorList.get(position).getOwnerName());
        StorageReference storageReference1=storageReference.child("TailorsProfile/"+selectTailorList.get(position).getOwnerName()+".jpg");
        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri!=null)
                {
                    Glide.with(context).load(uri).placeholder(R.drawable.personimage).into(holder.shopimage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=context.getSharedPreferences("Shop",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("ShopName",selectTailorList.get(position).getShopName());
                editor.putString("OwnerName",selectTailorList.get(position).getOwnerName());
                editor.commit();
                context.startActivity(new Intent(context,Select_Category_Activity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectTailorList.size();
    }

    public void filterList(List<SelectTailorModelClass> list)
    {
        selectTailorList=list;
        notifyDataSetChanged();
    }
}
