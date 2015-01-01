package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by Gareth on 2014-11-20.
 */
public class RegistrationDialog extends Activity implements LocationListener {

    private TextView userAddress;
    private LatLng coOrdinates;
    private ProgressDialog progressDialog;
    private FormEditText name,surname,email,password,confirmPassword;
    private String userName,userSurname,userEmail,userPassword,streetAddress,regError;
    private double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(locationServiceEnabled()){
            setContentView(R.layout.dialog_registration);
            setupLocationUpdates();
            setupRegistrationViews();
        }
        else{
            locationServiceDisabledAlert();
        }
    }

    // setting ui views
    private void setupRegistrationViews(){

        name            = (FormEditText)findViewById(R.id.regFirstName);
        surname         = (FormEditText)findViewById(R.id.regSurame);
        email           = (FormEditText)findViewById(R.id.regEmail);
        password        = (FormEditText)findViewById(R.id.regPassword);
        confirmPassword = (FormEditText)findViewById(R.id.regConfirmPassword);
        userAddress     = (TextView)findViewById(R.id.regLocation);
    }

    // sets the provider and update intervals
    private void setupLocationUpdates(){

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
            Toast.makeText(this, getResources().getString(R.string.error) + ": " + Err, Toast.LENGTH_LONG).show();
        }

        return address;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Get the coordinates and set the views
        coOrdinates = new LatLng(location.getLatitude(), location.getLongitude());
        userAddress.setText(displayAddress(coOrdinates));
        lat = location.getLatitude();
        lon = location.getLongitude();
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

    // Validates the users registration input
    private boolean validInput(){

        // If the passwords do not match
        if(!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())){
            regError = getResources().getString(R.string.password_mismatch);
            return false;
        }
        // If the users address is still being detected
        else if(userAddress.getText().equals(getResources().getString(R.string.detecting_address))){
            regError = getResources().getString(R.string.address_not_detected);
            return false;
        }
        else
            return true;

    }

    // called when user clicks register
    public void registerUser(View view) {

        FormEditText[] registrationFields = {name,surname,email,password,confirmPassword};
        boolean fieldsValid = true;

        for(FormEditText field: registrationFields){
            fieldsValid = field.testValidity() && fieldsValid;
        }
        if(fieldsValid){
            if(!validInput()){
                registrationError(regError);
            }
            else{
                userName        = name.getText().toString();
                userSurname     = surname.getText().toString();
                userEmail       = email.getText().toString();
                userPassword    = password.getText().toString();
                streetAddress   = userAddress.getText().toString();

                new RegistrationResponse().execute(new RegistrationAPI(userName,userSurname,userEmail,userPassword,streetAddress,lat,lon));
            }
        }
    }

    // setup the progress dialog
    private void startRegistrationProgressDialog() {

        String title = getResources().getString(R.string.registering);
        String message = getResources().getString(R.string.please_wait);

        progressDialog = new ProgressDialog(RegistrationDialog.this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

    }

    // dismisses the progress dialog
    private void stopRegistrationProgressDialog() {
        progressDialog.dismiss();
    }

    private void registrationError(String regError){

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationDialog.this);
        builder.setTitle(R.string.error);
        builder.setMessage(regError);
        builder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog regErr = builder.create();
        regErr.show();

    }

   private class RegistrationResponse extends AsyncTask<RegistrationAPI,Long,JSONArray> {


       @Override
        protected void onPreExecute() {
           // start the progress wheel
           startRegistrationProgressDialog();
        }

       @Override
       protected JSONArray doInBackground(RegistrationAPI... params) {
           // Return the response from RegistrationAPI
           return params[0].getRegistrationResponse();
       }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

                try{
                    String response;

                    for(int i = 0; i <jsonArray.length();i++){
                        JSONObject json = jsonArray.getJSONObject(i);

                        // stores result from userRegistration.php
                        response = json.getString("response");

                        if(response.equals("true")){
                            //Save email and password
                            Preferences prefs = new Preferences();
                            prefs.setSharedPrefs(RegistrationDialog.this,userEmail,userPassword);
                            // Alert user that registration was successful
                            Toast.makeText(RegistrationDialog.this,getResources().getString(R.string.registration_complete),Toast.LENGTH_LONG).show();
                            // Close the registration dialog
                            finish();
                        }
                        else{
                            // Display the error in an alert dialog
                            registrationError(response);
                        }
                    }
                }
                catch (JSONException e) {
                    Log.v("--JSONException--", e.toString());
                }
                catch(NullPointerException e){
                    Log.v("--NullPointerException--", e.toString());
                }
                catch(Exception e){
                    Log.v("--Exception--", e.toString());
                }
                finally {
                    stopRegistrationProgressDialog();
                }
        }
    }
}
