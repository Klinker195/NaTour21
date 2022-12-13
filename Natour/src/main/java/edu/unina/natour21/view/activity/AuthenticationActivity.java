package edu.unina.natour21.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthResetPasswordResult;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.step.AuthSignInStep;
import com.amplifyframework.core.Amplify;
import com.google.firebase.analytics.FirebaseAnalytics;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.AmplifyExceptionHandler;
import edu.unina.natour21.utility.KeyboardHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.AuthenticationViewModel;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = AuthenticationActivity.class.getSimpleName();

    private FirebaseAnalytics firebaseAnalytics;

    private AuthenticationViewModel viewModel;

    private TextView signInTextView;
    private EditText emailEditTextView;
    private EditText passwordEditTextView;
    private Button signInButton;
    private Button signInWithGoogleButton;
    private Button registerButton;
    private TextView errorTextView;
    private TextView forgotPasswordTextView;

    private Dialog loadingDialog;

    @Override
    protected void onResume() {
        super.onResume();
        KeyboardHandler.hideKeyboard(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        KeyboardHandler.hideKeyboard(this);

        // Retrofit API declaration
        // userAPI = AmazonAPI.getClient().create(IUserAPI.class);

        // Set ViewModel
        viewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setFullscreen(this);

        // Set Content View
        setContentView(R.layout.activity_authentication);

        // Find Views
        signInTextView = (TextView) findViewById(R.id.authenticationSignInTextView);
        emailEditTextView = (EditText) findViewById(R.id.authenticationEditTextEmailAddress);
        passwordEditTextView = (EditText) findViewById(R.id.authenticationEditTextPassword);
        signInButton = (Button) findViewById(R.id.authenticationSignInButton);
        signInWithGoogleButton = (Button) findViewById(R.id.authenticationGoogleSignInButton);
        registerButton = (Button) findViewById(R.id.authenticationRegisterButton);
        errorTextView = (TextView) findViewById(R.id.authenticationErrorTextView);
        forgotPasswordTextView = (TextView) findViewById(R.id.authenticationForgotPasswordClickableTextView);

        // Set Gradients
        designHandler.setTextGradient(signInTextView);
        designHandler.setTextGradient(signInButton);
        designHandler.setTextGradient(registerButton);
        designHandler.setTextGradient(signInWithGoogleButton);
        designHandler.setTextGradient(forgotPasswordTextView);

        // Amplify Sign Out
        // viewModel.signOut();

        // Set Button Listeners
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardHandler.hideKeyboard(AuthenticationActivity.this);
                errorTextView.setVisibility(View.INVISIBLE);
                errorTextView.setText("");
                String email = emailEditTextView.getText().toString();
                email = email.replace(" ", "").toLowerCase();
                emailEditTextView.setText(email);
                String password = passwordEditTextView.getText().toString();
                if (viewModel.checkEmailAndPasswordValidity(email, password)) {
                    showLoadingDialog();
                    viewModel.signIn(email, password);
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Something went wrong.");
                }
            }
        });

        signInWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();
                viewModel.googleSignIn(AuthenticationActivity.this);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(AuthenticationActivity.this, SignUpActivity.class);
                startActivity(switchActivityIntent);
                passwordEditTextView.setText("");
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditTextView.getText().toString();
                email = email.replace(" ", "").toLowerCase();
                emailEditTextView.setText(email);
                if (viewModel.checkEmailValidity(email)) {
                    showLoadingDialog();
                    viewModel.sendResetPasswordRequest(email);
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Email is incorrect.");
                }
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.signOut();
            }
        });

        // Set ViewModel Observers
        observeViewModel();
    }


    private void observeViewModel() {
        // Amplify Login Success
        viewModel.getLoginSuccess().observe(this, new Observer<AuthSignInResult>() {
            @Override
            public void onChanged(AuthSignInResult authSignInResult) {
                dismissLoadingDialog();
                AuthSignInStep signInStep = authSignInResult.getNextStep().getSignInStep();
                errorTextView.setVisibility(View.INVISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Amplify login");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

                if (signInStep == AuthSignInStep.RESET_PASSWORD) {
                    String email = emailEditTextView.getText().toString();
                    email = email.replace(" ", "").toLowerCase();
                    emailEditTextView.setText(email);
                    if (viewModel.checkEmailValidity(email)) {
                        viewModel.sendResetPasswordRequest(email);
                    } else {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("Email is incorrect.");
                    }
                } else if (signInStep == AuthSignInStep.DONE) {
                    emailEditTextView.setText("");
                    passwordEditTextView.setText("");
                    Log.i("AmplifyLogin", Amplify.Auth.getCurrentUser().toString());
                    viewModel.checkUserInfoAndRedirect(AuthenticationActivity.this);
                } else {
                    viewModel.onLoginError(new AuthException("Generic issue", "Try again."));
                }
            }
        });

        // Amplify Login Error
        viewModel.getLoginError().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                dismissLoadingDialog();
                errorTextView.setVisibility(View.VISIBLE);
                passwordEditTextView.setText("");

                if (error instanceof AuthException.UserNotConfirmedException) {
                    errorTextView.setVisibility(View.INVISIBLE);
                    Log.i("Amplify", "Sign-Up confirmation needed");
                    Intent switchActivityIntent = new Intent(AuthenticationActivity.this, VerificationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("email", emailEditTextView.getText().toString().replace(" ", "").toLowerCase());
                    switchActivityIntent.putExtras(bundle);
                    startActivity(switchActivityIntent);
                } else {
                    AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                    errorTextView.setText(exceptionHandler.getStringFromAuthException(error));
                }

            }
        });

        // Amplify Google Login Success
        viewModel.getGoogleLoginSuccess().observe(this, new Observer<AuthSignInResult>() {
            @Override
            public void onChanged(AuthSignInResult authSignInResult) {
                dismissLoadingDialog();
                AuthSignInStep signInStep = authSignInResult.getNextStep().getSignInStep();
                errorTextView.setVisibility(View.INVISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Amplify Google login");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

                if (signInStep == AuthSignInStep.RESET_PASSWORD) {
                    String email = emailEditTextView.getText().toString();
                    email = email.replace(" ", "").toLowerCase();
                    emailEditTextView.setText(email);
                    if (viewModel.checkEmailValidity(email)) {
                        viewModel.sendResetPasswordRequest(email);
                    } else {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("Email is incorrect.");
                    }
                } else if (signInStep == AuthSignInStep.DONE) {
                    emailEditTextView.setText("");
                    passwordEditTextView.setText("");
                    Log.i("AmplifyGoogleLogin", Amplify.Auth.getCurrentUser().toString());
                    viewModel.checkUserInfoAndRedirect(AuthenticationActivity.this);
                } else {
                    viewModel.onLoginError(new AuthException("Generic issue", "Try again."));
                }
            }
        });

        // Amplify Google Login Error
        viewModel.getGoogleLoginError().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                dismissLoadingDialog();
                errorTextView.setVisibility(View.VISIBLE);

                AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                errorTextView.setText(exceptionHandler.getStringFromAuthException(error));

                emailEditTextView.setText("");
                passwordEditTextView.setText("");
            }
        });

        viewModel.getResetPasswordSuccess().observe(this, new Observer<AuthResetPasswordResult>() {
            @Override
            public void onChanged(AuthResetPasswordResult authResetPasswordResult) {
                dismissLoadingDialog();
                errorTextView.setVisibility(View.INVISIBLE);
                Intent switchActivityIntent = new Intent(AuthenticationActivity.this, ResetPasswordActivity.class);
                startActivity(switchActivityIntent);
                passwordEditTextView.setText("");
            }
        });

        viewModel.getResetPasswordError().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                dismissLoadingDialog();
                Log.i("Amplify", "Reset Error");
                Log.i("Amplify", error.getMessage() + "\n\n" + error.toString());
                errorTextView.setVisibility(View.VISIBLE);
                AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                errorTextView.setText(exceptionHandler.getStringFromAuthException(error));
            }
        });

        viewModel.getOnCheckInfoFailure().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                dismissLoadingDialog();
                Log.e("NatourAuth", error.toString());
                viewModel.signOut();

                AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                errorTextView.setText(exceptionHandler.getStringFromAuthException(error));

                emailEditTextView.setText("");
                passwordEditTextView.setText("");
            }
        });
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new Dialog(this);
        }
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.popup_loading);

        ProgressBar loadingDialogProgressBar = (ProgressBar) findViewById(R.id.popupLoadingProgressBar);

        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}

