package edu.unina.natour21.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.core.Amplify;

import edu.unina.natour21.R;

public class GoogleLoginFailureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login_failure);
        if(Amplify.Auth.getCurrentUser() != null) Log.i("AmplifyGoogle", Amplify.Auth.getCurrentUser().getUsername());
    }
}