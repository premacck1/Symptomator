package com.prembros.symptomator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.Locale;

/**
 *
 * Created by Prem $ on 6/16/2017.
 */

public class CallEmergencyServices extends AppCompatActivity {

    @SuppressWarnings("unused")
    public CallEmergencyServices() {
        doCalling(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doCalling(this);
    }

    public CallEmergencyServices(Context context) {
        doCalling(context);
    }

    void doCalling(Context context) {
        DatabaseHolder db = new DatabaseHolder(context);
        db.open();
        Cursor cursor = db.returnEmergencyNumber(getUserCountry(context));
        cursor.moveToFirst();
        call(
                cursor.getString(cursor.getColumnIndex("Country")),                         //Country name
                Integer.parseInt(cursor.getString(cursor.getColumnIndex("Number"))),        //Emergency number
                context
        );
        cursor.close();
        db.close();
    }

    private String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void call(String country, int number, final Context context){
        final Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Toast.makeText(context, "Calling emergency services in " + country + ", \"" + number + "\"", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(callIntent);
            }
        }, 500);

//        TextView title = new TextView(context);
//        title.setText("Emergency number");
//        title.setPadding(50, 50, 50, 50);
//        title.setGravity(Gravity.CENTER);
//        title.setTextSize(25);
//        title.setTextColor(Color.rgb(33, 33, 33));
//        title.setTypeface(Typeface.DEFAULT_BOLD);
//
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setIcon(R.drawable.ic_call_emergency_services)
//                .setCustomTitle(title)
//                .setMessage("*__ " + country + " __*\n\nWe're going to call \"" + number + "\" for you.\nClick OKAY to confirm.")
//                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                })
//                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//        AlertDialog alertDialog = dialog.show();
//        TextView messageText = (TextView)alertDialog.findViewById(android.R.id.message);
//        if (messageText != null) {
//            messageText.setGravity(Gravity.CENTER);
//            messageText.setTextColor(Color.rgb(117, 117, 117));
//        }
    }
}
