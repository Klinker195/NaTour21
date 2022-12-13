package edu.unina.natour21.viewmodel;

import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;

import edu.unina.natour21.view.activity.PostFilteringMapsActivity;
import edu.unina.natour21.view.activity.VerificationActivity;

public class VerificationViewModel extends ViewModelBase {

    private static final String TAG = VerificationViewModel.class.getSimpleName();

    private final MutableLiveData<String> onTimerUpdate = new MutableLiveData<>();
    private final MutableLiveData<Void> onTimerFinish = new MutableLiveData<>();

    private final MutableLiveData<AuthException> onSignUpConfirmationFailure = new MutableLiveData<>();
    private final MutableLiveData<AuthSignUpResult> onSignUpConfirmationSuccess = new MutableLiveData<>();

    private final MutableLiveData<AuthSignUpResult> onResendCodeSuccess = new MutableLiveData<>();
    private final MutableLiveData<AuthException> onResendCodeFailure = new MutableLiveData<>();

    public void startCodeTimer(long currentMillis) {

        new CountDownTimer(currentMillis, 1000) {

            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                Integer adjustedMinutes = Math.toIntExact(minutes);
                Integer adjustedSeconds = Math.toIntExact(seconds);
                String adjustedTime;

                if (adjustedSeconds < 10) {
                    adjustedTime = adjustedMinutes.toString() + ":" + "0" + adjustedSeconds.toString();
                } else {
                    adjustedTime = adjustedMinutes.toString() + ":" + adjustedSeconds.toString();
                }

                onTimerUpdate.postValue(adjustedTime);
            }

            public void onFinish() {
                onTimerFinish.postValue(null);
            }

        }.start();
    }

    public void resendCode(String email) {
        Amplify.Auth.resendSignUpCode(
                email,
                VerificationViewModel.this::onResendCodeSuccess,
                VerificationViewModel.this::onResendCodeFailure
        );
    }


    private void onResendCodeSuccess(AuthSignUpResult authSignUpResult) {
        Log.i("AmplifyCognito", authSignUpResult.toString());
        onResendCodeSuccess.postValue(authSignUpResult);
    }

    private void onResendCodeFailure(AuthException error) {
        Log.e("AmplifyCognito", error.getMessage());
        onResendCodeFailure.postValue(error);
    }

    public void signUpConfirmation(String email, String verificationCode) {
        Amplify.Auth.confirmSignUp(
                email,
                verificationCode,
                VerificationViewModel.this::onSignUpConfirmationSuccess,
                VerificationViewModel.this::onSignUpConfirmationFailure
        );
    }

    private void onSignUpConfirmationFailure(AuthException error) {
        Log.e("AmplifyCognito", error.getMessage());
        onSignUpConfirmationFailure.postValue(error);
    }

    private void onSignUpConfirmationSuccess(AuthSignUpResult authSignUpResult) {
        Log.i("AmplifyCognito", authSignUpResult.toString());
        onSignUpConfirmationSuccess.postValue(authSignUpResult);
    }


    public LiveData<AuthSignUpResult> getOnResendCodeSuccess() {
        return onResendCodeSuccess;
    }

    public LiveData<AuthException> getOnResendCodeFailure() {
        return onResendCodeFailure;
    }

    public LiveData<AuthException> getOnSignUpConfirmationFailure() {
        return onSignUpConfirmationFailure;
    }

    public LiveData<AuthSignUpResult> getOnSignUpConfirmationSuccess() {
        return onSignUpConfirmationSuccess;
    }

    public LiveData<String> getOnTimerUpdate() {
        return onTimerUpdate;
    }

    public LiveData<Void> getOnTimerFinish() {
        return onTimerFinish;
    }

}
