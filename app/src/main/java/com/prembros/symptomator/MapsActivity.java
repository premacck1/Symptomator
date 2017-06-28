package com.prembros.symptomator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;

    private double mLatitude=0;
    private double mLongitude=0;
    private boolean showNearest;
    static boolean isHospital;

    private final HashMap<String, String> mMarkerPlaceLink = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        showNearest = getIntent().getExtras().getBoolean("showNearest");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            if (isHospital) {
                if (showNearest) {
                    actionBar.setTitle(R.string.nearest_hospital);
                } else actionBar.setTitle(R.string.nearby_hospitals);
            } else {
                if (showNearest) {
                    actionBar.setTitle(R.string.nearest_doctor);
                } else actionBar.setTitle(R.string.nearby_doctors);
            }
        }

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(this);

        if(status!=ConnectionResult.SUCCESS) {                                   // Google Play Services are not available
            if(googleAPI.isUserResolvableError(status)) {
                googleAPI.getErrorDialog(this, status, 10000).show();
            }
        } else {                                                                 // Google Play Services are available
            if (isConnected()) {
                SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                fragment.getMapAsync(this);

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);

                Location location = null;
                if (checkLocationPermission()) {
                    location = locationManager.getLastKnownLocation(provider);
                }
                if (location != null) {
                    onLocationChanged(location);
                }
                locationManager.requestLocationUpdates(provider, 20000, 0, this);

                isHospital = getIntent().getStringExtra("showWhat").equals("hospital");

                showNearbyPlaces();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getString(R.string.not_connected))
                        .setMessage(getString(R.string.not_connected_message))
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MapsActivity.this.finish();
                            }
                        })
                        .show();
            }
        }
    }

    private boolean isConnected() {
        ConnectivityManager conman = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conman.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void showNearbyPlaces() {
        if (isHospital) {
            String sb = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLatitude + "," + mLongitude +
                    "&radius=10000" +
                    "&types=hospital" +
                    "&sensor=true" +
                    "&key=AIzaSyC8JIhbRFb1uVOqawgaHP8Dr9goMIVey7k";

            new PlacesTask().execute(sb);
        } else {
            String sb = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLatitude + "," + mLongitude +
                    "&radius=10000" +
                    "&types=doctor" +
                    "&sensor=true" +
                    "&key=AIzaSyC8JIhbRFb1uVOqawgaHP8Dr9goMIVey7k";

            new PlacesTask().execute(sb);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setInfoWindowAdapter(new MapInfoWindowAdapter());

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
//                if (mGoogleApiClient == null) {
//                    buildGoogleApiClient();
//                }
                mMap.setMyLocationEnabled(true);
            }
        } else this.finish();

        showNearbyPlaces();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                Intent intent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
                String reference = mMarkerPlaceLink.get(arg0.getId());
                intent.putExtra("reference", reference);

                // Starting the Place Details Activity
                startActivity(intent);
            }
        });
    }

    /** A class, to download Google Places */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            }catch(Exception e) {
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            new ParserTask().execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
            try {
                jObject = new JSONObject(jsonData[0]);
                places = placeJsonParser.parse(jObject);
            } catch(Exception e) {
                Log.d("Exception",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list) {

            // Clears all the existing markers
            mMap.clear();
            HashMap<Double, HashMap<String, String>> placeDistance = new HashMap<>();

            if (list != null) {
                if (list.size() == 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                    dialog.setTitle(R.string.sorry)
                            .setMessage("But we couldn't find any suitable places nearby you.")
                            .setCancelable(false)
                            .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MapsActivity.this.finish();
                                }
                            })
                            .show();
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        HashMap<String, String> hmPlace = list.get(i);

                        double lat = Double.parseDouble(hmPlace.get("lat"));
                        double lng = Double.parseDouble(hmPlace.get("lng"));

                        if (showNearest) {
                            placeDistance.put(distanceFromCurrentLocation(mLatitude, mLongitude, lat, lng), hmPlace);
                        } else {
                            LatLng latLng = new LatLng(lat, lng);

                            String name = hmPlace.get("place_name");
//                            String vicinity = hmPlace.get("vicinity");

                            markerOptions.position(latLng);
                            markerOptions.title(name);
                            if (isHospital) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital));
                            } else
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_doctor));

                            Marker m = mMap.addMarker(markerOptions);
                            mMarkerPlaceLink.put(m.getId(), hmPlace.get("reference"));

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                        }
                    }


                    if (showNearest) {
                        List<Double> distances = new ArrayList<>(placeDistance.keySet());
                        Collections.sort(distances);
                        double shortestDistance = distances.get(0);
                        HashMap<String, String> nearestHastMap = placeDistance.get(shortestDistance);
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + nearestHastMap.get("lat") + "," + nearestHastMap.get("lng"))));

                        MapsActivity.this.finish();
                    }
                }
            }
        }
    }

    private Double distanceFromCurrentLocation(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        int meterConversion = 1609;
        return (double) Double.valueOf(dist * meterConversion).floatValue();    // this will return distance
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);


            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb  = new StringBuilder();

            String line;
            while( ( line = br.readLine())  != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e) {
            Log.e("Exception ", " while downloading url" + e.toString());
        }finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_mark_nearby) {
            showNearbyPlaces();
            return true;
        } else return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            showNearbyPlaces();
        }
        locationManager.removeUpdates(this);
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                        99);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View rootView;

        @SuppressLint("InflateParams")
        MapInfoWindowAdapter() {
            rootView = getLayoutInflater().inflate(R.layout.marker_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, rootView);
            return rootView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(Marker marker, View view) {
            TextView placeName = (TextView) view.findViewById(R.id.marker_place_name);
            placeName.setText(marker.getTitle());
        }
    }

    @Override
    protected void onDestroy() {
        mMap = null;
        super.onDestroy();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
