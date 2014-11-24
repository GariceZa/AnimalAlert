package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity {

    private EditText email,password;
    private String userEmail,userPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email       = (EditText)findViewById(R.id.username);
        password    = (EditText)findViewById(R.id.password);
    }

    public void login(View view) {

        userEmail      = email.getText().toString();
        userPassword   = password.getText().toString();

        new LoginResponse().execute(new LoginAPI(userEmail,userPassword));

    }

    public void register(View view) {
       // create GPS object
       GPS gps = new GPS(this);

        // if google play services installed
       if (gps.getGooglePlayService()) {
           // launch the registration dialog
            startActivity(new Intent(this,RegistrationDialog.class));
       }
    }

    // setup the progress dialog
    protected void startLoginProgressDialog(){

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        //-------------------
    }

    // dismisses the progress dialog
    protected void stopLoginProgressDialog(){

        progressDialog.dismiss();
    }

    // AsyncTask to check users credentials on background thread
    private class LoginResponse extends AsyncTask<LoginAPI,Long,JSONArray>{

        @Override
        protected void onPreExecute() {
            startLoginProgressDialog();
        }

        @Override
        protected JSONArray doInBackground(LoginAPI... params) {
            return params[0].getLoginResponse();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            String response;

            for(int i = 0; i <jsonArray.length();i++){
                try{
                    JSONObject json = jsonArray.getJSONObject(i);

                    // stores result from login.php
                    response = json.getString("response");

                    Log.v("--GET RESPONSE--",response);

                    if(response.equals("true")){
                        Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Username/password incorrect",Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    stopLoginProgressDialog();
                }

            }
        }
    }
}