package edu.unina.natour21.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpViewModel extends ViewModel {

    private final MutableLiveData<AuthException> onSignUpFailure = new MutableLiveData<>();
    private final MutableLiveData<AuthSignUpResult> onSignUpSuccess = new MutableLiveData<>();

    public void signOut() {
        Amplify.Auth.fetchAuthSession(
                success -> { if(success.isSignedIn()) Amplify.Auth.signOut(
                        () -> Log.i("AmplifySignOut", "Sign out success"),
                        error -> Log.e("AmplifySignOut", error.toString())
                ); },
                error -> Log.e("AmplifyFetchAuth", error.toString())
        );
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

    public boolean checkPasswordFieldsValidity(String password, String passwordConfirmation) {
        return !password.isEmpty() && !passwordConfirmation.isEmpty();
    }

    public boolean checkPasswordMatch(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    public void signUp(String email, String password) {
        AuthSignUpOptions options = AuthSignUpOptions.builder().build();

        Amplify.Auth.signUp(
                email,
                password,
                options,
                SignUpViewModel.this::onSignUpSuccess,
                SignUpViewModel.this::onSignUpFailure
        );
    }

    private void onSignUpFailure(AuthException error) {
        Log.i("Amplify", error.getMessage());
        onSignUpFailure.postValue(error);
    }

    private void onSignUpSuccess(AuthSignUpResult authSignUpResult) {
        onSignUpSuccess.postValue(authSignUpResult);
    }

    public LiveData<AuthException> getOnSignUpFailure() {
        return onSignUpFailure;
    }

    public LiveData<AuthSignUpResult> getOnSignUpSuccess() {
        return onSignUpSuccess;
    }




}
