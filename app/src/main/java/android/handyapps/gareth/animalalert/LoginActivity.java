package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;


public class LoginActivity extends Activity implements LocationListener {

    LatLng coOrdinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupLocationUpdates();
    }

    public void login(View view) {
        Toast.makeText(getApplicationContext(), "You Clicked login", Toast.LENGTH_SHORT).show();
    }

    public void register(View view) {

        GPS gps = new GPS(this);

        // if google play services installed
        if (gps.getGooglePlayService()) {
            // if location services are enabled
            if (locationServiceEnabled()) {
                // Open registration dialog
                RegistrationDialog register = RegistrationDialog.newInstance(displayAddress(coOrdinates));
                register.show(getFragmentManager(), "dialog");

            } else {
                // Display alert dialog
                locationServiceDisabledAlert();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        coOrdinates = new LatLng(location.getLatitude(), location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private String displayAddress(LatLng coOrdinates){

        //instantiating new Geocoder object
        Geocoder gCode = new Geocoder(this, Locale.getDefault());
        String address = "";
        try
        {
            //Address variables
            String streetAdd = "",suburb = "";
            //----------------

            //adding address information to the addresses arraylist
            List<Address> addresses = gCode.getFromLocation(coOrdinates.latitude,coOrdinates.longitude,1);

            //if the addresses arraylist is not empty then set variables
            if(addresses.size() > 0)
            {
                streetAdd   = addresses.get(0).getAddressLine(0);
                suburb      = addresses.get(0).getLocality();
                address     = streetAdd + " " + suburb;
            }

        }
        catch(Exception Err)
        {
            //display any errors in this toast
            Toast.makeText(this, "Error: " + Err, Toast.LENGTH_LONG).show();
        }

        return address;
    }

    protected void setupLocationUpdates(){

        LocationManager locMan = (LocationManager)getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        Location location = locMan.getLastKnownLocation(provider);
        locMan.requestLocationUpdates(provider,2000,5,this);
    }

    // displays an alert dialog allowing the user to turn location services on
    private void locationServiceDisabledAlert(){

        AlertDialog.Builder locationServiceAlert = new AlertDialog.Builder(this);
        locationServiceAlert.setTitle(R.string.enable_location_service_title)
                .setMessage(R.string.enable_location_service_message)
                .setCancelable(false)
                .setPositiveButton(R.string.enable,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //opens the location service settings
                        Intent EnableGPSIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(EnableGPSIntent);
                        // closes the activity
                        finish();
                    }
                });
        locationServiceAlert.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // cancels the alert dialog
                dialog.cancel();
            }
        });
        AlertDialog alert = locationServiceAlert.create();
        alert.show();
    }
    // determines if the GPS location service is on
    private boolean locationServiceEnabled() {
        LocationManager locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}