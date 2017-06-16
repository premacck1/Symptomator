package com.prembros.symptomator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

//    private static final String KEY_CAMERA_POSITION = "camera_position";
//    private static final String KEY_LOCATION = "location";
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
//    CameraPosition mCameraPosition;

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private boolean showNearest;
    private RelativeLayout progressBarContainer;
    private GoogleApiClient mGoogleApiClient;
//    private Location mLastLocation;
    private Marker mCurrLocationMarker;

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        if (mMap != null) {
//            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
//            outState.putParcelable(KEY_LOCATION, mLastLocation);
//            super.onSaveInstanceState(outState);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        if (savedInstanceState != null) {
//            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
//            mLastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
//        }
        if (checkGooglePlayServices()) {

            showNearest = getIntent().getExtras().getBoolean("showNearest");

            progressBarContainer = (RelativeLayout) MapsActivity.this.findViewById(R.id.progress_bar_container);
            if (showNearest) {
                progressBarContainer.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                ((TextView) MapsActivity.this.findViewById(R.id.myProgressText)).setText(R.string.getting_directions_to_nearest_hospital);
                progressBarContainer.setVisibility(View.VISIBLE);
            } else {
                progressBarContainer.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                ((TextView) MapsActivity.this.findViewById(R.id.myProgressText)).setText(R.string.finding_nearby_hospitals);
                progressBarContainer.setVisibility(View.VISIBLE);
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
//            actionBar.setTitle(R.string.symptom);
        }

        buildGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }
                mMap.setMyLocationEnabled(true);
            }
        } else this.finish();

//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                // Inflate the layouts for the info window, title and snippet.
//                @SuppressLint("InflateParams")
//                View infoWindow = getLayoutInflater().inflate(R.layout.marker_info, null);
//
//                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
//                title.setText(marker.getTitle());
//
//                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
//                snippet.setText(marker.getSnippet());
//
//                return infoWindow;
//            }
//        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                markNearbyHospitals();
//            }
//        }, 2000);
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

//    public void buildGoogleApiClient(){
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .enableAutoManage(this, 0, this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//        mGoogleApiClient.connect();
//    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            mAdapter.setGoogleApiClient(null);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (checkLocationPermission()) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } else this.finish();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        focusCurrentLocation(location);
        markNearbyHospitals();

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    void markNearbyHospitals() {
        mMap.clear();
        String url = getUrl(latitude, longitude, "hospital");
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        final GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(latitude, longitude, showNearest);
        getNearbyPlacesData.execute(DataTransfer);
        if (progressBarContainer.getVisibility() == View.VISIBLE) {
            progressBarContainer.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            progressBarContainer.setVisibility(View.INVISIBLE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (showNearest) {
                    String[] nearestMarker = getNearbyPlacesData.getNearestMarker();
                    if (nearestMarker != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + nearestMarker[0] + "," + nearestMarker[1])));
                        MapsActivity.this.finish();
                    }
                }
            }
        }, 2000);
//        Toast.makeText(MapsActivity.this, "Nearby Hospitals", Toast.LENGTH_LONG).show();
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        int PROXIMITY_RADIUS = 20000;
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=").append(nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyC8JIhbRFb1uVOqawgaHP8Dr9goMIVey7k");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    void focusCurrentLocation(Location location){
        final LinearLayout container = (LinearLayout) MapsActivity.this.findViewById(R.id.marker_details_container);
        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            Marker mMarker;

            @Override
            public boolean onMarkerClick(Marker marker) {
                mMarker = marker;
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.purple_marker));
                container.startAnimation(AnimationUtils.loadAnimation(MapsActivity.this, R.anim.float_down));
                container.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMarker.hideInfoWindow();
                    }
                }, 5);
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        marker.hideInfoWindow();
                        toggleMarkerDetails(container, mMarker);
                        mMarker = null;
                    }
                });

                TextView title = (TextView) MapsActivity.this.findViewById(R.id.marker_title);
                TextView snippet = (TextView) MapsActivity.this.findViewById(R.id.marker_snippet);
                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                return false;
            }
        });
//        if (mCameraPosition != null) {
//            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
//        } else if (mLastLocation != null) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                    new LatLng(mLastLocation.getLatitude(),
//                            mLastLocation.getLongitude()), 13));
//        } else {
//        }
    }

    void toggleMarkerDetails(View container, final Marker marker) {
        if (container != null && marker != null) {
            if (container.getVisibility() == View.VISIBLE) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker());
                container.startAnimation(AnimationUtils.loadAnimation(MapsActivity.this, R.anim.sink_up));
                container.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        marker.hideInfoWindow();
                    }
                }, 5);
            } else {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.purple_marker));
                container.startAnimation(AnimationUtils.loadAnimation(MapsActivity.this, R.anim.float_down));
                container.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        marker.hideInfoWindow();
                    }
                }, 5);
            }
        }
    }

//    private void displayPlacePicker() {
//        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected())
//            return;
//
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//        try {
//            startActivityForResult(builder.build(this), 10);
//        } catch (GooglePlayServicesRepairableException e) {
//            Log.d("PlacesAPI Demo", "GooglePlayServicesRepairableException thrown");
//        } catch (GooglePlayServicesNotAvailableException e) {
//            Log.d("PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown");
//        }
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == 10 && resultCode == RESULT_OK) {
//            displayPlace(PlacePicker.getPlace(this, data));
//        }
//    }
//
//    private void displayPlace(Place place) {
//        if(place == null)
//            return;
//
//        String content = "";
//        if(!TextUtils.isEmpty(place.getName())) {
//            content += "Name: " + place.getName() + "\n";
//        }
//        if(!TextUtils.isEmpty(place.getAddress())) {
//            content += "Address: " + place.getAddress() + "\n";
//        }
//        if(!TextUtils.isEmpty(place.getPhoneNumber())) {
//            content += "Phone: " + place.getPhoneNumber();
//        }
//
//        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
//    }

    public boolean checkLocationPermission(){
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
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        99);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_current_location:
//                focusCurrentLocation(mLastLocation);
//                return true;
            case R.id.action_mark_nearby_hospitals:
                markNearbyHospitals();
                return true;
            default:
                return false;
        }
//        return super.onOptionsItemSelected(item);
    }
}
