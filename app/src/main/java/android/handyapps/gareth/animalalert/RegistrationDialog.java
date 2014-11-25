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
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText name,surname,email,password,confirmPassword;
    private String userName,userSurname,userEmail,userPassword,userLocation,regError;

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

        name            = (EditText)findViewById(R.id.regFirstName);
        surname         = (EditText)findViewById(R.id.regSurame);
        email           = (EditText)findViewById(R.id.regEmail);
        password        = (EditText)findViewById(R.id.regPassword);
        confirmPassword = (EditText)findViewById(R.id.regConfirmPassword);
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

    // Validates the users registration input
    private boolean validInput(){

        // If any fields are empty
        if(name.getText().toString().trim().length() == 0 || surname.getText().toString().trim().length() == 0
                || email.getText().toString().trim().length() == 0 || password.getText().toString().trim().length() == 0 || confirmPassword.getText().toString().trim().length() == 0) {
            regError = getResources().getString(R.string.empty_field);
            return false;
        }
        // If the email address is not valid
        else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            regError = getResources().getString(R.string.email_invalid);
            return false;
        }
        // If the passwords do not match
        else if(!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())){
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

       if(!validInput()){
           registrationError(regError);
       }
        else{
           userName     = name.getText().toString();
           userSurname  = surname.getText().toString();
           userEmail    = email.getText().toString();
           userPassword = password.getText().toString();

           new RegistrationResponse().execute(new RegistrationAPI(userName,userSurname,userEmail,userPassword));
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

    private void registrationError(String regErrorMsg){

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
                        finish();
                        Toast.makeText(RegistrationDialog.this,"Registration complete",Toast.LENGTH_LONG).show();
                    }
                    else{
                        // Display the error in an alert dialog
                        registrationError(response);
                    }
                }

                }
                catch (JSONException e) {
                    registrationError(e.toString());
                }
                catch(NullPointerException e){
                    registrationError(e.toString());
                }
                catch(Exception e){
                    registrationError(e.toString());
                }
                finally {
                    stopRegistrationProgressDialog();
                }
        }
    }
}
