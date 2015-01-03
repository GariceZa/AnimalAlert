package android.handyapps.gareth.animalalert;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class AlertsFragment extends Fragment {

    private RecyclerView                mRecyclerView;
    private RecyclerView.Adapter        mAdapter;
    private RecyclerView.LayoutManager  mLayoutManager;
    private SwipeRefreshLayout          mSwipeRefreshLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setting up recycler view

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // If screen size is < 480 then use linear layout
        if(getResources().getConfiguration().smallestScreenWidthDp <= 480){
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        // If screen size is < 720 then use grid layout with 2 columns
        else if(getResources().getConfiguration().smallestScreenWidthDp < 720){
            mLayoutManager = new GridLayoutManager(getActivity(),2);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        // else the screen size is > 720, use grid layout with 3 columns
        else{
            mLayoutManager = new GridLayoutManager(getActivity(),3);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        //---------------------------------------------------

        // Execute the async task to load alerts
        new LoadAlerts().execute();
    }

    // setting up the view during view creation
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alerts,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        // Setting up the swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadAlerts().execute();
            }
        });

        return view;
    }

    private class LoadAlerts extends AsyncTask<Void,Void,String> {

        ArrayList<String> time = new ArrayList<>();
        ArrayList<String> msg = new ArrayList<>();

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            String url = "http://animalalert.garethprice.co.za/selectAlerts.php";
            HttpEntity httpEntity;

            try {
                // Returns the api response
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

            try{
                JSONArray jsonArray = new JSONArray(s);

                // Loads the array lists
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        time.add(jsonObject.getString("AlertTime"));
                        msg.add(jsonObject.getString("AlertDescription"));
                    } catch (JSONException e) {
                        Log.v("--JSONException--",e.toString());
                    }
                }
            } catch (JSONException e) {
                Log.v("--JSONException--",e.toString());
            }

            // Specify an adapter
            mAdapter = new AlertMessageAdapter(time,msg);
            // Set the adapter
            mRecyclerView.setAdapter(mAdapter);
            // Set swipe refresh to false if swiped
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
