package com.example.mytailor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button tailor, customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignIds();
        tailor.setOnClickListener(MainActivity.this);
        customer.setOnClickListener(MainActivity.this);
    }

    private void assignIds() {
        tailor = findViewById(R.id.tailor);
        customer = findViewById(R.id.customer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customer:
                Intent customer_intent=new Intent(MainActivity.this,LogIn_Activity.class);
                customer_intent.putExtra("Selector","Customer Login");
                startActivity(customer_intent);
                finish();
                break;
            case R.id.tailor:
                Intent tailor_intent=new Intent(MainActivity.this,LogIn_Activity.class);
                tailor_intent.putExtra("Selector","Tailor Login");
                startActivity(tailor_intent);
                finish();
                break;
        }
    }
}