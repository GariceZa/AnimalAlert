package android.handyapps.gareth.animalalert;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {

        Toast.makeText(getApplicationContext(),"You Clicked login",Toast.LENGTH_SHORT).show();
    }

    public void register(View view) {

        Toast.makeText(getApplicationContext(),"You Clicked Register",Toast.LENGTH_SHORT).show();
    }
}
