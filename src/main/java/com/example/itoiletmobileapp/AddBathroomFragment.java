package com.example.itoiletmobileapp;

import android.content.Context;
import android.media.Rating;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBathroomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBathroomFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddBathroomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddBathroomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddBathroomFragment newInstance(String param1, String param2) {
        AddBathroomFragment fragment = new AddBathroomFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_bathroom, container, false);

        //associate the layout elements
        EditText address = view.findViewById(R.id.address);
        EditText location = view.findViewById(R.id.location);
        EditText genderType = view.findViewById(R.id.genderType);
        RatingBar overall = view.findViewById(R.id.overall);
        RatingBar cleanliness = view.findViewById(R.id.cleanliness);
        RatingBar toiletPaper = view.findViewById(R.id.toiletPaper);
        RadioGroup handicap = view.findViewById(R.id.handicap);
        RadioGroup babyStation = view.findViewById(R.id.changingStation);
        RadioGroup methodDrying = view.findViewById(R.id.dryingHands);
        Button submit = view.findViewById(R.id.submit);

        //Submit button click operations
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String addressGiven = address.getText().toString();
                String locationGiven = location.getText().toString();
                String genderTypeGiven = genderType.getText().toString();
                int overallGiven = (int) overall.getRating();
                int cleanlinessGiven = (int) cleanliness.getRating();
                int toiletPaperGiven = (int) toiletPaper.getRating();
                String handicapGiven = getRadioValue(handicap);
                String babyStationGiven = getRadioValue(babyStation);
                String methodDryingGiven = getRadioValue(methodDrying);


                //Send to bathrooms
                try {
                    DBHandler db = new DBHandler(requireContext());
                    db.addBathroom(addressGiven, locationGiven, genderTypeGiven);
                    //Get bathroomID for ratings table
                    int bathroomID = db.getLastBathroom();
                    db.addRating(bathroomID, LoginActivity.getUserID(),cleanlinessGiven, toiletPaperGiven, overallGiven,
                            handicapGiven, methodDryingGiven, babyStationGiven);
                    //log test for data in Rating table
                    db.logPendingTable();
                    db.close();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("MyApp", "Exception Occurred", e);
                }
                finally{
                    //clear screen once complete
                    toast(requireContext(), "Thanks for your addition!");
                    clearScreen();
                }
            }
        });

        return view;
    }

    //Interpret choice of radio ratings
    private String getRadioValue(RadioGroup radio){
        int sent = radio.getCheckedRadioButtonId();
        if (sent != -1){
            RadioButton choice = radio.findViewById(sent);
            return choice.getText().toString();
        }
        else {
            return "";
        }
    }

    private void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    //Method to clear screen when user submits a bathroom successfully
    private void clearScreen(){

        EditText address = requireView().findViewById(R.id.address);
        EditText location = requireView().findViewById(R.id.location);
        EditText genderType = requireView().findViewById(R.id.genderType);
        RatingBar overall = requireView().findViewById(R.id.overall);
        RatingBar cleanliness = requireView().findViewById(R.id.cleanliness);
        RatingBar toiletPaper = requireView().findViewById(R.id.toiletPaper);
        RadioGroup handicap = requireView().findViewById(R.id.handicap);
        RadioGroup babyStation = requireView().findViewById(R.id.changingStation);
        RadioGroup methodDrying = requireView().findViewById(R.id.dryingHands);

        //clear all elements
        address.setText("");
        location.setText("");
        genderType.setText("");
        overall.setRating(0);
        cleanliness.setRating(0);
        toiletPaper.setRating(0);
        handicap.clearCheck();
        babyStation.clearCheck();
        methodDrying.clearCheck();
    }
}