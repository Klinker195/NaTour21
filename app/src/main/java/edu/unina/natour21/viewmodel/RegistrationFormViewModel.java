package edu.unina.natour21.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.amplifyframework.core.Amplify;

public class RegistrationFormViewModel extends ViewModel {

    public void signOut() {
        Amplify.Auth.fetchAuthSession(
                success -> { if(success.isSignedIn()) Amplify.Auth.signOut(
                        () -> Log.i("AmplifySignOut", "Sign out success"),
                        error -> Log.e("AmplifySignOut", error.toString())
                ); },
                error -> Log.e("AmplifyFetchAuth", error.toString())
        );
    }

}
