package com.prembros.symptomator;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by Prem $ on 6/15/2017.
 */

class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private double latitude;
    private double longitude;
    private boolean showNearest;
    private HashMap<Double, HashMap<String, String>> placeDistance;
    private String[] nearestMarker;
    private String googlePlacesData;
    private GoogleMap mMap;

    GetNearbyPlacesData(double latitude, double longitude, boolean showNearest) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.showNearest = showNearest;
        placeDistance = new HashMap<>();
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            mMap = (GoogleMap) params[0];
            String url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (Exception e) {
            Log.e("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        List<HashMap<String, String>> nearbyPlacesList;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        if (showNearest) {
            nearestMarker = nearestMarker();
        }
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        if (nearbyPlacesList != null) {
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                if (showNearest) {
                    placeDistance.put(distanceFrom(latitude, longitude, lat, lng), googlePlace);
                } else {
                    LatLng latLng = new LatLng(lat, lng);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName);
                    markerOptions.snippet(vicinity);
                    mMap.addMarker(markerOptions);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                }
            }
        }
    }

    private String[] nearestMarker() {
//        List<HashMap<Double, HashMap<String/, String>>> hashMapList = null;
//        HashMap<Double, HashMap<String, String>> doubleHashMap = new HashMap<>();
//        for (Map.Entry<Double, HashMap<String, String>> entry : placeDistance.entrySet()) {
//            doubleHashMap.put(entry.getKey(), entry.getValue());
//            hashMapList.add(doubleHashMap);
//        }
        List<Double> distances = new ArrayList<>(placeDistance.keySet());
        Collections.sort(distances);
        double shortestDistance = distances.get(0);
        HashMap<String, String> nearestHastMap = placeDistance.get(shortestDistance);
        return new String[]{nearestHastMap.get("lat"), nearestHastMap.get("lng")};
    }

    String[] getNearestMarker() {
        return nearestMarker;
    }

    private Double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        int meterConversion = 1609;
        return (double) Double.valueOf(dist * meterConversion).floatValue();    // this will return distance
    }
}
