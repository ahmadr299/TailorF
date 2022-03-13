package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Customer_Order_Activity extends AppCompatActivity implements View.OnClickListener {
    private TextView serviceName, cost, measurement;
    private RadioGroup stitichingMode;
    private EditText date, stitichingDetails, address, et_kurtalength, et_kurtaneck, et_kurtashoulder, et_kurtachest, et_kurtawaist, et_kurtaseat, et_kurtasleeves, et_kurtasleevescircum, et_pantwaist, et_pantseat, et_pantcalf, et_pantlength;
    private Button finish_btn, kurta_addbtn, pant_addbtn;
    private ImageView clothImage1, clothImage2, back_button;
    private ImageButton clothImage1Button, clothImage2Button, add_btn;
    private Uri Image1, Image2;
    private RadioButton selectedRadio;
    private String Main, Sub, radioButtonText, CustomerName, ShopName, ShopAddress,OwnerName;
    private int int_random;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private String Phone, TailorPhone;
    private String CustomerWallet;
    private int RemaningAmount;
    private int TailorWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer__order_);
        assignIds();
        randomNumber();
        Intent intent = getIntent();
        Main = intent.getStringExtra("Main");
        Sub = intent.getStringExtra("Sub");
        serviceName.setText(Main + "(" + Sub + ")");
        SharedPreferences sharedPreferences = getSharedPreferences("Shop", MODE_PRIVATE);
        ShopName = sharedPreferences.getString("ShopName", "Tailor");
        OwnerName=sharedPreferences.getString("OwnerName","Tailor");
        setAddressandPhone();
        clothImage1Button.setOnClickListener(Customer_Order_Activity.this);
        clothImage2Button.setOnClickListener(Customer_Order_Activity.this);
        add_btn.setOnClickListener(Customer_Order_Activity.this);
        finish_btn.setOnClickListener(Customer_Order_Activity.this);
        back_button.setOnClickListener(Customer_Order_Activity.this);
        int RadioGroupId = stitichingMode.getCheckedRadioButtonId();
        SetPrice(RadioGroupId);
        stitichingMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SetPrice(checkedId);
            }
        });
    }

    private void setAddressandPhone() {
        firestore.collection("TailorsProfile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            if (ShopName.equals(snapshot.getString("Shop"))) {
                                TailorPhone = snapshot.getString("Phone");
                                ShopAddress = snapshot.getString("Area") + ", " + snapshot.getString("City") + ", " + snapshot.getString("Country");
                            }
                        }
                    } else {
                        Toast.makeText(Customer_Order_Activity.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Customer_Order_Activity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void randomNumber() {
        Random rand = new Random();
        int upperbound = 10000;
        int_random = rand.nextInt(upperbound);
    }

    private void sendOrder() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        if (date.getText().toString().isEmpty() ||
                stitichingDetails.getText().toString().isEmpty() ||
                address.getText().toString().isEmpty() ||
                measurement.getText().toString().isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Fill all the Fields", Toast.LENGTH_SHORT).show();
        } else {
            if (Image2 == null || Image1 == null) {
                progressDialog.dismiss();
                Toast.makeText(this, "Please Select Images", Toast.LENGTH_SHORT).show();
            } else {
                getCustomerNameandPhone();
                int radioGroupId = stitichingMode.getCheckedRadioButtonId();
                selectedRadio = findViewById(radioGroupId);
                radioButtonText = selectedRadio.getText().toString();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, Object> orders = new HashMap();
                        orders.put("CustomerName", CustomerName);
                        orders.put("OrderID", int_random + "");
                        orders.put("ShopName", ShopName);
                        orders.put("MainCategory", Main);
                        orders.put("SubCategory", Sub);
                        orders.put("Status", "0");
                        orders.put("Address", address.getText().toString());
                        orders.put("Instructions", stitichingDetails.getText().toString());
                        orders.put("StitchingMode", radioButtonText);
                        orders.put("UserId", firebaseAuth.getCurrentUser().getUid());
                        orders.put("CustomerEmail", firebaseAuth.getCurrentUser().getEmail());
                        orders.put("DueDate", date.getText().toString());
                        orders.put("Phone", Phone);
                        orders.put("Price", cost.getText().toString());
                        orders.put("TailorPhone", TailorPhone);
                        orders.put("ShopAddress", ShopAddress);
                        orders.put("Measurements",measurement.getText().toString());
                        DocumentReference documentReference = firestore.collection("Orders").document(int_random + "");
                        documentReference.set(orders).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                firestore.collection("Customers").document(firebaseAuth.getCurrentUser().getUid()).update("Wallet",RemaningAmount+"").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            firestore.collection("Tailors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        if (task.getResult().size()>0)
                                                        {
                                                            for (QueryDocumentSnapshot snapshot:task.getResult())
                                                            {
                                                                if (OwnerName.equals(snapshot.getString("Name")))
                                                                {
                                                                    TailorWallet=Integer.parseInt(cost.getText().toString())+Integer.parseInt(snapshot.getString("Wallet"));
                                                                    firestore.collection("Tailors").document(snapshot.getId()).update("Wallet",TailorWallet+"").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                uploadImages(progressDialog);
                                                                            }
                                                                            else
                                                                            {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(Customer_Order_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(Customer_Order_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(Customer_Order_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(Customer_Order_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }, 5000);
            }
        }
    }

    private void uploadImages(ProgressDialog progressDialog) {
        StorageReference storageReference1 = storageReference.child("Orders/" + int_random + "/Pic1.jpg");
        storageReference1.putFile(Image1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference storageReference2 = storageReference.child("Orders/" + int_random + "/Pic2.jpg");
                storageReference2.putFile(Image2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        startActivity(new Intent(Customer_Order_Activity.this, CustomerNavigationDrawer_Activity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Customer_Order_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Customer_Order_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCustomerNameandPhone() {
        firestore.collection("Customers").document(firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                CustomerName = value.getString("Name");
                Phone = value.getString("Phone");
                CustomerWallet=value.getString("Wallet");
                RemaningAmount=Integer.parseInt(CustomerWallet)-Integer.parseInt(cost.getText().toString());
            }
        });
    }

    private void assignIds() {
        serviceName = findViewById(R.id.serviceName);
        cost = findViewById(R.id.cost);
        measurement = findViewById(R.id.measurement);
        stitichingMode = findViewById(R.id.stitichingMode);
        date = findViewById(R.id.date);
        stitichingDetails = findViewById(R.id.stitichingDetails);
        add_btn = findViewById(R.id.add_btn);
        clothImage1 = findViewById(R.id.clothImage1);
        clothImage2 = findViewById(R.id.clothImage2);
        clothImage1Button = findViewById(R.id.clothImage1Button);
        clothImage2Button = findViewById(R.id.clothImage2Button);
        finish_btn = findViewById(R.id.finish_btn);
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        address = findViewById(R.id.address);
        back_button = findViewById(R.id.back_button);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clothImage1Button:
                selectImage1();
                break;
            case R.id.clothImage2Button:
                selectImage2();
                break;
            case R.id.add_btn:
                Log.e("Sub", Sub);
                if (Sub.equals("Kurta")) {
                    Log.e("MatchKurta", "Match");
                    KurtaMeasurements();
                } else {
                    PantMeasurements();
                }
                break;
            case R.id.finish_btn:
                sendOrder();
                break;
            case R.id.back_button:
                startActivity(new Intent(Customer_Order_Activity.this, Select_Category_Activity.class));
                finish();
                break;
        }
    }

    private void PantMeasurements() {
        View dialogView = LayoutInflater.from(Customer_Order_Activity.this).inflate(R.layout.pant_measurements_custom, null);
        et_pantcalf = dialogView.findViewById(R.id.et_pantcalf);
        et_pantlength = dialogView.findViewById(R.id.et_pantlength);
        et_pantseat = dialogView.findViewById(R.id.et_pantseat);
        et_pantwaist = dialogView.findViewById(R.id.et_pantwaist);
        pant_addbtn = dialogView.findViewById(R.id.pant_addbtn);
        AlertDialog.Builder builder = new AlertDialog.Builder(Customer_Order_Activity.this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        pant_addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_pantcalf.getText().toString().isEmpty() ||
                        et_pantlength.getText().toString().isEmpty() ||
                        et_pantseat.getText().toString().isEmpty() ||
                        et_pantwaist.getText().toString().isEmpty()) {
                    Toast.makeText(Customer_Order_Activity.this, "Please Fill all the Measurements", Toast.LENGTH_SHORT).show();
                } else {
                    measurement.setText("Length: " + et_pantlength.getText().toString() +
                            ", Waist: " + et_pantwaist.getText().toString() +
                            ", Calf: " + et_pantcalf.getText().toString() +
                            ", Seat: " + et_pantseat.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void KurtaMeasurements() {
        View dialogView = LayoutInflater.from(Customer_Order_Activity.this).inflate(R.layout.kurta_measurements_custom, null);
        et_kurtalength = dialogView.findViewById(R.id.et_kurtalength);
        et_kurtaneck = dialogView.findViewById(R.id.et_kurtaneck);
        et_kurtashoulder = dialogView.findViewById(R.id.et_kurtashoulder);
        et_kurtachest = dialogView.findViewById(R.id.et_kurtachest);
        et_kurtawaist = dialogView.findViewById(R.id.et_kurtawaist);
        et_kurtaseat = dialogView.findViewById(R.id.et_kurtaseat);
        et_kurtasleeves = dialogView.findViewById(R.id.et_kurtasleeves);
        et_kurtasleevescircum = dialogView.findViewById(R.id.et_kurtasleevescircum);
        kurta_addbtn = dialogView.findViewById(R.id.kurta_addbtn);
        AlertDialog.Builder builder = new AlertDialog.Builder(Customer_Order_Activity.this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        kurta_addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_kurtalength.getText().toString().isEmpty() ||
                        et_kurtaneck.getText().toString().isEmpty() ||
                        et_kurtashoulder.getText().toString().isEmpty() ||
                        et_kurtachest.getText().toString().isEmpty() ||
                        et_kurtawaist.getText().toString().isEmpty() ||
                        et_kurtaseat.getText().toString().isEmpty() ||
                        et_kurtasleeves.getText().toString().isEmpty() ||
                        et_kurtasleevescircum.getText().toString().isEmpty()) {
                    Toast.makeText(Customer_Order_Activity.this, "Please Fill all the Measurements", Toast.LENGTH_SHORT).show();
                } else {
                    measurement.setText("Length: " + et_kurtalength.getText().toString() +
                            ", Neck: " + et_kurtaneck.getText().toString() +
                            ", Shoulder: " + et_kurtashoulder.getText().toString() +
                            ", Chest: " + et_kurtachest.getText().toString() +
                            ", Waist: " + et_kurtawaist.getText().toString() +
                            ", Seat: " + et_kurtaseat.getText().toString() +
                            ", Sleeves: " + et_kurtasleeves.getText().toString() +
                            ", Sleeves Circum: " + et_kurtasleevescircum.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void selectImage2() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 2000);
    }

    private void selectImage1() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Image1 = data.getData();
            clothImage1.setVisibility(View.VISIBLE);
            clothImage1.setImageURI(Image1);
            clothImage1Button.setVisibility(View.INVISIBLE);
        }
        if (requestCode == 2000) {
            Image2 = data.getData();
            clothImage2.setVisibility(View.VISIBLE);
            clothImage2.setImageURI(Image2);
            clothImage2Button.setVisibility(View.INVISIBLE);
        }
    }

    private void SetPrice(int checkedId) {
        RadioButton radioButton = findViewById(checkedId);
        String selectedMode = radioButton.getText().toString();
        firestore.collection("TailorsProfile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            if (ShopName.equals(snapshot.getString("Shop"))) {
                                if (selectedMode.equals("New Stitching")) {
                                    cost.setText(snapshot.getString(Sub));
                                } else {
                                    cost.setText(snapshot.getString(Sub + "Alter"));
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(Customer_Order_Activity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}