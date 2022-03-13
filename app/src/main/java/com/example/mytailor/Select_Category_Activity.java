package com.example.mytailor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Select_Category_Activity extends AppCompatActivity {
    private Spinner category,category6;
    private List<String> MainCategoriesList=new ArrayList<>();
    private List<String> SubCategoriesList=new ArrayList<>();
    private LinearLayout innerLayout;
    private String MainCategory,SubCategory,ShopName;
    private FirebaseFirestore firestore;
    private int noofCategories;
    private ImageView back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__category_);
        assignIds();
        subCategories();
        SharedPreferences sharedPreferences=getSharedPreferences("Shop", Context.MODE_PRIVATE);
        ShopName=sharedPreferences.getString("ShopName","Tailor");
        Log.e("Shop Name",ShopName);
        mainCategories();
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Select_Category_Activity.this,Place_Order_Activity.class));
                finish();
            }
        });
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (MainCategoriesList.get(position).equals("Male Stitching") ||
                        MainCategoriesList.get(position).equals("Female Stitching") ||
                        MainCategoriesList.get(position).equals("Boy Stitching") ||
                        MainCategoriesList.get(position).equals("Girl Stitching"))
                {
                   innerLayout.setVisibility(View.VISIBLE);
                   MainCategory=MainCategoriesList.get(position);
                   ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<>(Select_Category_Activity.this,R.layout.spinner_design,R.id.spinnerText,SubCategoriesList);
                   category6.setAdapter(arrayAdapter1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Select_Category_Activity.this, "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        });
        category6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(SubCategoriesList.get(position).equals("Pant") ||
                SubCategoriesList.get(position).equals("Kurta")) {
                    SubCategory = SubCategoriesList.get(position);
                    Intent intent = new Intent(Select_Category_Activity.this, Customer_Order_Activity.class);
                    intent.putExtra("Main", MainCategory);
                    intent.putExtra("Sub", SubCategory);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void subCategories() {
        SubCategoriesList.add("Select Item");
        SubCategoriesList.add("Kurta");
        SubCategoriesList.add("Pant");
    }

    private void mainCategories() {
        firestore.collection("TailorsProfile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    MainCategoriesList.add("Select Category");
                    if (task.getResult().size()>0)
                    {
                        for (QueryDocumentSnapshot snapshot:task.getResult())
                        {
                            if (ShopName.equals(snapshot.getString("Shop")))
                            {
                                noofCategories=Integer.parseInt(snapshot.getString("Categories"));
                                for (int i=0;i<noofCategories;i++)
                                {
                                    MainCategoriesList.add(snapshot.getString("Category"+(i+1)));
                                }
                            }
                        }
                        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(Select_Category_Activity.this,R.layout.spinner_design,R.id.spinnerText,MainCategoriesList);
                        category.setAdapter(arrayAdapter);
                    }
                    else
                    {
                        Toast.makeText(Select_Category_Activity.this, "Tailors Profile is Empty", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Select_Category_Activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void assignIds() {
        category=findViewById(R.id.category);
        category6=findViewById(R.id.category6);
        innerLayout=findViewById(R.id.innerLayout);
        firestore=FirebaseFirestore.getInstance();
        back_button=findViewById(R.id.back_button);
    }
}