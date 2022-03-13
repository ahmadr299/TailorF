package com.example.mytailor;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Tailor_new_ordersAdapter extends RecyclerView.Adapter<Tailor_new_ordersViewHolder> {
    List<Tailor_new_ordersModelClass> list=new ArrayList<>();
    LayoutInflater layoutInflater;
    Context context;
    List<Uri> ImagesList=new ArrayList<>();
    StorageReference storageReference;
    FirebaseFirestore firestore;
    String documentId;
    CallBackFragment callBackFragment;
    public Tailor_new_ordersAdapter(List<Tailor_new_ordersModelClass> list, Context context, CallBackFragment callBackFragment) {
        this.list = list;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
        storageReference= FirebaseStorage.getInstance().getReference();
        firestore=FirebaseFirestore.getInstance();
        this.callBackFragment=callBackFragment;
    }

    @NonNull
    @Override
    public Tailor_new_ordersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.tailor_new_order_custom,null);
        return new Tailor_new_ordersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Tailor_new_ordersViewHolder holder, int position) {
        holder.customerName.setText(list.get(position).getCustomerName());
        holder.category.setText("Category: "+list.get(position).getCategory());
        holder.duedate.setText("Date: "+list.get(position).getDate());
        holder.measurements.setText(list.get(position).getMeasurements());
        holder.orderNo.setText("Order# "+list.get(position).getOrderNo());
        holder.phone.setText("Contact: "+list.get(position).getPhone());
        holder.address.setText("Address: "+list.get(position).getAddress());
        holder.acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Orders").document(list.get(position).getOrderNo()).update("Status","1").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Successfully Transfer to Active Orders", Toast.LENGTH_SHORT).show();
                        callBackFragment.OnCallBack(position);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.rejectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Orders").document(list.get(position).getOrderNo()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Rejected Successfully", Toast.LENGTH_SHORT).show();
                        callBackFragment.OnCallBack(position);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        StorageReference storageReference1=storageReference.child("Orders/"+list.get(position).getOrderNo()+"/Pic1.jpg");
        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri!=null)
                {
                    Glide.with(context).load(uri).placeholder(R.drawable.personimage).into(holder.image1);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
