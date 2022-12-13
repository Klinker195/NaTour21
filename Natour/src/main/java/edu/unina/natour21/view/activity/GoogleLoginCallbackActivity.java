package edu.unina.natour21.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.core.Amplify;
import com.google.firebase.analytics.FirebaseAnalytics;

public class GoogleLoginCallbackActivity extends AppCompatActivity {

    private static final String TAG = GoogleLoginCallbackActivity.class.getSimpleName();

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Amplify.Auth.handleWebUISignInResponse(getIntent());

        if (Amplify.Auth.getCurrentUser() != null)
            Log.i(TAG, Amplify.Auth.getCurrentUser().getUsername());

        Intent switchActivityIntent = new Intent(GoogleLoginCallbackActivity.this, AuthenticationActivity.class);
        finish();
        startActivity(switchActivityIntent);
    }

}