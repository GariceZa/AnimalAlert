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
import java.net.URLEncoder;

/**
 * Created by Gareth on 2014-11-24.
 */
public class RegistrationAPI {

    private String name,surname,email,password,address;
    private double latitude,longitude;

    public RegistrationAPI(String na,String su,String em,String pass,String add,Double lat,Double lon){
        name        = na;
        surname     = su;
        email       = em;
        password    = pass;
        address     = add;
        latitude    = lat;
        longitude   = lon;

    }
    // // Returns the api response
    public JSONArray getRegistrationResponse(){

        JSONArray jsonArray = null;
        try {

            String url = "http://animalalert.garethprice.co.za/registerUser.php?name=" + name + "&surname=" + surname + "&email=" + email + "&password=" + password + "&address=" + URLEncoder.encode(address, "UTF-8") + "&latitude=" + latitude + "&longitude=" + longitude;

            HttpEntity httpEntity;

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
                    Log.v("entityResponse", entityResponse);
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
