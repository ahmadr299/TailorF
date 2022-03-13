package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.xml.transform.Result;

public class Customer_Profile_Activity extends AppCompatActivity implements View.OnClickListener {
    private ImageView back_button_profile,image;
    private EditText profilePersonName_ed,profilePhoneNumber_ed,customerWallet;
    private Button choose_img,upload_img,saveProfile;
    private TextView tv_chooseimg,tv_edit_customerProfile;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private Uri Image;
    private boolean isUpload=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer__profile_);
        assignIds();
        getCustomerProfile();
        getCustomerImage();
        profilePhoneNumber_ed.setEnabled(false);
        profilePersonName_ed.setEnabled(false);
        choose_img.setVisibility(View.GONE);
        upload_img.setVisibility(View.GONE);
        saveProfile.setVisibility(View.GONE);
        tv_chooseimg.setVisibility(View.GONE);
        back_button_profile.setOnClickListener(Customer_Profile_Activity.this);
        choose_img.setOnClickListener(Customer_Profile_Activity.this);
        upload_img.setOnClickListener(Customer_Profile_Activity.this);
        saveProfile.setOnClickListener(Customer_Profile_Activity.this);
        tv_edit_customerProfile.setOnClickListener(Customer_Profile_Activity.this);
    }

    private void getCustomerImage() {
        StorageReference storageReference1=storageReference.child("Customers/"+firebaseAuth.getCurrentUser().getEmail()+".jpg");
        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(Customer_Profile_Activity.this).load(uri).into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Customer_Profile_Activity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCustomerProfile() {
        firestore.collection("Customers").document(firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                profilePersonName_ed.setText(value.getString("Name"));
                profilePhoneNumber_ed.setText(value.getString("Phone"));
                customerWallet.setText(value.getString("Wallet"));
            }
        });
    }

    private void saveProfile() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        if (profilePersonName_ed.getText().toString().isEmpty() ||
        profilePhoneNumber_ed.getText().toString().isEmpty())
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Fill all the Fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (isUpload) {
                firestore.collection("Customers").document(firebaseAuth.getCurrentUser().getUid()).update("Name", profilePersonName_ed.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firestore.collection("Customers").document(firebaseAuth.getCurrentUser().getUid()).update("Phone", profilePhoneNumber_ed.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(Customer_Profile_Activity.this,CustomerNavigationDrawer_Activity.class));
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(Customer_Profile_Activity.this, "Error Phone: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Customer_Profile_Activity.this, "Error Name: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                progressDialog.dismiss();
                Toast.makeText(this, "Please Upload Image First", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void assignIds() {
        back_button_profile=findViewById(R.id.back_button_profile);
        profilePersonName_ed=findViewById(R.id.profilePersonName_ed);
        profilePhoneNumber_ed=findViewById(R.id.profilePhoneNumber_ed);
        choose_img=findViewById(R.id.choose_img);
        upload_img=findViewById(R.id.upload_img);
        saveProfile=findViewById(R.id.saveProfile);
        tv_chooseimg=findViewById(R.id.tv_chooseimg);
        image=findViewById(R.id.image);
        customerWallet=findViewById(R.id.customerwallet);
        tv_edit_customerProfile=findViewById(R.id.tv_edit_customerProfile);
        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.choose_img:
                chooseImage();
                break;
            case R.id.upload_img:
                uploadImage();
                break;
            case R.id.saveProfile:
                saveProfile();
                break;
            case  R.id.back_button_profile:
                startActivity(new Intent(Customer_Profile_Activity.this,CustomerNavigationDrawer_Activity.class));
                finish();
                break;
            case R.id.tv_edit_customerProfile:
                tv_edit_customerProfile.setVisibility(View.GONE);
                profilePhoneNumber_ed.setEnabled(true);
                profilePersonName_ed.setEnabled(true);
                choose_img.setVisibility(View.VISIBLE);
                upload_img.setVisibility(View.VISIBLE);
                tv_chooseimg.setVisibility(View.VISIBLE);
                saveProfile.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (tv_chooseimg.getText().toString().equals("Image Selected"))
        {
            if (Image!=null) {
                StorageReference storageReference1 = storageReference.child("Customers/" + firebaseAuth.getCurrentUser().getEmail() + ".jpg");
                storageReference1.putFile(Image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        isUpload=true;
                        progressDialog.dismiss();
                        Toast.makeText(Customer_Profile_Activity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Customer_Profile_Activity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                progressDialog.dismiss();
                tv_chooseimg.setText("No Choose Image");
                Toast.makeText(this, "Image not Choose Please Choose Image Again", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Choose Image First", Toast.LENGTH_LONG).show();
        }
    }

    private void chooseImage() {
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
            tv_chooseimg.setText("Image Selected");
        }
    }
}