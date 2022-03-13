package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Tailor_Rate_Activity extends AppCompatActivity {
    private RatingBar ratebar;
    private Button ratebtn;
    private String ShopName;
    private FirebaseFirestore firestore;
    private String FirebaseRating,TotalRating;
    private String documentId,TailorName;
    private Float Rating;
    private int noofRates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailor__rate_);
        assignIds();
        Intent intent=getIntent();
        ShopName=intent.getStringExtra("ShopName");
        getFirebaseRating();
        ratebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratebar.getRating()==0)
                {
                    Toast.makeText(Tailor_Rate_Activity.this, "Please Rate The Tailor", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    noofRates++;
                    firestore.collection("Ratings").document(TailorName).update("NoofRates",noofRates+"").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                firestore.collection("Ratings").document(TailorName).update("TotalRating",(Float.parseFloat(TotalRating)+ratebar.getRating())+"").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            if (FirebaseRating.equals("0.0"))
                                            {
                                                Rating=ratebar.getRating();
                                            }
                                            else
                                            {
                                                Rating=(Float.parseFloat(TotalRating)+ratebar.getRating())/noofRates;
                                            }
                                            firestore.collection("Ratings").document(TailorName).update("Rating",Rating+"").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(Tailor_Rate_Activity.this, "Successfully Rate Tailor", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(Tailor_Rate_Activity.this,CustomerNavigationDrawer_Activity.class));
                                                        finish();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(Tailor_Rate_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                        else
                                        {
                                            Toast.makeText(Tailor_Rate_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(Tailor_Rate_Activity.this, "Error: ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void getFirebaseRating() {
        firestore.collection("TailorsProfile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().size()>0)
                    {
                        for (QueryDocumentSnapshot snapshot:task.getResult())
                        {
                            Log.e("ShopName",ShopName);
                            if (ShopName.equals(snapshot.getString("Shop")))
                            {
                                TailorName=snapshot.getString("OwnerName");
                                Log.e("TailorName",TailorName);
                                firestore.collection("Ratings").document(TailorName).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        noofRates=Integer.parseInt(value.getString("NoofRates"));
                                        FirebaseRating=value.getString("Rating");
                                        TotalRating=value.getString("TotalRating");
                                    }
                                });
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(Tailor_Rate_Activity.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Tailor_Rate_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void assignIds() {
        ratebar=findViewById(R.id.ratebar);
        ratebtn=findViewById(R.id.ratebtn);
        firestore=FirebaseFirestore.getInstance();
    }
}