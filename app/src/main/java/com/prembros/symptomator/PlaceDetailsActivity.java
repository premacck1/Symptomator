package com.prembros.symptomator;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static com.prembros.symptomator.MapsActivity.isHospital;

public class PlaceDetailsActivity extends AppCompatActivity implements View.OnClickListener {

	private String name;
	private String phone;
	private String localPhone;
	private String address;
	private String lat;
	private String lng;
	private String rating;
	private String website;
	private String url;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_details);

		// Getting place reference from the mMap
		String reference = getIntent().getStringExtra("reference");

		String sb = "https://maps.googleapis.com/maps/api/place/details/json?" + "reference=" +
				reference + "&sensor=true" + "&key=AIzaSyC8JIhbRFb1uVOqawgaHP8Dr9goMIVey7k";

		// Creating a new non-ui thread task to download Google place details 
		PlacesTask placesTask = new PlacesTask();

		// Invokes the "doInBackground()" method of the class PlaceTask
		placesTask.execute(sb);

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

			StringBuilder sb = new StringBuilder();

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();
			br.close();

		} catch (Exception e) {
			Log.d("Exception", "while downloading url: " + e.toString());
		} finally {
			if (iStream != null) {
				iStream.close();
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		return data;
	}

	/** A class, to download Google Place Details */
	private class PlacesTask extends AsyncTask<String, Integer, String> {

		String data = null;

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result) {
			ParserTask parserTask = new ParserTask();

			// Start parsing the Google place details in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserTask.execute(result);
		}
	}


	/** A class to parse the Google Place Details in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, HashMap<String, String>> {

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected HashMap<String, String> doInBackground(String... jsonData) {

			HashMap<String, String> hPlaceDetails = null;
			PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				// Start parsing Google place details in JSON format
				hPlaceDetails = placeDetailsJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return hPlaceDetails;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(HashMap<String, String> hPlaceDetails) {
//			String icon = hPlaceDetails.get("icon");
//			String vicinity = hPlaceDetails.get("vicinity");
			name = hPlaceDetails.get("name");
			phone = hPlaceDetails.get("international_phone_number");
			localPhone = "N/A";
			if (phone.contains("NA"))
				localPhone = hPlaceDetails.get("formatted_phone");
			address = hPlaceDetails.get("formatted_address");
			lat = hPlaceDetails.get("lat");
			lng = hPlaceDetails.get("lng");
			rating = hPlaceDetails.get("rating");
			website = hPlaceDetails.get("website");
			url = hPlaceDetails.get("url");

			setData();
//			String mimeType = "text/html";
//			String encoding = "utf-8";

//			String data = 	"<html>"+
//							"<body><img style='float:left' src="+icon+" /><h1><center>"+name+"</center></h1>" +
//							"<br style='clear:both' />" +
//							"<hr  />"+
//							"<p>Vicinity : " + vicinity + "</p>" +
//							"<p>Location : " + lat + "," + lng + "</p>" +
//							"<p>Address : " + formatted_address + "</p>" +
//							"<p>Phone : " + formatted_phone + "</p>" +
//							"<p>Website : " + website + "</p>" +
//							"<p>Rating : " + rating + "</p>" +
//							"<p>International Phone  : " + international_phone_number + "</p>" +
//							"<p>URL  : <a href='" + url + "'>" + url + "</p>" +
//							"</body></html>";
		}
	}

	private void setData() {
		TextView type = (TextView) this.findViewById(R.id.type);
		ImageView iconTextView = (ImageView) this.findViewById(R.id.photo);
		TextView nameTextView = (TextView) this.findViewById(R.id.name);
		TextView phoneTextView = (TextView) this.findViewById(R.id.phone);
		TextView addressTextView = (TextView) this.findViewById(R.id.address);
		TextView locationTextView = (TextView) this.findViewById(R.id.location);
		RatingBar ratingBar = (RatingBar) this.findViewById(R.id.rating);
		TextView ratingTextView = (TextView) this.findViewById(R.id.rating_text);
		TextView wesiteTextView = (TextView) this.findViewById(R.id.website);
		TextView urlTextView = (TextView) this.findViewById(R.id.url);

		phoneTextView.setOnClickListener(this);
		locationTextView.setOnClickListener(this);
		wesiteTextView.setOnClickListener(this);
		urlTextView.setOnClickListener(this);

		if (isHospital) {
			iconTextView.setImageResource(R.drawable.ic_hospital_icon);
			type.setText(R.string.hospital);
		} else {
			iconTextView.setImageResource(R.drawable.ic_medical_icon);
			type.setText(R.string.doctor);
		}

		nameTextView.setText(name);
		String phoneText;
		if (!phone.contains("NA")) {
			phoneText = "Call " + phone;
			phoneTextView.setText(phoneText);
		}
		else if (!localPhone.contains("NA")){
			phoneText = "Call " + localPhone;
			phoneTextView.setText(phoneText);
		} else phoneTextView.setText(R.string.phone_number_not_available);

		String[] addr = address.split(",");
		address = addr[0];
		for (int i = 1; i<addr.length; i++) {
			address =  address + "\n" + addr[i];
		}
		addressTextView.setText(address);
		String location = "Get directions to this place";
		locationTextView.setText(location);

		if (!rating.contains("NA"))
			ratingBar.setRating(Float.parseFloat(rating));
		else ratingBar.setRating(0);

		String ratingText = rating;
		ratingTextView.setText(ratingText);

		wesiteTextView.setText(website);
		urlTextView.setText(url);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.phone:
				Intent intent = new Intent(Intent.ACTION_CALL);
				if (!phone.contains("NA"))
					intent.setData(Uri.parse("tel:" + phone));
				else if (!localPhone.contains("NA"))
					intent.setData(Uri.parse("tel:" + localPhone));
				else {
					Toast.makeText(this, "Phone number is not available!", Toast.LENGTH_SHORT).show();
					break;
				}
				if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
					this.startActivity(intent);
				} else {
					return;
				}
				break;
			case R.id.location:
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lng)));
				break;
			case R.id.website:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
				break;
			case R.id.url:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				break;
			default:
				break;
		}
	}
}