package com.example.mytailor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Tailor_Orders_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tailor_Orders_Fragment extends Fragment {
    TabLayout tailor_tab_layout;
    ViewPager tailor_view_pager;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Tailor_Orders_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tailor_Orders_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Tailor_Orders_Fragment newInstance(String param1, String param2) {
        Tailor_Orders_Fragment fragment = new Tailor_Orders_Fragment();
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
        View view=inflater.inflate(R.layout.fragment_tailor__orders_, container, false);
        tailor_view_pager=view.findViewById(R.id.tailor_view_pager);
        tailor_tab_layout=view.findViewById(R.id.tailor_tab_layout);
        MyViewPagerAdapter myViewPagerAdapter= new MyViewPagerAdapter(getChildFragmentManager());
        myViewPagerAdapter.addFragment(new Tailor_Active_Orders_Fragment(), "ACTIVE");
        myViewPagerAdapter.addFragment(new Tailor_Due_Orders_Fragment(), "Past DUE");
        myViewPagerAdapter.addFragment(new Orders_Notification_Fragment(), "New");
        tailor_view_pager.setAdapter(myViewPagerAdapter);
        tailor_tab_layout.setupWithViewPager(tailor_view_pager);
        return view;
    }
}