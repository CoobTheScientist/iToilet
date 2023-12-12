package com.example.itoiletmobileapp;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.Manifest;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

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
    private FusedLocationProviderClient fusedLocationProviderClient;
    //set location permission code
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        //associate map to layout
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //click listener for location button
        FloatingActionButton locBtn = view.findViewById(R.id.locateBtn);
        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get permissions for location and show on the map
                if (googleMap != null) {
                    if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        googleMap.setMyLocationEnabled(true);

                        //get location
                        FusedLocationProviderClient fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(requireContext());
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null) {
                                    //show it on map
                                    LatLng userCoord = new LatLng(location.getLatitude(), location.getLongitude());
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCoord, 14f));
                                }

                            }
                        });

                    }
                    else{
                        //request if permissions not given
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    }

                }
            }
        });

        return view;
    }

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
        //add markers when map appears and call click operation
        googleMap = maps;
        googleMap.setOnMarkerClickListener(this);
        addMarkers(googleMap);
    }
    public void addMarkers(GoogleMap maps){
        googleMap = maps;

        DBHandler dbHandler = new DBHandler(requireContext());
        Cursor cursor = dbHandler.bathroomsRatings();

        //get index for cursor
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

        if(addIndex==-1 || locIndex==-1 || genIndex==-1 || idIndex==-1 || ovrIndex==-1 || cleanIndex==-1 ||
                tpIndex==-1 || handIndex==-1 || dryIndex==-1 || babyIndex==-1){
            toast(requireContext(), "Failed to load markers");
            cursor.close();
            return;
        }

        //read through cursor initialize bathroom object
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

                //get geocoded output from geocode class and add the marker to the map
                new GeoCode(new GeoCode.GeocodingListener() {
                    @Override
                    public void onGeocodeOutput(LatLng output) {
                        MarkerOptions markerOptions = new MarkerOptions().position(output).title(add).snippet("Location: " + loc +
                                "\nGender: " + genType + "\nOverall: " + ovr + "\nCleanliness: " + clean + "\nToilet Paper: " + tp +
                                "\nHandicap: " + handicap + "\nDrying: " + dry + "\nBaby Station: " + baby);
                        //add marker to map
                        Marker marker = googleMap.addMarker(markerOptions);


                        marker.setTag(bathroom);
                    }
                    //do this for the address
                }, requireContext()).execute(add);
            }
        }
        cursor.close();
    }
// handle when marker is clicked
    @Override
    public boolean onMarkerClick(Marker marker){
        Bathroom bathroom = (Bathroom) marker.getTag();
        openWindow(bathroom);
        LatLng choice = marker.getPosition();

        getLocation(choice);
        return true;
    }
    //get location method for directions
    private LatLng getLocation(LatLng choice){
        try {
            //get location
            Task<Location> locTask = fusedLocationProviderClient.getLastLocation();
            locTask.addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //get directions from location to destination
                    if (location != null) {
                        LatLng user = new LatLng(location.getLatitude(), location.getLongitude());

                        getDirections(user, choice);
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return new LatLng(0, 0);
    }

    //method to open dialog window
    private void openWindow(Bathroom bathroom){
        View infoView = getLayoutInflater().inflate(R.layout.info_window, null);

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

        addressText.setText(bathroom.getAddress());
        locText.setText(bathroom.getLocation());
        genText.setText(bathroom.getGender());
        ovrText.setText(String.valueOf(bathroom.getOverall()));
        cleanText.setText(String.valueOf(bathroom.getCleanliness()));
        tpText.setText(String.valueOf(bathroom.getToiletPaper()));
        handicapText.setText(bathroom.getHandicap());
        dryText.setText(bathroom.getDrying());
        babyText.setText(bathroom.getBabyStation());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(infoWindow.isShowing()){
                    infoWindow.dismiss();
                }
            }
        });

        infoWindow = new Dialog(requireContext());
        infoWindow.setContentView(infoView);
        infoWindow.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoWindow.show();
    }

    //methof to
    private void getDirections(LatLng start, LatLng end){
        //get url for directions api
        String url = getNavURL(start, end);

        //send lat and lng to direction api class
        DirectionsAPI directions = new DirectionsAPI();
        directions.execute(url);

    }
    //method containing url for directions
    private String getNavURL(LatLng start, LatLng end){
        String startStep = "origin=" + start.latitude + "," + start.longitude;
        String endStep = "destination=" + end.latitude + "," + end.longitude;
        String travel = "mode=walking";
        String request = "https://maps.googleapis.com/maps/api/directions/json?" +
                startStep + "&" + endStep + "&" + travel;

        request = request + "&key=" + getKey();
        return request;
    }

    private void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    //method to get api key for url
    private String getKey(){
        return "AIzaSyAoS8qrfTY9wUqZvOu3nU7wYnBKzFVDLyA";
    }

    //embedded class to get directions from google maps directions api
    public class DirectionsAPI extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url){
            String info = "";
            try{
                info = getUrl(url[0]);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return info;
        }
        @Override
        protected void onPostExecute(String end){
            super.onPostExecute(end);
            //parse through response from api
            JSONparse parse = new JSONparse();
            parse.execute(end);
        }
        //method to connect and read info from api
        private String getUrl(String firstUrl) throws IOException {
            String info = "";
            InputStream inputStream = null;
            HttpURLConnection connection = null;

            //establish connection and record response
            try{
                URL secUrl = new URL(firstUrl);
                connection = (HttpURLConnection) secUrl.openConnection();
                connection.connect();

                inputStream = connection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder str = new StringBuilder();
                String record;

                while((record = buffer.readLine()) != null){
                    str.append(record);
                }
                info = str.toString();
                buffer.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally {
                if(inputStream != null){
                    inputStream.close();
                }
                if(connection != null){
                    connection.disconnect();
                }
            }
            return info;

        }
    }

    //embedded class to parse through json Data response and get directions
    public class JSONparse extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        protected List<List<HashMap<String, String>>> doInBackground(String... info){
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> direction = null;

            //parse through with direct class
            try{
                jsonObject = new JSONObject(info[0]);
                JSONdirect direct = new JSONdirect();

                direction = direct.parse(jsonObject);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return direction;
        }
        protected  void onPostExecute(List<List<HashMap<String, String>>> ending){
            ArrayList<LatLng> spot = new ArrayList<>();
            PolylineOptions line = new PolylineOptions();

            //iterate through results to build polyline
            for(int i = 0; i < ending.size(); i++){
                List<HashMap<String, String>> list = ending.get(i);
                for(int a = 0; a < list.size(); a++){
                    HashMap<String, String> route = list.get(a);
                    double lat = Double.parseDouble(route.get("lat"));
                    double lng = Double.parseDouble(route.get("lng"));
                    LatLng pinpoint = new LatLng(lat, lng);
                    spot.add(pinpoint);
                }
            }

            line.addAll(spot);
            line.width(13);
            line.color(Color.GREEN);

            //show line on google maps
            if(googleMap != null){
                googleMap.addPolyline(line);
            }
            else{
                toast(requireContext(), "Failed to load directions...");
            }

        }

    }
    //json class to read polyline coordinates
    public class JSONdirect {
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<>();
            JSONArray JSONRoutes;
            JSONArray JSONLen;
            JSONArray JSONSteps;

            try {
                //iterate through route array json object
                JSONRoutes = jObject.getJSONArray("routes");

                for (int i = 0; i < JSONRoutes.length(); i++) {
                    JSONLen = ((JSONObject) JSONRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList<>();

                    //iterate through leg
                    for (int a = 0; a < JSONLen.length(); a++) {
                        JSONSteps = ((JSONObject) JSONLen.get(a)).getJSONArray("steps");

                        //iterate through steps and get info for each
                        for (int b = 0; b < JSONSteps.length(); b++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) JSONSteps.get(b)).get("polyline")).get("points");
                            List<LatLng> list = interpretPoly(polyline);

                            // coordinates for each step
                            for (int c = 0; c < list.size(); c++) {
                                HashMap<String, String> hash = new HashMap<>();
                                hash.put("lat", Double.toString((list.get(c)).latitude));
                                hash.put("lng", Double.toString((list.get(c)).longitude));
                                path.add(hash);
                            }
                        }
                        routes.add(path);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        //This method decodes polylines for the map
        //modified from google maps api documentation
        private List<LatLng> interpretPoly(String line) {
            List<LatLng> polyLine = new ArrayList<>();
            //initialize variables
            int i = 0;
            int polyLength = line.length();
            int Lat = 0;
            int Lng = 0;

            //loop to go through the line value
            while (i < polyLength) {
                int value;
                int shift = 0;
                int end = 0;

                //decode lat using hexdecimal to represent bits
                do {
                    value = line.charAt(i++) - 63;
                    end |= (value & 0x1F) << shift;
                    shift += 5;
                } while (value >= 0x20);

                //update lat
                //int decodedLat = ((end & 1) != 0 ? ~(end >> 1) : (end >> 1));
                int decodedLat;
                if((end & 1) != 0){
                    decodedLat = ~(end >> 1);
                }
                else{
                    decodedLat = end >> 1;
                }
                Lat += decodedLat;

                shift = 0;
                end = 0;

                //decode longitude using hexdecimal
                do {
                    value = line.charAt(i++) - 63;
                    end |= (value & 0x1F) << shift;
                    shift += 5;
                } while (value >= 0x20);

                //update longitude
                //int decodedLng = ((end & 1) != 0 ? ~(end >> 1) : (end >> 1));
                int decodedLng;
                if((end & 1) != 0){
                    decodedLng = ~(end >> 1);
                }
                else{
                    decodedLng = end >> 1;
                }
                Lng += decodedLng;

                LatLng p = new LatLng(((double) Lat / 1E5), ((double) Lng / 1E5));
                polyLine.add(p);
            }
            return polyLine;
        }
    }
}