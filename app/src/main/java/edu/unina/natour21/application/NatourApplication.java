package edu.unina.natour21.application;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

import edu.unina.natour21.model.User;

public class NatourApplication extends Application {

    // Giusto?
    private static User currentUser;

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
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        NatourApplication.currentUser = currentUser;
    }

}
