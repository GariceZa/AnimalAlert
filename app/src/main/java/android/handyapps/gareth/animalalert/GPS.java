package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Gareth on 2014-11-20.
 */
public class GPS {

    private Context context;

    public GPS(Context con){
        context = con;
    }

    public boolean getGooglePlayService(){
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if(status == ConnectionResult.SUCCESS) return true;
        else
            // Alerts the user that google play services is not installed and
            // provides the user with a link to the play store to get the service
            GooglePlayServicesUtil.getErrorDialog(status, (Activity) context, 10).show();
        return false;
    }
}
