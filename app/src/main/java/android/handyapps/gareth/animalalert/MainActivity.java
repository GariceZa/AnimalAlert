package android.handyapps.gareth.animalalert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Gareth on 2014-12-17.
 * for info go to the following github project: https://github.com/astuetz/PagerSlidingTabStrip
 */
public class MainActivity extends FragmentActivity implements LocationListener {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("--ANDROID LIFECYCLE--","ON RESUME");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("--ANDROID LIFECYCLE--", "ON PAUSE");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("--ANDROID LIFECYCLE--", "ON STOP");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("--ANDROID LIFECYCLE--", "ON START");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("--ANDROID LIFECYCLE--", "ON RESTART");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("--ANDROID LIFECYCLE--","ON CREATE");
        setContentView(R.layout.activity_main);

        //Setting up the sliding page tabs
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        //-------------------------

        // Checking location services are enabled
        if(locationServiceEnabled()){
            setupLocationUpdates();
        }
        else{
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
            TextView alertLocation = (TextView)findViewById(R.id.alertLocation);

            if(TextUtils.isEmpty(alertDetails.getText().toString())){
                alertDetails.setError(getResources().getString(R.string.description_missing));
            }
            else if(alertLocation.getText().equals(getResources().getString(R.string.detecting_location))){
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.still_detecting_location),Toast.LENGTH_LONG).show();
            }
            else{
                //save data to db via asynctask
                Toast.makeText(getApplicationContext(),"All good",Toast.LENGTH_SHORT).show();
            }
        }

        // Displays an alert dialog allowing the user to turn location services on
        private void locationServiceDisabledAlert(){

            AlertDialog.Builder locationServiceAlert = new AlertDialog.Builder(this);
            locationServiceAlert.setTitle(R.string.enable_location_service_title)
                    .setMessage(R.string.enable_location_service)
                    .setCancelable(true)
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

        // Sets the provider and update intervals
        private void setupLocationUpdates(){

            LocationManager locMan = (LocationManager)getSystemService(LOCATION_SERVICE);
            String provider = LocationManager.GPS_PROVIDER;
            locMan.requestLocationUpdates(provider,5000,0, this);
        }

        // Determines if the GPS location service is on
        private boolean locationServiceEnabled() {
            LocationManager locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
            return locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        @Override
        public void onLocationChanged(Location location) {
            TextView alertLocation = (TextView)findViewById(R.id.alertLocation);
            LatLng coOrdinates;

            if(location.getAccuracy() < 20){
                // Get the location lat/lng
                coOrdinates = new LatLng(location.getLatitude(), location.getLongitude());
                // Set view text to latlng, if the user navigates to another tab the text view gets destroyed hence the check if it is null
                if(alertLocation != null){
                    alertLocation.setText(coOrdinates.toString());
                }

                Log.v("--LOCATION DATA--: ","lat/lng: " + coOrdinates.toString() + " accuracy: " + location.getAccuracy());
            }
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

    //------------------------------------------------------------------------------
    public class MyPagerAdapter extends FragmentPagerAdapter{

        private final String[] TITLES = {"Add Sighting","Recent Sightings","Sightings Map"};


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
                    return new AddAlertFragment();
                case 1:
                    return new AlertsFragment();
                case 2:
                     return new MapsFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }
}
