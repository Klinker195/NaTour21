package edu.unina.natour21.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.amplifyframework.core.Amplify;

public class GoogleLoginCallbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Amplify.Auth.handleWebUISignInResponse(getIntent());

        //setContentView(R.layout.activity_google_login_success);

        if(Amplify.Auth.getCurrentUser() != null) Log.i("AmplifyGoogle", Amplify.Auth.getCurrentUser().getUsername());

        Intent switchActivityIntent = new Intent(GoogleLoginCallbackActivity.this, AuthenticationActivity.class);
        finish();
        startActivity(switchActivityIntent);
    }

}