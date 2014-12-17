package android.handyapps.gareth.animalalert;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;


/**
 * Created by Gareth on 2014-12-02.
 */
public class MapsFragment extends SupportMapFragment {

    private GoogleMap mMap;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a  check to confirm that the map is not already instantiated
        if (mMap == null) {
            // obtain the map from the SupportMapFragment.
            mMap = getMap();
            // Check if obtaining the map was successful
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }
}
