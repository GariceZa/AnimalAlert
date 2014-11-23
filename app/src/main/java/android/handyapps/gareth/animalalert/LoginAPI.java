package android.handyapps.gareth.animalalert;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Gareth on 2014-11-23.
 */
public class LoginAPI {

    private String email,password;

    public LoginAPI(String em,String pass){
        email = em;
        password = pass;
    }

    public JSONArray getLoginResponse(){

        String url = "http://animalalert.garethprice.co.za/login.php?email=" + email + "&password=" + password;
        HttpEntity httpEntity = null;
        JSONArray jsonArray = null;

        try{
            // Creating a new DefaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();

            // HttpGet to get data from the php page
            HttpGet httpGet = new HttpGet(url);

            // httpResponse executes httpGet
            HttpResponse httpResponse = httpClient.execute(httpGet);

            // Obtains the message entity of this response, if any.
            httpEntity = httpResponse.getEntity();

        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(httpEntity != null){
            try{
                String entityResponse = EntityUtils.toString(httpEntity);
                entityResponse = "["+entityResponse+"]";
                Log.v("--Entity Response--",entityResponse);

                jsonArray = new JSONArray(entityResponse);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

    return jsonArray;
    }
}
