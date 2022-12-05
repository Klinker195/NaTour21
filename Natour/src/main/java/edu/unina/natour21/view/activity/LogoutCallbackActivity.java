package edu.unina.natour21.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.core.Amplify;

import edu.unina.natour21.R;

public class LogoutCallbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_google_login_failure);

        Amplify.Auth.handleWebUISignInResponse(getIntent());

        if(Amplify.Auth.getCurrentUser() != null) Log.i("AmplifyGoogle", Amplify.Auth.getCurrentUser().getUsername());

        Intent switchActivityIntent = new Intent(LogoutCallbackActivity.this, AuthenticationActivity.class);
        finish();
        startActivity(switchActivityIntent);
    }

}