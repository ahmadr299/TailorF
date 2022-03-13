package com.example.mytailor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Customer_Due_Orders_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Customer_Due_Orders_Fragment extends Fragment {
    private RecyclerView recyclerView_customerComplete_order;
    private TextView tv_customerComplete_order;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String ShopName, CustomerName, Category, Phone, Address, OrderNo, Date;
    private List<Tailor_new_ordersModelClass> list = new ArrayList<>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int j = 0;
    private int Orders = 0;
    private String Measurements;

    public Customer_Due_Orders_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Customer_Due_Orders_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Customer_Due_Orders_Fragment newInstance(String param1, String param2) {
        Customer_Due_Orders_Fragment fragment = new Customer_Due_Orders_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_customer__due__orders_, container, false);
        tv_customerComplete_order = view.findViewById(R.id.tv_customerComplete_order);
        recyclerView_customerComplete_order = view.findViewById(R.id.recyclerView_customerComplete_order);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore.collection("Orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Log.e("Loop", "Outer");
                            if (firebaseAuth.getCurrentUser().getEmail().equals(snapshot.getString("CustomerEmail"))) {
                                Log.e("Match", "Email");
                                if (snapshot.getString("Status").equals("3")) {
                                    Log.e("Match", "Status");
                                    OrderNo = snapshot.getString("OrderID");
                                    Category = snapshot.getString("MainCategory") + ", " + snapshot.getString("SubCategory");
                                    Date = snapshot.getString("DueDate");
                                    ShopName = snapshot.getString("ShopName");
                                    Address = snapshot.getString("ShopAddress");
                                    Phone = snapshot.getString("TailorPhone");
                                    Measurements=snapshot.getString("Measurements");
                                    Orders++;
                                    Log.e("Order", OrderNo);
                                    Log.e("Category", Category);
                                    Log.e("Date", Date);
                                    Tailor_new_ordersModelClass modelClass = new Tailor_new_ordersModelClass();
                                    modelClass.setAddress(Address);
                                    modelClass.setCategory(Category);
                                    modelClass.setCustomerName(ShopName);
                                    modelClass.setDate(Date);
                                    modelClass.setOrderNo(OrderNo);
                                    modelClass.setPhone(Phone);
                                    modelClass.setCustomerEmail(snapshot.getString("CustomerEmail"));
                                    modelClass.setMeasurements(Measurements);
                                    list.add(modelClass);
                                    Log.e("Address", list.get(j).getAddress());
                                    j++;
                                }
                            }
                        }
                        Log.e("ListSize", list.size() + "");
                        if (Orders>0) {
                            Tailor_Active_OrdersAdapter adapter = new Tailor_Active_OrdersAdapter(list, view.getContext(),"Customer Complete Order");
                            recyclerView_customerComplete_order.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
                            recyclerView_customerComplete_order.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            tv_customerComplete_order.setVisibility(View.VISIBLE);
                            recyclerView_customerComplete_order.setVisibility(View.GONE);
                            tv_customerComplete_order.setText("No New Order Yet");
                        }
                    } else {
                        Toast.makeText(view.getContext(), "DataBase Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}