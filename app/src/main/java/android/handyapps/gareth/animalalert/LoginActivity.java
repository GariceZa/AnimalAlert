package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
    private void startLoginProgressDialog(){

        String title = getResources().getString(R.string.logging_in);
        String message = getResources().getString(R.string.please_wait);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

    }

    // dismisses the progress dialog
    private void stopLoginProgressDialog(){

        progressDialog.dismiss();
    }

    // displays an alert dialog if the users credentials are incorrect
    private void loginError(){

        String error    = getResources().getString(R.string.error);
        String message  = getResources().getString(R.string.username_password_incorrect);
        String ok       = getResources().getString(R.string.ok);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(error);
        builder.setMessage(message);
        builder.setPositiveButton(ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog loginFailed = builder.create();
        loginFailed.show();
    }

    // AsyncTask to check users credentials on background thread
    private class LoginResponse extends AsyncTask<LoginAPI,Long,JSONArray>{

        @Override
        protected void onPreExecute() {
            // start the progress wheel
            startLoginProgressDialog();
        }

        @Override
        protected JSONArray doInBackground(LoginAPI... params) {
            // return the response from getLoginResponse
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

                    if(response.equals("true")){
                        finish();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }
                    else{
                        loginError();
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