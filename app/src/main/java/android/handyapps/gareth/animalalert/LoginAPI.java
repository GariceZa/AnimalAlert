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

    public LoginAPI(String em, String pass){
        email = em;
        password = pass;
    }

    public JSONArray getLoginResponse(){

        String url = "http://animalalert.garethprice.co.za/login.php?email=" + email + "&password=" + password;
        HttpEntity httpEntity;
        JSONArray jsonArray = null;

        try {
            // Creating a new DefaultHttpClient to establish connection
            DefaultHttpClient httpClient = new DefaultHttpClient();

            // retrieves information from the url
            HttpGet httpGet = new HttpGet(url);

            // the http response
            HttpResponse httpResponse = httpClient.execute(httpGet);

            // Obtains the message entity of this response
            httpEntity = httpResponse.getEntity();

            if (httpEntity != null) {

                try {
                    String entityResponse = EntityUtils.toString(httpEntity);
                    entityResponse = "[" + entityResponse + "]";
                    Log.v("entityResponse",entityResponse);
                    jsonArray = new JSONArray(entityResponse);
                }
                catch (IOException e) {
                    Log.v("--IOException--", e.toString());
                }
                catch (JSONException e) {
                    Log.v("--JSONException--",e.toString());
                }
            }
        }
        catch (ClientProtocolException e) {
            Log.v("--ClientProtocolException--", e.toString());
        }
        catch (IOException e) {
            Log.v("--IOException--", e.toString());
        }

    return jsonArray;
    }
}
