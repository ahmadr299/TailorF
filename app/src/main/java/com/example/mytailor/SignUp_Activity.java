package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp_Activity extends AppCompatActivity implements View.OnClickListener {
    private TextView scustomerortailor,alreadyhaveaccount;
    private EditText fullName,Email,password,phone;
    private Button registerBtn;
    private String SignUp_SELECTOR;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_);
        assignIds();
        registerBtn.setOnClickListener(SignUp_Activity.this);
        alreadyhaveaccount.setOnClickListener(SignUp_Activity.this);
        Intent SignUp_Selector_intent=getIntent();
        SignUp_SELECTOR=SignUp_Selector_intent.getStringExtra("SignUp_Selector");
        scustomerortailor.setText(SignUp_SELECTOR);
    }

    private void assignIds() {
        scustomerortailor=findViewById(R.id.scustomerortailor);
        alreadyhaveaccount=findViewById(R.id.alreadyhaveaccount);
        fullName=findViewById(R.id.fullName);
        Email=findViewById(R.id.uEmail);
        password=findViewById(R.id.uphone);
        phone=findViewById(R.id.phone);
        registerBtn=findViewById(R.id.registerBtn);
        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.alreadyhaveaccount:
                Intent alreadyhaveaccount_intent=new Intent(SignUp_Activity.this,LogIn_Activity.class);
                if (scustomerortailor.getText().toString().equals("Customer SignUp"))
                {
                    alreadyhaveaccount_intent.putExtra("Selector","Customer Login");
                }
                else
                {
                    alreadyhaveaccount_intent.putExtra("Selector","Tailor Login");
                }
                startActivity(alreadyhaveaccount_intent);
                finish();
                break;
            case R.id.registerBtn:
                Register();
                break;
        }
    }

    private void Register() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (fullName.getText().toString().isEmpty() ||
                Email.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() ||
                phone.getText().toString().isEmpty())
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Fill all the Fields", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (scustomerortailor.getText().toString().equals("Customer SignUp"))
            {
                firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Map<String, Object> customers=new HashMap<>();
                            customers.put("Name",fullName.getText().toString());
                            customers.put("Phone", phone.getText().toString());
                            customers.put("Email",Email.getText().toString());
                            customers.put("Wallet","2000");
                            DocumentReference documentReference=firestore.collection("Customers").document(firebaseAuth.getCurrentUser().getUid());
                            documentReference.set(customers).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(SignUp_Activity.this,CustomerNavigationDrawer_Activity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUp_Activity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else
            {
                firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Map<String, Object> tailors=new HashMap<>();
                            tailors.put("Name",fullName.getText().toString());
                            tailors.put("Phone", phone.getText().toString());
                            tailors.put("Email",Email.getText().toString());
                            tailors.put("ProfileStatus","0");
                            tailors.put("Wallet","0");
                            DocumentReference documentReference=firestore.collection("Tailors").document(firebaseAuth.getCurrentUser().getUid());
                            documentReference.set(tailors).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Map<String, Object> map=new HashMap<>();
                                    map.put("NoofRates","0");
                                    map.put("Rating","0.0");
                                    map.put("TotalRating","0.0");
                                    DocumentReference documentReference1=firestore.collection("Ratings").document(fullName.getText().toString());
                                    documentReference1.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Intent intent=new Intent(SignUp_Activity.this,Tailor_Profile_Acitivity.class);
                                            intent.putExtra("ProfileStatus","0");
                                            intent.putExtra("OwnerName",fullName.getText().toString());
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUp_Activity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUp_Activity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
}