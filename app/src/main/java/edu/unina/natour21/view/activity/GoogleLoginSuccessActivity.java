package edu.unina.natour21.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amplifyframework.core.Amplify;

import edu.unina.natour21.R;

public class GoogleLoginSuccessActivity extends AppCompatActivity {

    Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login_success);
        if(Amplify.Auth.getCurrentUser() != null) Log.i("AmplifyGoogle", Amplify.Auth.getCurrentUser().getUsername());

        signOutButton = (Button) findViewById(R.id.button2);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("AmplifyCurrentUser", String.valueOf(Amplify.Auth.getCurrentUser() != null));

                if(Amplify.Auth.getCurrentUser() != null) Log.i("AmplifyCurrentUser", Amplify.Auth.getCurrentUser().toString());

                if(Amplify.Auth.getCurrentUser() != null) Amplify.Auth.signOut(
                        () -> Log.i("AmplifySignOut", "Sign out success"),
                        error -> Log.e("AmplifySignOut", error.toString())
                );
            }
        });
    }
}