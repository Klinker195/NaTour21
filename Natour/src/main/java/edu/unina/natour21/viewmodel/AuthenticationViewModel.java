package edu.unina.natour21.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.result.AuthResetPasswordResult;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unina.natour21.dto.UserDTO;
import edu.unina.natour21.retrofit.AmazonAPI;
import edu.unina.natour21.retrofit.IUserAPI;
import edu.unina.natour21.view.activity.AuthenticationActivity;
import edu.unina.natour21.view.activity.PostFilteringMapsActivity;
import edu.unina.natour21.view.activity.RegistrationFormActivity;
import edu.unina.natour21.view.activity.RouteExplorationActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationViewModel extends ViewModelBase {

    private static final String TAG = AuthenticationViewModel.class.getSimpleName();

    private final MutableLiveData<AuthException> onLoginError = new MutableLiveData<>();
    private final MutableLiveData<AuthSignInResult> onLoginSuccess = new MutableLiveData<>();
    private final MutableLiveData<AuthResetPasswordResult> onResetPasswordRequestSuccess = new MutableLiveData<>();
    private final MutableLiveData<AuthException> onResetPasswordRequestError = new MutableLiveData<>();
    private final MutableLiveData<AuthSignInResult> onGoogleSignInSuccess = new MutableLiveData<>();
    private final MutableLiveData<AuthException> onGoogleSignInFailure = new MutableLiveData<>();
    private final MutableLiveData<AuthException> onCheckInfoFailure = new MutableLiveData<>();

    private static String userEmail;

    private static void setUserEmail(String email) {
        userEmail = email;
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
                AuthenticationViewModel.this::onGoogleSignInSuccess,
                AuthenticationViewModel.this::onGoogleSignInFailure
        );
    }


    public void onGoogleSignInSuccess(AuthSignInResult result) {
        Log.i("AmplifyGoogleViewModel", result.toString());
        onGoogleSignInSuccess.postValue(result);
    }

    public void onGoogleSignInFailure(AuthException error) {
        Log.e("AmplifyGoogleViewModel", error.toString());
        onGoogleSignInFailure.postValue(error);
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

    public void checkUserInfoAndRedirect(Activity activity) {
        IUserAPI userAPI = AmazonAPI.getClient().create(IUserAPI.class);

        Amplify.Auth.fetchUserAttributes(
                new Consumer<List<AuthUserAttribute>>() {
                    @Override
                    public void accept(@NonNull List<AuthUserAttribute> value) {
                        for (int i = 0; i < value.size(); i++) {
                            if (value.get(i).getKey().getKeyString().equals("email")) {
                                Log.i("AuthAttributes", "Key: " + value.get(i).getKey().getKeyString() + " | Value: " + value.get(i).getValue());
                                AuthenticationViewModel.setUserEmail(value.get(i).getValue());
                                AuthenticationViewModel.userEmail = AuthenticationViewModel.userEmail.replace(" ", "").toLowerCase();

                                Call<UserDTO> call = userAPI.getUserByEmail(AuthenticationViewModel.userEmail);
                                call.enqueue(new Callback<UserDTO>() {
                                    @Override
                                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                        UserDTO userDTO = response.body();

                                        if (userDTO == null) {
                                            Log.i(TAG, "User not in DB");
                                            Intent switchActivityIntent = new Intent(activity, RegistrationFormActivity.class);
                                            activity.startActivity(switchActivityIntent);
                                            return;
                                        }

                                        Log.i(TAG, "User already in DB");
                                        Intent switchActivityIntent = new Intent(activity, RouteExplorationActivity.class);
                                        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        activity.finish();
                                        activity.startActivity(switchActivityIntent);
                                    }

                                    @Override
                                    public void onFailure(Call<UserDTO> call, Throwable t) {
                                        Log.e(TAG, "Request failed");
                                        t.printStackTrace();
                                        Intent switchActivityIntent = new Intent(activity, AuthenticationActivity.class);
                                        activity.startActivity(switchActivityIntent);
                                        signOut();
                                        activity.finish();
                                    }
                                });

                            }
                        }
                    }
                },
                new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException value) {
                        onCheckInfoFailure.postValue(value);
                    }
                }
        );

    }


    public LiveData<AuthException> getLoginError() {
        return onLoginError;
    }

    public LiveData<AuthSignInResult> getLoginSuccess() {
        return onLoginSuccess;
    }

    public LiveData<AuthSignInResult> getGoogleLoginSuccess() {
        return onGoogleSignInSuccess;
    }

    public LiveData<AuthException> getGoogleLoginError() {
        return onGoogleSignInFailure;
    }

    public LiveData<AuthResetPasswordResult> getResetPasswordSuccess() {
        return onResetPasswordRequestSuccess;
    }

    public LiveData<AuthException> getResetPasswordError() {
        return onResetPasswordRequestError;
    }

    public MutableLiveData<AuthException> getOnCheckInfoFailure() {
        return onCheckInfoFailure;
    }
}
