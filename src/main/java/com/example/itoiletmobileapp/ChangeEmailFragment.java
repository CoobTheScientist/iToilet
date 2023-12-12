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
 * Use the {@link ChangeEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeEmailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangeEmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangeEmailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeEmailFragment newInstance(String param1, String param2) {
        ChangeEmailFragment fragment = new ChangeEmailFragment();
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
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);

        //associate layout elements
        EditText emailText = view.findViewById(R.id.emailText);
        EditText newEmail = view.findViewById(R.id.newEmailText);
        EditText verifyEmail = view.findViewById(R.id.verifyEmailText);
        Button button = view.findViewById(R.id.submitBtn);

        //Handle button listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail(emailText, newEmail, verifyEmail);
            }
        });
        return view;
    }

    //Method to change users email
    private void changeEmail(EditText emailText, EditText newEmailText, EditText verifyEmailText){
        String email = emailText.getText().toString();
        String newEmail = newEmailText.getText().toString();
        String verifyEmail = verifyEmailText.getText().toString();
        DBHandler db = new DBHandler(requireContext());

        //Handle errors
        if(!newEmail.equals(verifyEmail)){
            toast(requireContext(), "ERROR: Emails must match! Please re-enter your new email!");
        }
        else if (db.userExists(newEmail)) {
            toast(requireContext(), "New email already in use!");
        }
        else if(!db.userExists(email)){
            toast(requireContext(), "Email does not exist!");
        }
        else{
            db.updateEmail(email, newEmail);
            toast(requireContext(), "Email successfully changed!");
            //close DB
            db.close();
        }
    }
    private void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}