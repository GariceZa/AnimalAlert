package android.handyapps.gareth.animalalert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Gareth on 2014-12-02.
 */
public class AddAlertFragment extends Fragment  {

    // inflates the fragment for adding an alert
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_alert,container,false);
    }
}
