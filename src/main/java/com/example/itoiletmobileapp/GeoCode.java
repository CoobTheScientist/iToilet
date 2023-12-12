package com.example.itoiletmobileapp;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import android.content.Context;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GeoCode extends AsyncTask<String, Void, LatLng> {
    private String key;
    private Context context;
    private GeocodingListener listener;

    //constructor
    public GeoCode(GeocodingListener listener, Context context) {
        this.key = getKey();
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected LatLng doInBackground(String... params) {
        if (params.length == 0){
            return null;
        }
        String add = params[0];
        LatLng output = geoTask(add);
        return output;
    }

    @Override
    protected void onPostExecute(LatLng output) {
        if(listener != null) {
            listener.onGeocodeOutput(output);
        }
    }

    private LatLng geoTask(String add) {
        //url to google geocoding api
        String geoURL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + add + "&key=" + key;

        //get http connection to send info to api
        try{
            URL apiUrl = new URL(geoURL);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            //read from address and send to url
            try{
                InputStream input = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

                StringBuilder str = new StringBuilder();
                String entry;
                while ((entry = bufferedReader.readLine()) != null){
                    str.append(entry).append("\n");
                }

                String res = str.toString();
                return GeocodeResult(res);
            }
            catch(IOException e2){
                e2.printStackTrace();
            }
            finally {
                connection.disconnect();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    //google api key
    private String getKey(){
        return *REFERENCE API KEY HERE*;
    }

    //make json object and get latitude and longitude
    private LatLng GeocodeResult(String input){
        try{
            JSONObject jsonObject = new JSONObject(input);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            if(jsonArray.length() > 0) {
                JSONObject locObject = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                double lat = locObject.getDouble("lat");
                double lng = locObject.getDouble("lng");

                return new LatLng(lat, lng);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public interface GeocodingListener {
        void onGeocodeOutput(LatLng output);
    }
}
