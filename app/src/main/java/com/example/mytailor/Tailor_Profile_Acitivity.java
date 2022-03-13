package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.shapes.PathShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tailor_Profile_Acitivity extends AppCompatActivity implements View.OnClickListener {
    private EditText tailorprofilePersonName_ed,tailorprofileShopName_ed,tailorprofilePhoneNumber_ed,profileCountryAddress_ed,profileCityAddress_ed,profileAreaAddress_ed,kurta,pant,kurtaalter,pantalter,tailorWallet;
    private TextView addCategory,tailorShopLogo,tv_categories,addPrice;
    private Button saveProfile;
    private Uri Image;
    private List<String> categories=new ArrayList<>();
    private Button selectCategorybtn;
    private CheckBox maleStitching,femaleStitching,boyStitching,girlStitching;
    private String SelectedCategories,Kurta,Pant,KurtaAlter,PantAlter,OwnerName;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ImageView back_button_profile;
    private Button addpricesbtn;
    private String ProfileStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailor__profile__acitivity);
        assignIds();
        Intent intent=getIntent();
        getTailorWallet();
        ProfileStatus=intent.getStringExtra("ProfileStatus");
        OwnerName=intent.getStringExtra("OwnerName");
        if (ProfileStatus.equals("1"))
        {
            firestore.collection("TailorsProfile").document(firebaseAuth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    tailorprofileShopName_ed.setText(value.getString("Shop"));
                    tailorprofilePersonName_ed.setText(value.getString("OwnerName"));
                    tailorprofilePhoneNumber_ed.setText(value.getString("Phone"));
                }
            });
            tailorprofileShopName_ed.setEnabled(false);
            tailorprofilePersonName_ed.setEnabled(false);
        }
        else
        {
            tailorprofilePersonName_ed.setText(OwnerName);
            tailorprofilePersonName_ed.setEnabled(false);
            back_button_profile.setVisibility(View.INVISIBLE);
        }
        addCategory.setOnClickListener(Tailor_Profile_Acitivity.this);
        saveProfile.setOnClickListener(Tailor_Profile_Acitivity.this);
        tailorShopLogo.setOnClickListener(Tailor_Profile_Acitivity.this);
        back_button_profile.setOnClickListener(Tailor_Profile_Acitivity.this);
        addPrice.setOnClickListener(Tailor_Profile_Acitivity.this);
    }

    private void getTailorWallet() {
        firestore.collection("Tailors").document(firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                tailorWallet.setText(value.getString("Wallet"));
            }
        });
    }

    private void assignIds() {
        tailorprofilePersonName_ed=findViewById(R.id.tailorprofilePersonName_ed);
        tailorprofileShopName_ed=findViewById(R.id.tailorprofileShopName_ed);
        tailorprofilePhoneNumber_ed=findViewById(R.id.tailorprofilePhoneNumber_ed);
        profileCountryAddress_ed=findViewById(R.id.profileCountryAddress_ed);
        profileCityAddress_ed=findViewById(R.id.profileCityAddress_ed);
        profileAreaAddress_ed=findViewById(R.id.profileAreaAddress_ed);
        back_button_profile=findViewById(R.id.back_button_profile);
        addCategory=findViewById(R.id.addCategory);
        tailorShopLogo=findViewById(R.id.tailorShopLogo);
        saveProfile=findViewById(R.id.saveProfile);
        tv_categories=findViewById(R.id.tv_categories);
        tailorWallet=findViewById(R.id.tailorWallet);
        addPrice=findViewById(R.id.addPrice);
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addCategory:
                ADDCategories();
                break;
            case R.id.saveProfile:
                SaveProfile();
                break;
            case R.id.tailorShopLogo:
                chooseLogo();
                break;
            case R.id.addPrice:
                addPrice();
                break;
            case R.id.back_button_profile:
                startActivity(new Intent(Tailor_Profile_Acitivity.this,TailorNavigationDrawer_Activity.class));
                break;
        }
    }

    private void addPrice() {
        View dialogView=LayoutInflater.from(Tailor_Profile_Acitivity.this).inflate(R.layout.add_prices_custom,null);
        kurta=dialogView.findViewById(R.id.kurta);
        pant=dialogView.findViewById(R.id.pant);
        kurtaalter=dialogView.findViewById(R.id.kurtaalter);
        pantalter=dialogView.findViewById(R.id.pantalter);
        addpricesbtn=dialogView.findViewById(R.id.addpricesbtn);
        AlertDialog.Builder builder=new AlertDialog.Builder(Tailor_Profile_Acitivity.this);
        builder.setView(dialogView);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        addpricesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kurta.getText().toString().isEmpty() ||
                pant.getText().toString().isEmpty() ||
                kurtaalter.getText().toString().isEmpty() ||
                pantalter.getText().toString().isEmpty())
                {
                    Toast.makeText(Tailor_Profile_Acitivity.this, "Please Fill All the Fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    alertDialog.dismiss();
                    Kurta=kurta.getText().toString();
                    Pant=pant.getText().toString();
                    KurtaAlter=kurtaalter.getText().toString();
                    PantAlter=pantalter.getText().toString();
                    addPrice.setText("Kurta: "+Kurta+", "+"Pant: "+Pant+", Kurta Alternation: "+KurtaAlter+", "+"Pant Alternatio: "+PantAlter);
                }
            }
        });

    }

    private void ADDCategories() {
        View dialogView= LayoutInflater.from(Tailor_Profile_Acitivity.this).inflate(R.layout.categories_dialog_view,null);
        selectCategorybtn=dialogView.findViewById(R.id.selectCategorybtn);
        maleStitching=dialogView.findViewById(R.id.maleStitching);
        femaleStitching=dialogView.findViewById(R.id.femaleStitching);
        boyStitching=dialogView.findViewById(R.id.boyStitching);
        girlStitching=dialogView.findViewById(R.id.girlStitching);
        AlertDialog.Builder builder=new AlertDialog.Builder(Tailor_Profile_Acitivity.this);
        builder.setView(dialogView);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        selectCategorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count=0;
                if (maleStitching.isChecked())
                {
                    categories.add(maleStitching.getText().toString());
                    count++;
                }
                if (femaleStitching.isChecked())
                {
                    categories.add(femaleStitching.getText().toString());
                    count++;
                }
                if (boyStitching.isChecked())
                {
                    categories.add(boyStitching.getText().toString());
                    count++;
                }
                if (girlStitching.isChecked())
                {
                    categories.add(girlStitching.getText().toString());
                    count++;
                }
                for (int i=0;i<categories.size();i++)
                {
                    if (i==0)
                    {
                        SelectedCategories=categories.get(i);
                    }
                    else {
                        SelectedCategories = SelectedCategories + ", " + categories.get(i);
                    }
                }
                tv_categories.setText(SelectedCategories);
                SelectedCategories="";
                alertDialog.dismiss();
            }
        });
    }

    private void SaveProfile() {
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (tailorprofilePersonName_ed.getText().toString().isEmpty() ||
        tailorprofilePhoneNumber_ed.getText().toString().isEmpty() ||
        tailorprofileShopName_ed.getText().toString().isEmpty() ||
        profileAreaAddress_ed.getText().toString().isEmpty() ||
        profileCityAddress_ed.getText().toString().isEmpty() ||
        profileCountryAddress_ed.getText().toString().isEmpty() ||
        Kurta.isEmpty() ||
        Pant.isEmpty())
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Fill all the Fields", Toast.LENGTH_LONG).show();
        }
        else
        {
            Map<String, Object> tailorProfile=new HashMap<>();
            tailorProfile.put("Shop",tailorprofileShopName_ed.getText().toString());
            tailorProfile.put("Country",profileCountryAddress_ed.getText().toString());
            tailorProfile.put("City",profileCityAddress_ed.getText().toString());
            tailorProfile.put("Area",profileAreaAddress_ed.getText().toString());
            tailorProfile.put("Categories",categories.size()+"");
            tailorProfile.put("Phone",tailorprofilePhoneNumber_ed.getText().toString());
            tailorProfile.put("OwnerName",tailorprofilePersonName_ed.getText().toString());
            tailorProfile.put("Kurta",Kurta);
            tailorProfile.put("Pant",Pant);
            tailorProfile.put("KurtaAlter",KurtaAlter);
            tailorProfile.put("PantAlter",PantAlter);
            for (int i=0;i<categories.size();i++)
            {
                tailorProfile.put("Category"+(i+1),categories.get(i));
            }
            categories.clear();
            DocumentReference documentReference=firestore.collection("TailorsProfile").document(firebaseAuth.getCurrentUser().getEmail());
            documentReference.set(tailorProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        if (Image!=null)
                        {
                            StorageReference storageReference1=storageReference.child("TailorsProfile/"+tailorprofilePersonName_ed.getText().toString().toString()+".jpg");
                            storageReference1.putFile(Image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    updateNameandPhone(progressDialog);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Tailor_Profile_Acitivity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else
                        {
                            updateNameandPhone(progressDialog);
                        }
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(Tailor_Profile_Acitivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void updateNameandPhone(ProgressDialog progressDialog) {
        firestore.collection("Tailors").document(firebaseAuth.getCurrentUser().getUid()).update("ProfileStatus","1").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    firestore.collection("Tailors").document(firebaseAuth.getCurrentUser().getUid()).update("Name",tailorprofilePersonName_ed.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                firestore.collection("Tailors").document(firebaseAuth.getCurrentUser().getUid()).update("Phone",tailorprofilePhoneNumber_ed.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(Tailor_Profile_Acitivity.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Tailor_Profile_Acitivity.this,TailorNavigationDrawer_Activity.class));
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(Tailor_Profile_Acitivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Tailor_Profile_Acitivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(Tailor_Profile_Acitivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void chooseLogo() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1000)
        {
            Image=data.getData();
        }
    }
}