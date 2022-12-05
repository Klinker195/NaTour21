package edu.unina.natour21.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amplifyframework.auth.AuthException;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.AmplifyExceptionHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.ResetPasswordViewModel;

public class ResetPasswordActivity extends AppCompatActivity {

    private ResetPasswordViewModel viewModel;

    private TextView forcedResetPasswordTextView;
    private EditText editTextVerificationCode;
    private TextView errorTextView;
    private EditText editTextResetPassword;
    private EditText editTextVerifyPassword;
    private Button buttonDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set ViewModel
        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setFullscreen(this);

        // Set Content View
        setContentView(R.layout.activity_reset_password);

        // Find Views
        forcedResetPasswordTextView = (TextView) findViewById(R.id.resetPasswordResetPasswordTextView);
        editTextVerificationCode = (EditText) findViewById(R.id.resetPasswordEditTextVerificationCode);
        errorTextView = (TextView) findViewById(R.id.resetPasswordErrorTextView);
        editTextResetPassword = (EditText) findViewById(R.id.resetPasswordEditTextPassword);
        editTextVerifyPassword = (EditText) findViewById(R.id.resetPasswordEditTextPassword2);
        buttonDone = (Button) findViewById(R.id.resetPasswordDoneButton);

        // Set Gradients
        designHandler.setTextGradient(forcedResetPasswordTextView);
        designHandler.setTextGradient(buttonDone);

        // Set Button Listeners
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = editTextVerificationCode.getText().toString();
                verificationCode = verificationCode.replace(" ", "");

                String password = editTextResetPassword.getText().toString();
                String passwordConfirmation = editTextVerifyPassword.getText().toString();

                if(viewModel.checkFieldsValidity(verificationCode, password, passwordConfirmation)) {
                    if(viewModel.checkPasswordMatch(password, passwordConfirmation)) {
                        viewModel.confirmResetPassword(verificationCode, password);
                    } else {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("Passwords do not match.");
                    }
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("One or more fields are empty.");
                }
            }
        });

        // Set ViewModel Observers
        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.getPasswordChangeSuccess().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                // TODO Go back to LOGIN
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
            }
        });

        viewModel.getPasswordChangeError().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                errorTextView.setVisibility(View.VISIBLE);
                AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                errorTextView.setText(exceptionHandler.getStringFromAuthException(error));
            }
        });

    }

}