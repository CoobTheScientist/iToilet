package com.example.itoiletmobileapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MapView mapView;
    private GoogleMap googleMap;

    private Dialog infoWindow;

    //set location permission code
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        //Associate map to layout element
        mapView = view.findViewById(R.id.adminMapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //click listener for get location button
        FloatingActionButton locBtn = view.findViewById(R.id.locBtn);
        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make sure permissions are allowed and google maps is active
                if (googleMap != null) {
                    if(ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        googleMap.setMyLocationEnabled(true);

                        //Get Location of device and show it on the mapview
                        FusedLocationProviderClient fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(requireContext());
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location loc) {
                                if(loc != null) {
                                    LatLng userCoord = new LatLng(loc.getLatitude(), loc.getLongitude());
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCoord, 14f));
                                }

                            }
                        });

                    }
                    //request permissions if not already allowed
                    else{
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                }
            }
        });

        return view;
    }

    //Override control methods to affect mapView
    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap maps){
        googleMap = maps;
        googleMap.setOnMarkerClickListener(this);
        addMarkers(googleMap);
    }
    //method to put markers of teh bathroom on the map
    public void addMarkers(GoogleMap maps){
        googleMap = maps;

        //Get Bathroom and rating information
        DBHandler dbHandler = new DBHandler(requireContext());
        Cursor cursor = dbHandler.bathroomsRatings();

        //get indexes for columns to feed cursor
        int addIndex = cursor.getColumnIndex(DBHandler.COLUMN_BATHROOM_ADDRESS);
        int locIndex = cursor.getColumnIndex(DBHandler.COLUMN_BATHROOM_LOCATION);
        int genIndex = cursor.getColumnIndex(DBHandler.COLUMN_BATHROOM_GENDER_TYPE);
        int idIndex = cursor.getColumnIndex(DBHandler.COLUMN_BATHROOM_ID);
        int ovrIndex = cursor.getColumnIndex(DBHandler.COLUMN_OVERALL);
        int cleanIndex = cursor.getColumnIndex(DBHandler.COLUMN_CLEANLINESS);
        int tpIndex = cursor.getColumnIndex(DBHandler.COLUMN_TOILET_PAPER);
        int handIndex = cursor.getColumnIndex(DBHandler.COLUMN_HANDICAP_ACCESSIBILITY);
        int dryIndex = cursor.getColumnIndex(DBHandler.COLUMN_METHOD_OF_DRYING);
        int babyIndex = cursor.getColumnIndex(DBHandler.COLUMN_BABY_STATION);

        //make sure everything has a correct index
        if(addIndex==-1 || locIndex==-1 || genIndex==-1 || idIndex==-1 || ovrIndex==-1 || cleanIndex==-1 ||
                tpIndex==-1 || handIndex==-1 || dryIndex==-1 || babyIndex==-1){
            toast(requireContext(), "Failed to load markers");
            cursor.close();
            return;
        }

        //move through columns
        while(cursor.moveToNext()){
            if(addIndex>=0 && locIndex>=0 && genIndex>=0){
                String add = cursor.getString(addIndex);
                String loc = cursor.getString(locIndex);
                String genType = cursor.getString(genIndex);
                int bathroomId = cursor.getInt(idIndex);
                int ovr = cursor.getInt(ovrIndex);
                int clean = cursor.getInt(cleanIndex);
                int tp = cursor.getInt(tpIndex);
                String handicap = cursor.getString(handIndex);
                String dry = cursor.getString(dryIndex);
                String baby = cursor.getString(babyIndex);

                Bathroom bathroom = new Bathroom(bathroomId, add, loc, genType, ovr, clean, tp, handicap, dry, baby);

                //get LatLng for markers
                new GeoCode(new GeoCode.GeocodingListener() {
                    @Override
                    public void onGeocodeOutput(LatLng output) {
                        MarkerOptions markerOptions = new MarkerOptions().position(output).title(add).snippet("Location: " + loc + "\nGender: " + genType);
                        Marker marker = googleMap.addMarker(markerOptions);


                        marker.setTag(bathroom);
                    }
                }, requireContext()).execute(add);
            }
        }
        cursor.close();
    }

    //override click to get bathroom info and open dialog window
    @Override
    public boolean onMarkerClick(Marker marker){
        Bathroom bathroom = (Bathroom) marker.getTag();
        openWindow(bathroom);
        return true;
    }

    //method to open bathroom info window
    private void openWindow(Bathroom bathroom){
        View infoView = getLayoutInflater().inflate(R.layout.info_window, null);

        // associate layout elements
        TextView addressText = infoView.findViewById(R.id.add);
        TextView locText = infoView.findViewById(R.id.loc);
        TextView genText = infoView.findViewById(R.id.gen);
        TextView ovrText = infoView.findViewById(R.id.ovr);
        TextView cleanText = infoView.findViewById(R.id.clean);
        TextView tpText = infoView.findViewById(R.id.tp);
        TextView handicapText = infoView.findViewById(R.id.handi);
        TextView dryText = infoView.findViewById(R.id.dry);
        TextView babyText = infoView.findViewById(R.id.baby);
        ImageButton close = infoView.findViewById(R.id.closeButton);

        //set values in elements
        addressText.setText(bathroom.getAddress());
        locText.setText(bathroom.getLocation());
        genText.setText(bathroom.getGender());
        ovrText.setText(String.valueOf(bathroom.getOverall()));
        cleanText.setText(String.valueOf(bathroom.getCleanliness()));
        tpText.setText(String.valueOf(bathroom.getToiletPaper()));
        handicapText.setText(bathroom.getHandicap());
        dryText.setText(bathroom.getDrying());
        babyText.setText(bathroom.getBabyStation());
        //close button listener
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(infoWindow.isShowing()){
                    infoWindow.dismiss();
                }
            }
        });

        //show the info window
        infoWindow = new Dialog(requireContext());
        infoWindow.setContentView(infoView);
        infoWindow.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoWindow.show();
    }

    private void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}