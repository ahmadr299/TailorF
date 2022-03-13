package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class TailorNavigationDrawer_Activity extends AppCompatActivity {
    private DrawerLayout drawerlayout;
    private NavigationView navigation;
    private BottomNavigationView bottom_nav;
    private ActionBarDrawerToggle toggle;
    private androidx.appcompat.widget.Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ImageView tailordrawerimg, profilePicture;
    private TextView ProfileName;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailor_navigation_drawer_);
        assignIds();
        navigationDrawer();
        setProfileImageandName();
        Tailor_Orders_Fragment tailor_orders_fragment = new Tailor_Orders_Fragment();
        replaceFragment(tailor_orders_fragment);
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_order:
                        Tailor_Orders_Fragment tailor_orders_fragment = new Tailor_Orders_Fragment();
                        replaceFragment(tailor_orders_fragment);
                        break;
                    case R.id.bottom_faqs:
                        Tailor_FAQs_Fragment tailor_faQs_fragment = new Tailor_FAQs_Fragment();
                        replaceFragment(tailor_faQs_fragment);
                        break;
                }
                return true;
            }
        });
        tailordrawerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
                    drawerlayout.closeDrawer(GravityCompat.START);
                } else
                    drawerlayout.openDrawer(GravityCompat.START);
            }

        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.tailormainframe, fragment);
        transaction.commit();
    }

    private void setProfileImageandName() {
        firestore.collection("Tailors").document(firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ProfileName.setText(value.getString("Name"));
                StorageReference storageReference1=storageReference.child("TailorsProfile/"+ProfileName.getText().toString()+".jpg");
                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(uri!=null)
                        {
                            Glide.with(getApplicationContext()).load(uri).into(profilePicture);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TailorNavigationDrawer_Activity.this, "Profile Image Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void assignIds() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        bottom_nav = findViewById(R.id.bottom_nav);
        drawerlayout = findViewById(R.id.drawerlayout);
        navigation = findViewById(R.id.navigation);
        tailordrawerimg = findViewById(R.id.tailordrawerimg);
        View view = navigation.getHeaderView(0);
        ProfileName = view.findViewById(R.id.ProfileName);
        profilePicture = view.findViewById(R.id.profilePicture);
        storageReference= FirebaseStorage.getInstance().getReference();
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
                    case R.id.mProfile:
                        drawerlayout.closeDrawer(GravityCompat.START);
                        Intent intent=new Intent(TailorNavigationDrawer_Activity.this, Tailor_Profile_Acitivity.class);
                        intent.putExtra("ProfileStatus","1");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.mSetting:
                        drawerlayout.closeDrawer(GravityCompat.START);
                        Intent Setting=new Intent(new Intent(TailorNavigationDrawer_Activity.this,Setting_Activity.class));
                        Setting.putExtra("Person","Tailor");
                        startActivity(Setting);
                        break;
                    case R.id.mLogout:
                        drawerlayout.closeDrawer(GravityCompat.START);
                        SharedPreferences sharedPreferences=getSharedPreferences("Logout",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("Status","0");
                        editor.putString("Person","Tailor");
                        editor.commit();
                        Intent intent1 = new Intent(TailorNavigationDrawer_Activity.this, MainActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                }
                return true;
            }
        });
    }
}