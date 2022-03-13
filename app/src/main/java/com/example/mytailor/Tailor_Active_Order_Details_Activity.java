package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Tailor_Active_Order_Details_Activity extends AppCompatActivity implements View.OnClickListener {
    private TextView cutomerNameOrderInfo,orderIdTitle,dueDateCustomerOrderInfo,category,measurements,address,totalBillAmount_edt,tv_phone;
    private ImageView back_btn_customerOrderDetail,phoneimage,image1;
    private Button orderCompleted;
    private String CustomerName,Phone,Address,Category,Order,Date,ImageUri,Measurements;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailor__active__order__details_);
        assignIds();
        getData();
        setData();
        setImage();
        back_btn_customerOrderDetail.setOnClickListener(Tailor_Active_Order_Details_Activity.this);
        phoneimage.setOnClickListener(Tailor_Active_Order_Details_Activity.this);
        orderCompleted.setOnClickListener(Tailor_Active_Order_Details_Activity.this);
    }

    private void setImage() {
        Glide.with(Tailor_Active_Order_Details_Activity.this).load(Uri.parse(ImageUri)).into(image1);
    }

    private void setData() {
        cutomerNameOrderInfo.setText(CustomerName);
        orderIdTitle.setText("Order# "+Order);
        dueDateCustomerOrderInfo.setText(Date);
        category.setText(Category);
        address.setText(Address);
        measurements.setText(Measurements);
    }

    private void getData() {
        Intent intent=getIntent();
        Order=intent.getStringExtra("Order");
        CustomerName=intent.getStringExtra("CustomerName");
        Phone=intent.getStringExtra("Phone");
        Address=intent.getStringExtra("Address");
        Category=intent.getStringExtra("Category");
        Date=intent.getStringExtra("Date");
        ImageUri=intent.getStringExtra("ImageUri");
        Measurements=intent.getStringExtra("Measurements");
        firestore.collection("Orders").document(Order).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                totalBillAmount_edt.setText(value.getString("Price"));
            }
        });
    }

    private void assignIds() {
        cutomerNameOrderInfo=findViewById(R.id.cutomerNameOrderInfo);
        orderIdTitle=findViewById(R.id.orderIdTitle);
        dueDateCustomerOrderInfo=findViewById(R.id.dueDateCustomerOrderInfo);
        category=findViewById(R.id.category);
        measurements=findViewById(R.id.measurements);
        address=findViewById(R.id.address);
        totalBillAmount_edt=findViewById(R.id.totalBillAmount_edt);
        back_btn_customerOrderDetail=findViewById(R.id.back_btn_customerOrderDetail);
        phoneimage=findViewById(R.id.phoneimage);
        image1=findViewById(R.id.image1);
        orderCompleted=findViewById(R.id.orderCompleted);
        firestore=FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_btn_customerOrderDetail:
                startActivity(new Intent(Tailor_Active_Order_Details_Activity.this,TailorNavigationDrawer_Activity.class));
                finish();
                break;
            case R.id.phoneimage:
                View dialogView= LayoutInflater.from(Tailor_Active_Order_Details_Activity.this).inflate(R.layout.phone_custom,null);
                tv_phone=dialogView.findViewById(R.id.tv_phone);
                tv_phone.setText(Phone);
                AlertDialog.Builder builder=new AlertDialog.Builder(Tailor_Active_Order_Details_Activity.this);
                builder.setView(dialogView);
                builder.show();
                break;
            case R.id.orderCompleted:
                firestore.collection("Orders").document(Order).update("Status","3").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(Tailor_Active_Order_Details_Activity.this, "Transfer to Completed Orders", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Tailor_Active_Order_Details_Activity.this,TailorNavigationDrawer_Activity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(Tailor_Active_Order_Details_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
}