package com.delarocha.mapsappdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
//import com.google.android.gms.maps.GoogleMap.On;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.data.geojson.GeoJsonLayer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BasicMapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private MapFragment mFragment;
    ArrayList markerPoints = new ArrayList();
    private FloatingActionButton fab;
    String parsedDistance = "";
    String parsedDuration = "";
    String parsedEnd_Address = "";
    String parsedStart_Address = "";
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocation;
    private Object mLastKnownLocation;
    LatLng mtyMx;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private Parcelable mCameraPosition, mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }*/

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);
        //fab = (FloatingActionButton) findViewById(R.id.floatingBtn);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(37.4233438, -122.0728817))
                                .title("LinkedIn")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(37.4629101, -122.2449094))
                                .title("Facebook")
                                .snippet("Facebook HQ: Menlo Park"));

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(37.3092293, -122.1136845))
                                .title("Apple"));

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.4233438, -122.0728817), 10));
                    }
                });
            }
        });*/
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mtyMx = new LatLng(25.6866142, -100.3161126);
        updateLocationUI();
        getDeviceLocation();
        //mMap.setMyLocationEnabled(true);
        //mMap.setOnMyLocationButtonClickListener(this);
        //getDeviceLocation();
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mtyMx,16));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(markerPoints.size()>1){
                    markerPoints.clear();
                    mMap.clear();
                }

                markerPoints.add(latLng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);

                if(markerPoints.size()==1){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else if(markerPoints.size()==2){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                mMap.addMarker(options);
                // Checks, whether start and end locations are captured
                if(markerPoints.size()>=2){
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng destination = (LatLng) markerPoints.get(1);
                    //Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, destination);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }
            }
        });
    }

    private void getDeviceLocation() {
        /*
        * Get the best and most recent location of the device, which may be null in rare
        * cases when a location is not available.
        */
        try {
            if(mLocationPermissionGranted){
                @SuppressLint("MissingPermission") Task locationResult = mFusedLocation.getLastLocation();
                mLastKnownLocation = locationResult.getResult();
                locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //mLastKnownLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),16));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mtyMx,16));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String...url) {
            String data = "";
            try{
                data = downloadUrl(url[0]);
            }catch (Exception e){e.printStackTrace();}
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            serializeMapsResponse(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private void serializeMapsResponse(String result) {

        try {


            JSONObject jsonObject = new JSONObject(result);
            JSONObject geoJSON = new JSONObject(result);
            GeoJsonLayer layer = new GeoJsonLayer(mMap,geoJSON);
            JSONArray routes = jsonObject.getJSONArray("routes");
            //JSONObject routes = array.getJSONObject(1);
            JSONObject routesIndexCero = routes.getJSONObject(0);
            JSONArray legs = routesIndexCero.getJSONArray("legs");
            JSONObject legsIndexCero = legs.getJSONObject(0);
            JSONObject distance = legsIndexCero.getJSONObject("distance");
            JSONObject duration = legsIndexCero.getJSONObject("duration");
            parsedEnd_Address = legsIndexCero.getString("end_address");
            parsedStart_Address = legsIndexCero.getString("start_address");
            parsedDistance=distance.getString("text");
            parsedDuration = duration.getString("text");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        
    }

    private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String, String>>>>{

        @Override
        protected  List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try {

                jsonObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jsonObject);

            }catch (Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for(int i = 0; i<lists.size();i++){
                points = new ArrayList();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = lists.get(i);

                for(int j = 0; j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat,lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);
            }
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String downloadUrl(String StrUrl) throws IOException{

        String data = "";
        String response = "";
        InputStream IStream = null;
        HttpURLConnection urlConnection = null;
        //HttpURLConnection urlConnection1 = null;
        try{
            URL url = new URL(StrUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection1 = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            //urlConnection1.setRequestMethod("POST");
            IStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(IStream));
            StringBuffer sb = new StringBuffer();
            String line =  "";
            //response = org.apache.commons.io.IOUtils.toString(IStream, "UTF-8");

            //JSONObject jsonObject = new JSONObject(response);
            //JSONArray array = jsonObject.getJSONArray("routes");
            //JSONObject routes = array.getJSONObject(0);
            //JSONArray legs = routes.getJSONArray("legs");
            //JSONObject steps = legs.getJSONObject(0);
            //JSONObject distance = steps.getJSONObject("distance");
            //parsedDistance=distance.getString("text");

            while ((line = br.readLine())!=null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            IStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getDirectionsUrl(LatLng origin, LatLng destination) {
        //Origin of the route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        //Destination of the route
        String str_dest = "destination="+destination.latitude+","+destination.longitude;
        //Sensor enabled
        String sensor = "sensor=false";
        String units = "units=metric";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"+ units +"&" + mode;

        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext()
                ,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED&&
                (ContextCompat.checkSelfPermission(this.getApplicationContext()
            ,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
            mLocationPermissionGranted = true;
        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                }
        }
        updateLocationUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState);
        if(mMap!=null){
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, (Parcelable) mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
}
