package edu.unina.natour21.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.options.AuthSignInOptions;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.step.AuthNextSignInStep;
import com.amplifyframework.auth.result.step.AuthSignInStep;
import com.amplifyframework.core.Amplify;

import edu.unina.natour21.R;
import edu.unina.natour21.application.NatourApplication;
import edu.unina.natour21.utility.NatourUIDesignHelper;

public class AuthenticationActivity extends AppCompatActivity {

    TextView signInTextView;
    EditText emailEditTextView;
    EditText passwordEditTextView;
    Button signInButton;
    Button signInWithGoogleButton;
    Button registerButton;
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Standard UI
        NatourUIDesignHelper designHelper = new NatourUIDesignHelper();
        designHelper.setFullscreen(this);

        setContentView(R.layout.activity_authentication);

        // Find Views
        signInTextView = (TextView) findViewById(R.id.authenticationSignInTextView);
        emailEditTextView = (EditText) findViewById(R.id.authenticationEditTextEmailAddress);
        passwordEditTextView = (EditText) findViewById(R.id.authenticationEditTextPassword);
        signInButton = (Button) findViewById(R.id.authenticationSignInButton);
        signInWithGoogleButton = (Button) findViewById(R.id.authenticationGoogleSignInButton);
        registerButton = (Button) findViewById(R.id.authenticationRegisterButton);
        errorTextView = (TextView) findViewById(R.id.authenticationErrorTextView);

        // Set Gradients
        designHelper.setTextGradient(signInTextView);
        designHelper.setTextGradient(signInButton);
        designHelper.setTextGradient(registerButton);
        designHelper.setTextGradient(signInWithGoogleButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Amplify Login

                Amplify.Auth.signIn(
                        emailEditTextView.getText().toString(),
                        passwordEditTextView.getText().toString(),
                        result -> Log.i("Amplify", result.toString()),
                        AuthenticationActivity.this::onLoginError
                );
            }
        });

    }


    private void onLoginError(AuthException error) {
        errorTextView.setVisibility(View.VISIBLE);
        if(error instanceof AuthException.NotAuthorizedException) {
            errorTextView.setText("Incorrect email or password.");
        } else if(error instanceof AuthException.UserNotFoundException) {
            errorTextView.setText("User does not exist.");
        } else if(error instanceof AuthException.InvalidParameterException) {
            errorTextView.setText("Missing fields.");
        } else if(error instanceof AuthException.SessionUnavailableOfflineException) {
            errorTextView.setText("Network error.");
        } else if(error instanceof AuthException.FailedAttemptsLimitExceededException) {
            errorTextView.setText("Too many attempts.");
        } else if(error instanceof AuthException.TooManyRequestsException) {
            errorTextView.setText("Too many requests.");
        } else if(error instanceof AuthException.UserCancelledException) {
            errorTextView.setText("User has been cancelled.");
        } else {
            errorTextView.setText("Something went wrong.");
        }
    }

    private void onLoginSuccess(AuthSignInResult result) {
        AuthSignInStep signInStep = result.getNextStep().getSignInStep();

        if(signInStep == AuthSignInStep.CONFIRM_SIGN_IN_WITH_NEW_PASSWORD) {
            // TODO Start Activity to set new password
        } else if(signInStep == AuthSignInStep.RESET_PASSWORD) {
            // TODO Start Activity to reset password
        } else if(signInStep == AuthSignInStep.DONE) {
            // TODO Start main app
        } else {

        }

    }


}