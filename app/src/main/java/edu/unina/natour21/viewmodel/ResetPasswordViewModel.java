package edu.unina.natour21.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.core.Amplify;

public class ResetPasswordViewModel extends ViewModel {

    private final MutableLiveData<AuthException> onPasswordChangeError = new MutableLiveData<>();
    private final MutableLiveData<Void> onPasswordChangeSuccess = new MutableLiveData<>();

    public boolean checkFieldsValidity(String verificationCode, String password, String passwordConfirmation) {
        return !verificationCode.isEmpty() && !password.isEmpty() && !passwordConfirmation.isEmpty();
    }

    public boolean checkPasswordMatch(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    public void confirmResetPassword(String verificationCode, String password) {
        Amplify.Auth.confirmResetPassword(
                password,
                verificationCode,
                ResetPasswordViewModel.this::onPasswordResetSuccess,
                ResetPasswordViewModel.this::onPasswordResetFailure
        );
    }

    private void onPasswordResetFailure(AuthException error) {
        onPasswordChangeError.postValue(error);
    }

    private void onPasswordResetSuccess() {
        onPasswordChangeSuccess.postValue(null);
    }

    public LiveData<AuthException> getPasswordChangeError() {
        return onPasswordChangeError;
    }

    public LiveData<Void> getPasswordChangeSuccess() {
        return onPasswordChangeSuccess;
    }

}
