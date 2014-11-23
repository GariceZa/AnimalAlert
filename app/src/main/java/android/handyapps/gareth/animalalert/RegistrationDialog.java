package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.app.AlertDialog;
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

/**
 * Created by Gareth on 2014-11-20.
 */
public class RegistrationDialog extends Activity implements LocationListener {

    TextView userAddress;
    LatLng coOrdinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(locationServiceEnabled()){
            setContentView(R.layout.dialog_registration);
            setupLocationUpdates();
            userAddress = (TextView)findViewById(R.id.regLocation);
        }
        else{
            locationServiceDisabledAlert();
        }
    }

    // sets the provider and update intervals
    protected void setupLocationUpdates(){

        LocationManager locMan = (LocationManager)getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
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
                // closes the alert dialog
                finish();
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

    // returns the users street address
    private String displayAddress(LatLng coOrdinates){

        //instantiating new Geocoder object
        Geocoder gCode = new Geocoder(this, Locale.getDefault());
        String address = "";
        try
        {
            //Address variables
            String streetAdd,suburb;
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

    @Override
    public void onLocationChanged(Location location) {
        coOrdinates = new LatLng(location.getLatitude(), location.getLongitude());
        userAddress.setText(displayAddress(coOrdinates));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Do nothing
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Do nothing
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Do nothing
    }

    public void registerUser(View view) {
        Toast.makeText(this,"You clicked register",Toast.LENGTH_SHORT).show();
    }
}
