package com.example.itoiletmobileapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminChangePasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminChangePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminChangePasswordFragment newInstance(String param1, String param2) {
        AdminChangePasswordFragment fragment = new AdminChangePasswordFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_change_password, container, false);

        //associate layout elements
        EditText passwordText = view.findViewById(R.id.passwordText3);
        EditText newPasswordText = view.findViewById(R.id.newPasswordText3);
        EditText verifyPasswordText = view.findViewById(R.id.verifyPasswordText3);
        Button button = view.findViewById(R.id.submitButton3);

        //handle button operations and error checking
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordText.getText().toString();
                String newPassword = newPasswordText.getText().toString();
                String verifyPassword = verifyPasswordText.getText().toString();
                DBHandler db = new DBHandler(requireContext());

                //Handle errors and make sure password is up to standard
                try {
                    if (!newPassword.equals(verifyPassword)) {
                        toast(requireContext(), "Passwords must match!");
                    } else if (!db.isAdminPassCorrect(password)) {
                        toast(requireContext(), "Wrong password entered!");
                    } else if (!db.checkPass(newPassword)) {
                        toast(requireContext(), "Password must consist of 8 or more characters and a number!");
                    } else {
                        db.updateAdminPass(newPassword);
                        toast(requireContext(), "Password changed successfully!");
                    }
                }
                //close DB
                finally {
                    if(db != null){
                        db.close();
                    }
                }
            }
        });

        return view;
    }
    private void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}