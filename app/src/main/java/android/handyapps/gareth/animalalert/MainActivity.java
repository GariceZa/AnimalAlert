package android.handyapps.gareth.animalalert;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Gareth on 2014-12-17.
 * for info go to the following github project: https://github.com/astuetz/PagerSlidingTabStrip
 */
public class MainActivity extends FragmentActivity {

    private PagerSlidingTabStrip    tabs;
    private ViewPager               pager;
    private MyPagerAdapter          adapter;
    private ProgressDialog          progressDialog;
    private double                  lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the sliding page tabs
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        //-------------------------

        // Checking location services are enabled
        if(!locationServiceEnabled()){
            locationServiceDisabledAlert();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(),"You clicked settings",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_signout:
                Preferences prefs = new Preferences();
                prefs.removeSharedPrefs(MainActivity.this);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // ADD ALERT FRAGMENT METHODS---------------------------------------------------

        // Validates user input and adds alert to database
        public void sendAlert(View view) {
            EditText alertDetails = (EditText)findViewById(R.id.alertDescription);

            if(TextUtils.isEmpty(alertDetails.getText().toString())){
                alertDetails.setError(getResources().getString(R.string.description_missing));
            }
            else{
                new UpdateLocationCoordinates().execute();
            }
        }

        // Displays an alert dialog allowing the user to turn location services on
        private void locationServiceDisabledAlert(){

            AlertDialog.Builder locationServiceAlert = new AlertDialog.Builder(this);
            locationServiceAlert.setTitle(R.string.enable_location_service_title)
                    .setMessage(R.string.enable_location_service)
                    .setCancelable(true)
                    .setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
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

        // Determines if location services are enabled
        private boolean locationServiceEnabled() {
            LocationManager locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
            return locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        // Displays an indeterminate progress dialog
        private void startProgressDialog(String title,String message){

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        // Removes the progress dialog
        private void stopProgressDialog(){
            progressDialog.dismiss();
        }

        // Displays any error messages in an alert dialog
        private void saveError(String regError){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    //------------------------------------------------------------------------------
    public class MyPagerAdapter extends FragmentPagerAdapter{

        private final String[] TITLES = {"Sightings Map","Recent Sightings","Add Sighting"};


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new MapsFragment();
                case 1:
                    return new AlertsFragment();
                case 2:
                    return new AddAlertFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }

    private class UpdateLocationCoordinates extends AsyncTask<Void,Void,Location> implements LocationListener{

        private LocationManager locMan;
        private Location location;
        private String provider;
        private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
        private String alertDate;

        // Sets the provider and update intervals
        private void setupLocationUpdates(){

            locMan = (LocationManager)getSystemService(LOCATION_SERVICE);
            provider = LocationManager.GPS_PROVIDER;
            locMan.requestLocationUpdates(provider,1000,0, this);

        }
        // Sets latitude and longitude
        private void setLatLon(Location location){
            lat = location.getLatitude();
            lon = location.getLongitude();
        }

        @Override
        protected void onPreExecute() {
            setupLocationUpdates();
            startProgressDialog(getResources().getString(R.string.add_alert_title),getResources().getString(R.string.add_alert_message));
        }

        @Override
        protected Location doInBackground(Void... params) {

            while(location == null){
             //Wait for location to be initialized
            }
            while(location.getAccuracy() >= 25){
             //Wait for location accuracy to < 25 meters
            }
            return location;
        }

        @Override
        protected void onPostExecute(Location location) {
            locMan.removeUpdates(this);
            setLatLon(location);
            stopProgressDialog();

            // Starting AddAlertResponse Asynctask
            Preferences prefs = new Preferences();
            EditText alertDetails = (EditText)findViewById(R.id.alertDescription);
            alertDate = df.format(Calendar.getInstance().getTime());
            new AddAlertResponse().execute(new AddAlertAPI(prefs.getEmailSharedPrefs(getApplicationContext()),alertDetails.getText().toString(),lat,lon,alertDate));
            //------------------------------------

        }

        @Override
        public void onLocationChanged(Location newLocation) {
                location = newLocation;
                Log.v("--LOCATION--","lat/lng: " + newLocation.getLatitude() + " " + newLocation.getLongitude() + " Accuracy: " + newLocation.getAccuracy());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("--onStatusChanged","Status Changed " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("--onProviderEnabled","Provider Enabled " + provider);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("--onProviderDisabled","Provider Disabled " + provider);
        }
    }

    private class AddAlertResponse extends AsyncTask<AddAlertAPI,Long,JSONArray> {

        @Override
        protected void onPreExecute() {
            startProgressDialog(getResources().getString(R.string.save_alert_title),getResources().getString(R.string.save_alert_message));
        }

        @Override
        protected JSONArray doInBackground(AddAlertAPI... params) {
            // Return the response from RegistrationAPI
            return params[0].getAddAlertResponse();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            String response="";
            try {
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject json = jsonArray.getJSONObject(i);

                    // stores result from addAlert.php
                    response = json.getString("response");

                    Log.v("--RESPONSE--",response);

                    if (response.equals("true")) {
                        //Informing user that their alert has been saved
                        Toast.makeText(getApplicationContext(), "Alert saved", Toast.LENGTH_LONG).show();
                        //--------------------------

                        // Clearing the users input
                        EditText alertDescription = (EditText)findViewById(R.id.alertDescription);
                        alertDescription.setText("");
                        //---------------------------
                    }
                    else{
                        // Displaying api error response
                        saveError(response);
                    }
                }
            }
            catch (JSONException e) {
                Log.v("--JSONException--",e.toString());
            }
            catch (NullPointerException e) {
                Log.v("--NullPointerException--",e.toString());
            }
            catch (Exception e) {
                Log.v("--Exception--",e.toString());
            }
            finally {
                stopProgressDialog();
                if(!response.equals("true")){
                    saveError(response);
                }
            }
        }
    }

}
