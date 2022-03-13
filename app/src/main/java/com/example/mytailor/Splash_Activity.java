package com.example.mytailor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash_Activity extends AppCompatActivity {
    String Person,Status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_);
        SharedPreferences sharedPreferences=getSharedPreferences("Logout",MODE_PRIVATE);
        Status=sharedPreferences.getString("Status","0");
        Person=sharedPreferences.getString("Person","Customer");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Person.equals("Customer"))
                {
                    if (Status.equals("1"))
                    {
                        startActivity(new Intent(Splash_Activity.this,CustomerNavigationDrawer_Activity.class));
                        finish();
                    }
                    else
                    {
                        startActivity(new Intent(Splash_Activity.this,MainActivity.class));
                        finish();
                    }
                }
                else
                {
                    if (Status.equals("1"))
                    {
                        Intent intent=new Intent(Splash_Activity.this,TailorNavigationDrawer_Activity.class);
                        intent.putExtra("ProfileStatus", "1");
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        startActivity(new Intent(Splash_Activity.this,MainActivity.class));
                        finish();
                    }
                }
            }
        },3000);
    }
}