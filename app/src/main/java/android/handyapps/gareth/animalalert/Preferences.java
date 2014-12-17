package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Gareth on 2014-11-27.
 */
public class Preferences extends Activity {

    // Saves username & password to shared prefs
    protected void setSharedPrefs(Context context,String email,String password){

        SharedPreferences setUserInfo = context.getSharedPreferences("userInfo",0);
        SharedPreferences.Editor edit = setUserInfo.edit();

        edit.putString("email",email);
        edit.putString("password",password);

        edit.apply();
    }

    // Saves username & password to shared prefs
    protected void removeSharedPrefs(Context context){

        SharedPreferences setUserInfo = context.getSharedPreferences("userInfo",0);
        SharedPreferences.Editor edit = setUserInfo.edit();

        edit.putString("email","");
        edit.putString("password","");

        edit.apply();
    }

    // Returns the stored email address
    protected String getEmailSharedPrefs(Context context){

        SharedPreferences getUserInfo = context.getSharedPreferences("userInfo",0);
        return getUserInfo.getString("email","");
    }

    // Returns the stored password
    protected String getPasswordSharedPrefs(Context context){

        SharedPreferences getUserInfo = context.getSharedPreferences("userInfo",0);
        return getUserInfo.getString("password","");
    }

    // Returns a boolean depending on if the user info shared prefs are set
    protected boolean sharedPrefsAreSet(Context context){

        if(getEmailSharedPrefs(context).equals("") && getPasswordSharedPrefs(context).equals("")){
            Log.v("---Shared prefs---","NOT SET");
            return false;
        }
        Log.v("---Shared prefs---","SET");
        return true;
    }
}
