package edu.unina.natour21.application;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;

import edu.unina.natour21.model.User;

public class NatourApplication extends Application {

    private static final String TAG = NatourApplication.class.getSimpleName();

    private static User currentUser;
    private static AuthUser currentAmplifyUser;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "Initialized Amplify");
        } catch(AmplifyException error) {
            Log.e(TAG, "Could not initialize Amplify", error);
        }

        // Amplify fetch current user/session
        currentAmplifyUser = Amplify.Auth.getCurrentUser();
        if(currentAmplifyUser != null) Log.i(TAG, "User already logged in");

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
