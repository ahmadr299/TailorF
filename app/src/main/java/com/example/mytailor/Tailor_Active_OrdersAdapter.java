package com.example.mytailor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class Tailor_Active_OrdersAdapter extends RecyclerView.Adapter<Tailors_Active_OrdersViewHolder> {
    private List<Tailor_new_ordersModelClass> list = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    StorageReference storageReference;
    private Uri Image;
    private String Activity;

    public Tailor_Active_OrdersAdapter(List<Tailor_new_ordersModelClass> list, Context context, String Activity) {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        storageReference = FirebaseStorage.getInstance().getReference();
        this.Activity = Activity;
    }

    @NonNull
    @Override
    public Tailors_Active_OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.tailor_new_order_custom, null);
        return new Tailors_Active_OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Tailors_Active_OrdersViewHolder holder, int position) {
        holder.customerName.setText(list.get(position).getCustomerName());
        holder.category.setText("Category: " + list.get(position).getCategory());
        holder.duedate.setText("Date: " + list.get(position).getDate());
        holder.measurements.setText(list.get(position).getMeasurements());
        holder.orderNo.setText("Order# " + list.get(position).getOrderNo());
        holder.phone.setText("Contact: " + list.get(position).getPhone());
        holder.address.setText("Address: " + list.get(position).getAddress());
        holder.acceptbtn.setVisibility(View.GONE);
        holder.rejectbtn.setVisibility(View.GONE);
        StorageReference storageReference1 = storageReference.child("Orders/" + list.get(position).getOrderNo() + "/Pic1.jpg");
        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    Image = uri;
                    Glide.with(context).load(uri).placeholder(R.drawable.personimage).into(holder.image1);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Activity.equals("Customer Complete Order")) {
                    Intent intent = new Intent(context, Tailor_Rate_Activity.class);
                    intent.putExtra("ShopName", holder.customerName.getText().toString());
                    context.startActivity(intent);
                    ((android.app.Activity) context).finish();
                }
                if (Activity.equals("Tailor Active Order")) {
                    StorageReference storageReference1 = storageReference.child("Orders/" + list.get(position).getOrderNo() + "/Pic1.jpg");
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                Intent intent = new Intent(context, Tailor_Active_Order_Details_Activity.class);
                                intent.putExtra("Order", list.get(position).getOrderNo());
                                intent.putExtra("CustomerName", list.get(position).getCustomerName());
                                intent.putExtra("Phone", list.get(position).getPhone());
                                intent.putExtra("Address", list.get(position).getAddress());
                                intent.putExtra("Category", list.get(position).getCategory());
                                intent.putExtra("Date", list.get(position).getDate());
                                intent.putExtra("ImageUri", Image.toString());
                                intent.putExtra("Measurements", list.get(position).getMeasurements());
                                context.startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
