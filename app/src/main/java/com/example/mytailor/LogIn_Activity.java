package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LogIn_Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText Email,password,forgotemail;
    private Button loginBtn,forgotpasswordbtn;
    private TextView customerortailor,createaccount,forgotPassword;
    private String SELECTOR;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private boolean VerifyCustomer=false;
    private boolean VerifyTailor=false;
    private boolean ProfileStatus=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_);
        assignIds();
        createaccount.setOnClickListener(LogIn_Activity.this);
        loginBtn.setOnClickListener(LogIn_Activity.this);
        forgotPassword.setOnClickListener(LogIn_Activity.this);
        Intent selector_intent=getIntent();
        SELECTOR=selector_intent.getStringExtra("Selector");
        customerortailor.setText(SELECTOR);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent customer_intent=new Intent(LogIn_Activity.this,MainActivity.class);
        startActivity(customer_intent);
        finish();
    }

    private void assignIds() {
        Email=findViewById(R.id.uEmail);
        password=findViewById(R.id.uphone);
        loginBtn=findViewById(R.id.updateBtn);
        customerortailor=findViewById(R.id.customerortailor);
        createaccount=findViewById(R.id.createaccount);
        forgotPassword=findViewById(R.id.forgotPassword);
        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.createaccount:
                Intent create_account_intent=new Intent(LogIn_Activity.this,SignUp_Activity.class);
                if (customerortailor.getText().toString().equals("Customer Login"))
                {
                    create_account_intent.putExtra("SignUp_Selector","Customer SignUp");
                }
                else
                {
                    create_account_intent.putExtra("SignUp_Selector","Tailor SignUp");
                }
                startActivity(create_account_intent);
                finish();
                break;
            case R.id.forgotPassword:
                forgotPassword();
                break;
            case R.id.updateBtn:
                loginUser();
                break;
        }
    }

    private void loginUser() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (Email.getText().toString().isEmpty() ||
        password.getText().toString().isEmpty())
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Fill all the Fields", Toast.LENGTH_LONG).show();
        }
        else
        {
            firebaseAuth.signInWithEmailAndPassword(Email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        if (customerortailor.getText().toString().equals("Customer Login"))
                        {
                            firestore.collection("Customers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        if (task.getResult().size()>0)
                                        {
                                            for (QueryDocumentSnapshot snapshot:task.getResult()) {
                                                Log.e("Email",snapshot.getString("Email"));
                                                if (Email.getText().toString().equals(snapshot.getString("Email")))
                                                {
                                                    Log.e("MatchEmail",snapshot.getString("Email"));
                                                    VerifyCustomer=true;
                                                }
                                            }
                                            Log.e("VerifyCustomer",VerifyCustomer+"");
                                            if (VerifyCustomer)
                                            {
                                                progressDialog.dismiss();
                                                SharedPreferences sharedPreferences=getSharedPreferences("Logout",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("Status","1");
                                                editor.putString("Person","Customer");
                                                editor.commit();
                                                startActivity(new Intent(LogIn_Activity.this,CustomerNavigationDrawer_Activity.class));
                                                finish();
                                            }
                                            else
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(LogIn_Activity.this, "User not Exist in Customer Side", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            firestore.collection("Tailors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        if (task.getResult().size()>0)
                                        {
                                            for (QueryDocumentSnapshot snapshot:task.getResult()) {
                                                if (Email.getText().toString().equals(snapshot.getString("Email")))
                                                {
                                                    VerifyTailor=true;
                                                    if (snapshot.getString("ProfileStatus").equals("1"))
                                                    {
                                                        ProfileStatus=true;
                                                    }
                                                }
                                            }
                                            if (VerifyTailor)
                                            {
                                                if (ProfileStatus) {
                                                    progressDialog.dismiss();
                                                    SharedPreferences sharedPreferences=getSharedPreferences("Logout",MODE_PRIVATE);
                                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                                    editor.putString("Status","1");
                                                    editor.putString("Person","Tailor");
                                                    editor.commit();
                                                    startActivity(new Intent(LogIn_Activity.this, TailorNavigationDrawer_Activity.class));
                                                    finish();
                                                }
                                                else
                                                {
                                                    progressDialog.dismiss();
                                                    Intent intent1=new Intent(LogIn_Activity.this,Tailor_Profile_Acitivity.class);
                                                    if (ProfileStatus) {
                                                        intent1.putExtra("ProfileStatus", "1");
                                                    }
                                                    else
                                                    {
                                                        intent1.putExtra("ProfileStatus", "0");
                                                    }
                                                    startActivity(intent1);
                                                    finish();
                                                }
                                            }
                                            else
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(LogIn_Activity.this, "User not Exist", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LogIn_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void forgotPassword() {
        View forgotdialogview= LayoutInflater.from(LogIn_Activity.this).inflate(R.layout.forgotpassword_dialog,null);
        forgotemail=forgotdialogview.findViewById(R.id.forgotemail);
        forgotpasswordbtn=forgotdialogview.findViewById(R.id.forgotpasswordbtn);
        AlertDialog.Builder builder=new AlertDialog.Builder(LogIn_Activity.this);
        builder.setView(forgotdialogview);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        forgotpasswordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forgotemail.getText().toString().isEmpty())
                {
                    Toast.makeText(LogIn_Activity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(forgotemail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            alertDialog.dismiss();
                            Toast.makeText(LogIn_Activity.this, "Please Check your Email to Forgot Password", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            alertDialog.dismiss();
                            Toast.makeText(LogIn_Activity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}