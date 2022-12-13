package edu.unina.natour21.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.AmplifyExceptionHandler;
import edu.unina.natour21.utility.KeyboardHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private FirebaseAnalytics firebaseAnalytics;

    private SignUpViewModel viewModel;

    private Button nextButton;
    private TextView errorTextView;
    private TextView registrationFormTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText verifyPasswordEditText;

    private NatourUIDesignHandler designHandler;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;

    @NotNull
    private final long currentMillis = 300000; // Default value: 300000
    private long endMillis;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Set View Model
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // Set Standard UI
        designHandler = new NatourUIDesignHandler();
        designHandler.setFullscreen(this);

        // Set Content View
        setContentView(R.layout.activity_sign_up);

        // Find Views
        registrationFormTextView = (TextView) findViewById(R.id.signUpRegistrationFormTextView);
        errorTextView = (TextView) findViewById(R.id.signUpErrorTextView);
        emailEditText = (EditText) findViewById(R.id.signUpEditTextEmailAddress);
        passwordEditText = (EditText) findViewById(R.id.signUpEditTextPassword);
        verifyPasswordEditText = (EditText) findViewById(R.id.signUpEditTextPassword2);
        nextButton = (Button) findViewById(R.id.signUpNextButton);

        // Set Gradients
        designHandler.setTextGradient(registrationFormTextView);
        designHandler.setTextGradient(nextButton);

        // Set Shared Preferences
        sharedPrefs = getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardHandler.hideKeyboard(SignUpActivity.this);

                String email = emailEditText.getText().toString();
                email = email.replace(" ", "").toLowerCase();
                emailEditText.setText(email);
                String password = passwordEditText.getText().toString();
                String verifyPassword = verifyPasswordEditText.getText().toString();

                if (viewModel.checkEmailValidity(email)) {
                    if (viewModel.checkPasswordAndPasswordConfirmationValidity(password, verifyPassword)) {
                        if (viewModel.checkPasswordAndPasswordConfirmationMatch(password, verifyPassword)) {
                            errorTextView.setVisibility(View.INVISIBLE);
                            emailEditText.setEnabled(false);
                            passwordEditText.setEnabled(false);
                            verifyPasswordEditText.setEnabled(false);
                            showLoadingDialog();
                            viewModel.signUp(email, password);
                        } else {
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.setText("Passwords do not match.");
                        }
                    } else {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("Password is invalid.");
                    }
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Email is invalid.");
                }

            }
        });

        // Set ViewModel Observers
        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.getOnSignUpFailure().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                dismissLoadingDialog();
                errorTextView.setVisibility(View.VISIBLE);
                AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                errorTextView.setText(exceptionHandler.getStringFromAuthException(error));
            }
        });

        viewModel.getOnSignUpSuccess().observe(this, new Observer<AuthSignUpResult>() {
            @Override
            public void onChanged(AuthSignUpResult authSignUpResult) {
                Log.i("Amplify", "Confirmation success");
                timerInit();

                dismissLoadingDialog();

                Bundle firebaseBundle = new Bundle();
                firebaseBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Sign-up done button");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, firebaseBundle);

                Intent switchActivityIntent = new Intent(SignUpActivity.this, VerificationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("email", emailEditText.getText().toString().replace(" ", "").toLowerCase());
                switchActivityIntent.putExtras(bundle);
                startActivity(switchActivityIntent);
                finish();
            }
        });
    }


    private void timerInit() {
        sharedPrefsEditor.putLong("endMillis", System.currentTimeMillis() + currentMillis);
        sharedPrefsEditor.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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