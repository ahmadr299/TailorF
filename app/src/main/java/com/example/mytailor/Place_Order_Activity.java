package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Place_Order_Activity extends AppCompatActivity {
    private EditText selecttailorsearchBox;
    private RecyclerView select_tailor_recyclerview;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private List<SelectTailorModelClass> selectTailorList = new ArrayList<>();
    private ImageView back_button;
    private SelectTailorAdapter adapter;
    private String rating;
    private List<String> nameList = new ArrayList<>();
    private List<String> ratingList = new ArrayList<>();
    int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place__order_);
        assignIds();
        getRating();
        selecttailorsearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Place_Order_Activity.this, CustomerNavigationDrawer_Activity.class));
                finish();
            }
        });
    }

    private void getRating() {
        firestore.collection("Ratings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Log.e("Body", "Rating for loop");
                            ratingList.add(snapshot.getString("Rating"));
                            nameList.add(snapshot.getId());
                        }
                    }
                    {
                        getTailors();
                    }
                    for (int i = 0; i < nameList.size(); i++) {
                        Log.e("ListName", nameList.get(i));
                        Log.e("ListRating", ratingList.get(i));
                    }
                } else {
                    Toast.makeText(Place_Order_Activity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filter(String text) {
        List<SelectTailorModelClass> filteredList = new ArrayList<>();
        for (SelectTailorModelClass item : selectTailorList) {
            if (item.getOwnerName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void getTailors() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firestore.collection("TailorsProfile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            String AllCategories = "";
                            Log.e("noofcategories", snapshot.getString("Categories"));
                            int noofcategories = Integer.parseInt(snapshot.getString("Categories"));
                            for (int i = 0; i < noofcategories; i++) {
                                if (i == 0) {
                                    AllCategories = snapshot.getString("Category1");
                                } else {
                                    AllCategories = AllCategories + ", " + snapshot.getString("Category" + (i + 1));
                                }
                            }
                            for (int i = 0; i < nameList.size(); i++) {
                                if (snapshot.getString("OwnerName").equals(nameList.get(i))) {
                                    rating = ratingList.get(i);
                                }
                            }
                            SelectTailorModelClass selectTailorModelClass = new SelectTailorModelClass();
                            selectTailorModelClass.setCategories(AllCategories);
                            selectTailorModelClass.setPhone(snapshot.getString("Phone"));
                            selectTailorModelClass.setOwnerName(snapshot.getString("OwnerName"));
                            selectTailorModelClass.setShopName(snapshot.getString("Shop"));
                            selectTailorModelClass.setAddress(snapshot.getString("Area") + ", " + snapshot.getString("City") + ", " + snapshot.getString("Country"));
                            selectTailorModelClass.setRating(rating);
                            Log.e("OwnerName", snapshot.getString("OwnerName"));
                            Log.e("Shop", snapshot.getString("Shop"));
                            Log.e("Phone", snapshot.getString("Phone"));
                            Log.e("Categories", AllCategories);
                            Log.e("Rating", rating);
                            selectTailorList.add(selectTailorModelClass);
                            Log.e("SelectTailorListinLoop", selectTailorList.get(j).getShopName());
                            j++;
                        }
                    }
                    for (int i = 0; i < selectTailorList.size(); i++) {
                        Log.e("SelectTailorList", selectTailorList.get(i).getShopName());
                    }
                    progressDialog.dismiss();
                    adapter = new SelectTailorAdapter(selectTailorList, Place_Order_Activity.this);
                    select_tailor_recyclerview.setLayoutManager(new GridLayoutManager(Place_Order_Activity.this, 1));
                    select_tailor_recyclerview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Place_Order_Activity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void assignIds() {
        select_tailor_recyclerview = findViewById(R.id.select_tailor_recyclerview);
        selecttailorsearchBox = findViewById(R.id.selecttailorsearchBox);
        back_button = findViewById(R.id.back_button);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }
}