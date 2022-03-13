package com.example.mytailor;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Tailor_new_ordersViewHolder extends RecyclerView.ViewHolder {
    TextView customerName,orderNo,phone,category,measurements,duedate,address;
    Button acceptbtn,rejectbtn;
    ImageView image1;
    public Tailor_new_ordersViewHolder(@NonNull View itemView) {
        super(itemView);
        customerName=itemView.findViewById(R.id.customerName);
        orderNo=itemView.findViewById(R.id.orderNo);
        phone=itemView.findViewById(R.id.phone);
        category=itemView.findViewById(R.id.category);
        measurements=itemView.findViewById(R.id.measurements);
        duedate=itemView.findViewById(R.id.duedate);
        acceptbtn=itemView.findViewById(R.id.acceptbtn);
        rejectbtn=itemView.findViewById(R.id.rejectbtn);
        image1=itemView.findViewById(R.id.image1);
        address=itemView.findViewById(R.id.address);
    }
}
