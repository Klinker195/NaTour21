package edu.unina.natour21.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.result.AuthResetPasswordResult;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.step.AuthSignInStep;
import com.amplifyframework.core.Amplify;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.AmplifyExceptionHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.AuthenticationViewModel;

public class AuthenticationActivity extends AppCompatActivity {

    private AuthenticationViewModel viewModel;

    private TextView signInTextView;
    private EditText emailEditTextView;
    private EditText passwordEditTextView;
    private Button signInButton;
    private Button signInWithGoogleButton;
    private Button registerButton;
    private TextView errorTextView;
    private TextView forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        viewModel.signOut();

        // Set Button Listeners
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditTextView.getText().toString();
                email = email.replace(" ", "").toLowerCase();
                emailEditTextView.setText(email);
                String password = passwordEditTextView.getText().toString();
                if(viewModel.checkFieldsValidity(email, password)) {
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
                Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), AuthenticationActivity.this,
                        result -> Log.i("AuthQuickstart", result.toString()),
                        error -> Log.e("AuthQuickstart", error.toString())
                );
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
                if(viewModel.checkEmailFieldValidity(email)) {
                    viewModel.sendResetPasswordRequest(email);
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Email is incorrect.");
                }
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
                AuthSignInStep signInStep = authSignInResult.getNextStep().getSignInStep();
                errorTextView.setVisibility(View.INVISIBLE);

                /*
                if(signInStep == AuthSignInStep.CONFIRM_SIGN_IN_WITH_NEW_PASSWORD) {
                    Intent switchActivityIntent = new Intent(AuthenticationActivity.this, ForcedResetPasswordActivity.class);
                    startActivity(switchActivityIntent);
                } */

                if(signInStep == AuthSignInStep.RESET_PASSWORD) {
                    String email = emailEditTextView.getText().toString();
                    email = email.replace(" ", "").toLowerCase();
                    emailEditTextView.setText(email);
                    if(viewModel.checkEmailFieldValidity(email)) {
                        viewModel.sendResetPasswordRequest(email);
                    } else {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("Email is incorrect.");
                    }
                } else if(signInStep == AuthSignInStep.DONE) {
                    // TODO Start main app (dashboard)
                } else {
                    viewModel.onLoginError(new AuthException("Generic issue", "Try again."));
                }
            }
        });

        // Amplify Login Error
        viewModel.getLoginError().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                errorTextView.setVisibility(View.VISIBLE);

                if(error instanceof AuthException.UserNotConfirmedException) {
                    errorTextView.setVisibility(View.INVISIBLE);
                    Log.i("Amplify", "Sign-Up confirmation needed");
                    Intent switchActivityIntent = new Intent(AuthenticationActivity.this, VerificationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("email", emailEditTextView.getText().toString().replace(" ", "").toLowerCase());
                    switchActivityIntent.putExtras(bundle);
                    startActivity(switchActivityIntent);
                    passwordEditTextView.setText("");
                } else {
                    AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                    errorTextView.setText(exceptionHandler.getStringFromAuthException(error));
                }

            }
        });

        viewModel.getResetPasswordSuccess().observe(this, new Observer<AuthResetPasswordResult>() {
            @Override
            public void onChanged(AuthResetPasswordResult authResetPasswordResult) {
                errorTextView.setVisibility(View.INVISIBLE);
                Intent switchActivityIntent = new Intent(AuthenticationActivity.this, ResetPasswordActivity.class);
                startActivity(switchActivityIntent);
                passwordEditTextView.setText("");
            }
        });

        viewModel.getResetPasswordError().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                Log.i("Amplify", "Reset Error");
                Log.i("Amplify", error.getMessage() + "\n\n" + error.toString());
                errorTextView.setVisibility(View.VISIBLE);
                AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                errorTextView.setText(exceptionHandler.getStringFromAuthException(error));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) {
            Amplify.Auth.handleWebUISignInResponse(data);
        }
    }

}