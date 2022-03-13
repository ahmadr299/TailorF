package com.example.mytailor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.grpc.internal.AbstractReadableBuffer;

public class Setting_Activity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout termsandconditions,privacypolicy;
    private ImageView back_button_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);
        assignIds();
        termsandconditions.setOnClickListener(Setting_Activity.this);
        privacypolicy.setOnClickListener(Setting_Activity.this);
        back_button_setting.setOnClickListener(Setting_Activity.this);
    }

    private void assignIds() {
        termsandconditions=findViewById(R.id.termsandconditions);
        privacypolicy=findViewById(R.id.privacypolicy);
        back_button_setting=findViewById(R.id.back_button_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.termsandconditions:
                startActivity(new Intent(Setting_Activity.this,Terms_and_Conditions_Activity.class));
                break;
            case R.id.privacypolicy:
                startActivity(new Intent(Setting_Activity.this,Privacy_Policy_Activity.class));
                break;
            case R.id.back_button_setting:
                Intent intent=getIntent();
                if (intent.getStringExtra("Person").equals("Tailor")) {
                    startActivity(new Intent(Setting_Activity.this, TailorNavigationDrawer_Activity.class));
                    finish();
                }
                else
                {
                    startActivity(new Intent(Setting_Activity.this, CustomerNavigationDrawer_Activity.class));
                    finish();
                }
                break;
        }
    }
}