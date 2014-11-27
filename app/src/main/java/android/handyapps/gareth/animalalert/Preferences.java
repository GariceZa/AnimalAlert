package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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

        edit.commit();
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
}
