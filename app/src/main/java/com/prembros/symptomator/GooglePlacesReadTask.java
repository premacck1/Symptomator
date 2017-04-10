package com.prembros.symptomator;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

/*
 * Created by Prem $ on 4/10/2017.
 */

class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {

    private String googlePlacesData = null;
    private GoogleMap googleMap;

    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            googleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = result;
        placesDisplayTask.execute(toPass);
    }

    private class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

        JSONObject googlePlacesJson;
        GoogleMap googleMap;

        @Override
        protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

            List<HashMap<String, String>> googlePlacesList = null;
            Places placeJsonParser = new Places();

            try {
                googleMap = (GoogleMap) inputObj[0];
                googlePlacesJson = new JSONObject((String) inputObj[1]);
                googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return googlePlacesList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            googleMap.clear();
            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = list.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                googleMap.addMarker(markerOptions);
            }
        }

        private class Places {

            List<HashMap<String, String>> parse(JSONObject jsonObject) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return getPlaces(jsonArray);
            }

            private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
                int placesCount = jsonArray.length();
                List<HashMap<String, String>> placesList = new ArrayList<>();
                HashMap<String, String> placeMap;

                for (int i = 0; i < placesCount; i++) {
                    try {
                        placeMap = getPlace((JSONObject) jsonArray.get(i));
                        placesList.add(placeMap);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return placesList;
            }

            private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
                HashMap<String, String> googlePlaceMap = new HashMap<>();
                String placeName = "-NA-";
                String vicinity = "-NA-";
                String latitude;
                String longitude;
                String reference;

                try {
                    if (!googlePlaceJson.isNull("name")) {
                        placeName = googlePlaceJson.getString("name");
                    }
                    if (!googlePlaceJson.isNull("vicinity")) {
                        vicinity = googlePlaceJson.getString("vicinity");
                    }
                    latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                    longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
                    reference = googlePlaceJson.getString("reference");
                    googlePlaceMap.put("place_name", placeName);
                    googlePlaceMap.put("vicinity", vicinity);
                    googlePlaceMap.put("lat", latitude);
                    googlePlaceMap.put("lng", longitude);
                    googlePlaceMap.put("reference", reference);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return googlePlaceMap;
            }
        }
    }

    private class Http {

        String read(String httpUrl) throws IOException {
            String httpData = "";
            HttpURLConnection httpURLConnection;
            URL url = new URL(httpUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuffer = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                httpData = stringBuffer.toString();
                bufferedReader.close();
            } catch (Exception e) {
                Log.d("ERROR reading Http url", e.toString());
            } finally {
                httpURLConnection.disconnect();
            }
            return httpData;
        }
    }
}