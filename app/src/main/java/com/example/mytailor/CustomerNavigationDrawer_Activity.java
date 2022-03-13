package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CustomerNavigationDrawer_Activity extends AppCompatActivity {
    private DrawerLayout drawerlayout;
    private NavigationView navigation;
    private ActionBarDrawerToggle toggle;
    private androidx.appcompat.widget.Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ImageView customerdrawerimg, profilePicture;
    private TextView ProfileName;
    private TabLayout customer_tab_layout;
    private ViewPager customer_view_pager;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_navigation_drawer_);
        assignIds();
        navigationDrawer();
        setProfileImageandName();
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        myViewPagerAdapter.addFragment(new Customer_Active_Orders_Fragment(), "ACTIVE");
        myViewPagerAdapter.addFragment(new Customer_Due_Orders_Fragment(), "Past DUE");
        customer_view_pager.setAdapter(myViewPagerAdapter);
        customer_tab_layout.setupWithViewPager(customer_view_pager);
        customerdrawerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
                    drawerlayout.closeDrawer(GravityCompat.START);
                } else
                    drawerlayout.openDrawer(GravityCompat.START);
            }

        });
    }

    private void setProfileImageandName() {
        firestore.collection("Customers").document(firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ProfileName.setText(value.getString("Name"));
            }
        });
        StorageReference storageReference1 = storageReference.child("Customers/" + firebaseAuth.getCurrentUser().getEmail() + ".jpg");
        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri!=null)
                {
                    Glide.with(getApplicationContext()).load(uri).into(profilePicture);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CustomerNavigationDrawer_Activity.this, "Profile Image Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void navigationDrawer() {
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.Open, R.string.Close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigation.setItemIconTintList(null);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cProfile:
                        drawerlayout.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(CustomerNavigationDrawer_Activity.this, Customer_Profile_Activity.class));
                        break;
                    case R.id.cSetting:
                        drawerlayout.closeDrawer(GravityCompat.START);
                        Intent Setting=new Intent(new Intent(CustomerNavigationDrawer_Activity.this,Setting_Activity.class));
                        Setting.putExtra("Person","Customer");
                        startActivity(Setting);
                        break;
                    case R.id.cOrder:
                        drawerlayout.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(CustomerNavigationDrawer_Activity.this,Place_Order_Activity.class));
                        break;
                    case R.id.cLogout:
                        drawerlayout.closeDrawer(GravityCompat.START);
                        SharedPreferences sharedPreferences=getSharedPreferences("Logout",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("Status","0");
                        editor.putString("Person","Customer");
                        editor.commit();
                        Intent intent = new Intent(CustomerNavigationDrawer_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    private void assignIds() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        drawerlayout = findViewById(R.id.drawerlayout);
        navigation = findViewById(R.id.navigation);
        customerdrawerimg = findViewById(R.id.customerdrawerimg);
        View view = navigation.getHeaderView(0);
        ProfileName = view.findViewById(R.id.ProfileName);
        profilePicture = view.findViewById(R.id.profilePicture);
        customer_tab_layout = findViewById(R.id.customer_tab_layout);
        customer_view_pager = findViewById(R.id.customer_view_pager);
        storageReference= FirebaseStorage.getInstance().getReference();
    }
}