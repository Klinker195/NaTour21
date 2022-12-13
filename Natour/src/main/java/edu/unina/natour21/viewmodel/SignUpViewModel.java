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

import edu.unina.natour21.view.activity.PostFilteringMapsActivity;
import edu.unina.natour21.view.activity.SignUpActivity;

public class SignUpViewModel extends ViewModelBase {

    private static final String TAG = SignUpViewModel.class.getSimpleName();

    private final MutableLiveData<AuthException> onSignUpFailure = new MutableLiveData<>();
    private final MutableLiveData<AuthSignUpResult> onSignUpSuccess = new MutableLiveData<>();

    public boolean checkPasswordAndPasswordConfirmationValidity(String password, String passwordConfirmation) {
        return !password.isEmpty() && !passwordConfirmation.isEmpty();
    }

    public boolean checkPasswordAndPasswordConfirmationMatch(String password, String passwordConfirmation) {
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
