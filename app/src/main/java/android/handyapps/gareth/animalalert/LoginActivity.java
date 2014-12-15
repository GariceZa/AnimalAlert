package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.andreabaccega.widget.FormEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class LoginActivity extends Activity {

    private FormEditText email,password;
    private String userEmail,userPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email       = (FormEditText)findViewById(R.id.username);
        password    = (FormEditText)findViewById(R.id.password);

        // If email & password shared preferences are saved then login
        File prefsFile = new File("/data/data/" + getPackageName() +  "/shared_prefs/" + "userInfo.xml");
        if(prefsFile.exists()){
            Preferences prefs = new Preferences();
            new LoginResponse().execute(new LoginAPI(prefs.getEmailSharedPrefs(this),prefs.getPasswordSharedPrefs(this)));
        }
        //-------------------------------------
    }

    // Called when the user clicks login
    public void login(View view) {

        FormEditText[] loginFields = {email,password};
        boolean fieldsValid = true;

        for(FormEditText field: loginFields){
            fieldsValid = field.testValidity() && fieldsValid;
        }

        if(fieldsValid){
            userEmail      = email.getText().toString().trim();
            userPassword   = password.getText().toString().trim();
            new LoginResponse().execute(new LoginAPI(userEmail,userPassword));
        }


//        userEmail      = email.getText().toString().trim();
//        userPassword   = password.getText().toString().trim();
//
//        // If the the username or password field is empty
//        if(userEmail.isEmpty() || userPassword.isEmpty()){
//            // Show this error
//            exceptionError(getResources().getString(R.string.username_password_empty));
//        }
//        else
//        // Validate the users credentials
//        new LoginResponse().execute(new LoginAPI(userEmail,userPassword));

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

    // setup the progress dialog wheel
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

    // Used to display and errors with an alert dialog
    private void exceptionError(String errResponse){

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(R.string.error);
        builder.setMessage(errResponse);
        builder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog exception = builder.create();
        exception.show();

    }

    // AsyncTask to check users credentials on background thread
    private class LoginResponse extends AsyncTask<LoginAPI,Long,JSONArray> {

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

            try {
                String response;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    // stores result from login.php
                    response = json.getString("response");

                    if (response.equals("true")) {
                        finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        exceptionError(getResources().getString(R.string.username_password_incorrect));
                    }
                }
            }
            catch (JSONException e) {
                exceptionError(e.toString());
            }
            catch(NullPointerException e){
                exceptionError(e.toString());
            }
            catch(Exception e){
                exceptionError(e.toString());
            }
            finally {
                stopLoginProgressDialog();
            }
        }
    }
}