package edu.unina.natour21.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

import edu.unina.natour21.model.User;

public class NatourApplication extends Application {

    // Giusto?
    private static User currentUser;
    private static AuthUser currentAmplifyUser;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());
            Log.i("NatourAmplify", "Initialized Amplify");
        } catch(AmplifyException error) {
            Log.e("NatourAmplify", "Could not initialize Amplify", error);
        }

        // TODO Amplify fetch current user/session
        currentAmplifyUser = Amplify.Auth.getCurrentUser();
        if(currentAmplifyUser != null) Log.i("NatourAmplify", "User already logged in");

        // SharedPreferences sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        // sharedPrefs.edit().putBoolean("firstAccess", true);

    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        NatourApplication.currentUser = currentUser;
    }

    public static AuthUser getCurrentAmplifyUser() {
        return currentAmplifyUser;
    }

    public static void setCurrentAmplifyUser(AuthUser currentAmplifyUser) {
        NatourApplication.currentAmplifyUser = currentAmplifyUser;
    }

}
