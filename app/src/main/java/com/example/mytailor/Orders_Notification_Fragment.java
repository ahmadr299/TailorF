package com.example.mytailor;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Orders_Notification_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Orders_Notification_Fragment extends Fragment implements CallBackFragment{
    private FirebaseFirestore firestore;
    private TextView tv_new_order;
    private RecyclerView new_order_recyclerView;
    private FirebaseAuth firebaseAuth;
    private String ShopName, CustomerName, Category, Phone, Address, OrderNo, Date;
    private List<Tailor_new_ordersModelClass> list = new ArrayList<>();
    private CallBackFragment callBackFragment=(CallBackFragment)this;
    private Tailor_new_ordersAdapter adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String OwnerName;
    private int Order=0;
    private String Measurements;

    public Orders_Notification_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Orders_Notification_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Orders_Notification_Fragment newInstance(String param1, String param2) {
        Orders_Notification_Fragment fragment = new Orders_Notification_Fragment();
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
        View view = inflater.inflate(R.layout.fragment_orders__notification_, container, false);
        tv_new_order = view.findViewById(R.id.tv_new_order);
        new_order_recyclerView = view.findViewById(R.id.new_order_recylerView);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore.collection("TailorsProfile").document(firebaseAuth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()) {
                    ShopName = value.getString("Shop");
                    firestore.collection("Orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                list.clear();
                                if (task.getResult().size()>0)
                                {
                                    for (QueryDocumentSnapshot snapshot:task.getResult())
                                    {
                                        if (ShopName.equals(snapshot.getString("ShopName")))
                                        {
                                            if (snapshot.getString("Status").equals("0"))
                                            {
                                                Tailor_new_ordersModelClass modelClass = new Tailor_new_ordersModelClass();
                                                CustomerName = snapshot.getString("CustomerName");
                                                OrderNo = snapshot.getString("OrderID");
                                                Category = snapshot.getString("MainCategory") + ", " + snapshot.getString("SubCategory");
                                                Address = snapshot.getString("Address");
                                                Date = snapshot.getString("DueDate");
                                                Phone=snapshot.getString("Phone");
                                                Measurements=snapshot.getString("Measurements");
                                                modelClass.setAddress(Address);
                                                modelClass.setCategory(Category);
                                                modelClass.setCustomerName(CustomerName);
                                                modelClass.setDate(Date);
                                                modelClass.setOrderNo(OrderNo);
                                                modelClass.setPhone(Phone);
                                                modelClass.setCustomerEmail(snapshot.getString("CustomerEmail"));
                                                modelClass.setMeasurements(Measurements);
                                                list.add(modelClass);
                                                Order++;
                                            }
                                        }
                                    }
                                    if (Order>0)
                                    {
                                        adapter = new Tailor_new_ordersAdapter(list, view.getContext(),callBackFragment);
                                        new_order_recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
                                        new_order_recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                    else
                                    {
                                        tv_new_order.setVisibility(View.VISIBLE);
                                        new_order_recyclerView.setVisibility(View.GONE);
                                        tv_new_order.setText("No New Order Yet");
                                    }
                                }
                                else
                                {
                                    Toast.makeText(view.getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(view.getContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void OnCallBack(int position) {
        list.remove(position);
        adapter.notifyDataSetChanged();
    }
}