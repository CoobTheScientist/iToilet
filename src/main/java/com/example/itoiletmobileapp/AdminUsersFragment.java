package com.example.itoiletmobileapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUsersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private UserRecycleAdapter userRecycleAdapter;
    private DBHandler db;

    public AdminUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUsersFragment newInstance(String param1, String param2) {
        AdminUsersFragment fragment = new AdminUsersFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        // associate elements and initialize DB
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewUsers);
        Button deleteBtn = view.findViewById(R.id.btnDeleteUser);
        db = new DBHandler(getActivity());

        //Load users to recycler view
        List<User> list = db.getUsers();
        userRecycleAdapter = new UserRecycleAdapter(list, db);
        recyclerView.setAdapter(userRecycleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //handle delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userRecycleAdapter != null){
                    userRecycleAdapter.deleteHighlighted();
                }
            }
        });

        return view;
    }

    // close db to prevent leaks
    public void onDestroy(){
        super.onDestroy();
        if(db != null){
            db.close();
        }
    }
}