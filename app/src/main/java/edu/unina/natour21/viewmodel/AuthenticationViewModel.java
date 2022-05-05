package edu.unina.natour21.viewmodel;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.auth.options.AuthWebUISignInOptions;
import com.amplifyframework.auth.result.AuthResetPasswordResult;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.core.Amplify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unina.natour21.view.activity.AuthenticationActivity;

public class AuthenticationViewModel extends ViewModel {

    private final MutableLiveData<AuthException> onLoginError = new MutableLiveData<>();
    private final MutableLiveData<AuthSignInResult> onLoginSuccess = new MutableLiveData<>();
    private final MutableLiveData<AuthResetPasswordResult> onResetPasswordRequestSuccess = new MutableLiveData<>();
    private final MutableLiveData<AuthException> onResetPasswordRequestError = new MutableLiveData<>();

    public void signOut() {

        if(Amplify.Auth.getCurrentUser() != null) Amplify.Auth.signOut(
                () -> Log.i("AmplifySignOut", "Sign out success"),
                error -> Log.e("AmplifySignOut", error.toString())
        );

    }

    public void signIn(String email, String password) {
        Amplify.Auth.signIn(
                email,
                password,
                AuthenticationViewModel.this::onLoginSuccess,
                AuthenticationViewModel.this::onLoginError
        );
    }

    public void googleSignIn(Activity activity) {
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), activity,
                result -> Log.i("AmplifyGoogleViewModel", result.toString()),
                error -> Log.e("AmplifyGoogleViewModel", error.toString())
        );
    }

    public void sendResetPasswordRequest(String email) {
        Amplify.Auth.resetPassword(
                email,
                AuthenticationViewModel.this::onResetPasswordRequestSuccess,
                AuthenticationViewModel.this::onResetPasswordRequestError
        );
    }

    public void onResetPasswordRequestSuccess(AuthResetPasswordResult authResetPasswordResult) {
        onResetPasswordRequestSuccess.postValue(authResetPasswordResult);
    }

    public void onResetPasswordRequestError(AuthException error) {
        onResetPasswordRequestError.postValue(error);
    }

    public void onLoginSuccess(AuthSignInResult authSignInResult) {
        onLoginSuccess.postValue(authSignInResult);
    }

    public void onLoginError(AuthException error) {
        onLoginError.postValue(error);
    }

    public boolean checkFieldsValidity(String email, String password) {
        return checkEmailFieldValidity(email) && !password.isEmpty();
    }

    public boolean checkEmailFieldValidity(String email) {
        boolean check = false;

        if(!email.isEmpty()) {
            Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
            Matcher matcher = pattern.matcher(email);
            if(matcher.matches()) {
                check = true;
            }
        }

        return check;
    }

    public LiveData<AuthException> getLoginError() {
        return onLoginError;
    }

    public LiveData<AuthSignInResult> getLoginSuccess() {
        return onLoginSuccess;
    }

    public LiveData<AuthResetPasswordResult> getResetPasswordSuccess() { return onResetPasswordRequestSuccess; }

    public LiveData<AuthException> getResetPasswordError() { return onResetPasswordRequestError; }
}
