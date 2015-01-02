package android.handyapps.gareth.animalalert;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Gareth on 2014-12-02.
 */
public class MapsFragment extends SupportMapFragment {

    private GoogleMap mMap;
    ArrayList<LatLng>   latlng      = new ArrayList<>();
    ArrayList<String>   date        = new ArrayList<>();
    ArrayList<String>   description = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new LoadPoints().execute();
    }

    private void setUpMapIfNeeded() {
        // Do a  check to confirm that the map is not already instantiated
        if (mMap == null) {
            // obtain the map from the SupportMapFragment.
            mMap = getMap();
            // Check if obtaining the map was successful
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);

                // Adding markers for last 5 alerts
                for(int i = 0;i < latlng.size();i++) {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latlng.get(i))
                            .title(date.get(i))
                            .snippet(description.get(i)));
                }
            }
        }
    }

    private class LoadPoints extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            String url = "http://animalalert.garethprice.co.za/selectAlertLocations.php";
            HttpEntity httpEntity;

            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();

                if (httpEntity != null) {
                    response = EntityUtils.toString(httpEntity);
                }

            } catch (IOException e) {
                Log.v("--IOException--", e.toString());
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v("Response", s);

            try {
                JSONArray jsonArray = new JSONArray(s);

                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        latlng.add(new LatLng(jsonObject.getDouble("latitude"),jsonObject.getDouble("longitude")));
                        description.add(jsonObject.getString("AlertDescription"));
                        date.add(jsonObject.getString("AlertTime"));
                    } catch (JSONException e) {
                        Log.v("--JSONException--", e.toString());
                    }
                }
            } catch (JSONException e) {
                Log.v("--JSONException--", e.toString());
            }
            setUpMapIfNeeded();

        }
    }
}
