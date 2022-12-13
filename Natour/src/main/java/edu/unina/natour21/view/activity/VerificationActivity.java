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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import edu.unina.natour21.R;
import edu.unina.natour21.dto.UserDTO;
import edu.unina.natour21.retrofit.AmazonAPI;
import edu.unina.natour21.retrofit.IUserAPI;
import edu.unina.natour21.utility.AmplifyExceptionHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.VerificationViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = VerificationActivity.class.getSimpleName();

    private FirebaseAnalytics firebaseAnalytics;

    private VerificationViewModel viewModel;

    private EditText confirmationCodeEditText;
    private TextView verificationFormTextView;
    private TextView errorConfirmationCodeTextView;
    private Button resendCodeButton;
    private Button nextButton;

    private NatourUIDesignHandler designHandler;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;

    private IUserAPI userAPI;

    private Dialog loadingDialog;

    @NotNull
    private long currentMillis = 300000; // 300000
    private long endMillis;

    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Get Bundle Data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        }

        // Retrofit API declaration
        userAPI = AmazonAPI.getClient().create(IUserAPI.class);

        // Set View Model
        viewModel = new ViewModelProvider(this).get(VerificationViewModel.class);

        // Set Standard UI
        designHandler = new NatourUIDesignHandler();
        designHandler.setFullscreen(this);

        // Set Content View
        setContentView(R.layout.activity_verification);

        // Find Views
        verificationFormTextView = (TextView) findViewById(R.id.verificationFormTextView);
        confirmationCodeEditText = (EditText) findViewById(R.id.verificationEditTextVerificationCode);
        errorConfirmationCodeTextView = (TextView) findViewById(R.id.verificationErrorTextView2);
        resendCodeButton = (Button) findViewById(R.id.verificationResendCodeButton);
        nextButton = (Button) findViewById(R.id.verificationNextButton);

        // Set Gradients
        designHandler.setTextGradient(verificationFormTextView);
        designHandler.setTextGradient(resendCodeButton);
        designHandler.setTextGradient(nextButton);

        // Set Shared Preferences
        sharedPrefs = getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();

        resendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endMillis = System.currentTimeMillis() + currentMillis;
                viewModel.resendCode(email);
                sendCodeButtonInit();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = confirmationCodeEditText.getText().toString();
                verificationCode = verificationCode.replace(" ", "");
                if (!verificationCode.isEmpty()) {
                    errorConfirmationCodeTextView.setVisibility(View.INVISIBLE);
                    showLoadingDialog();
                    viewModel.signUpConfirmation(email, verificationCode);
                } else {
                    errorConfirmationCodeTextView.setVisibility(View.VISIBLE);
                    errorConfirmationCodeTextView.setText("Code is invalid.");
                }
            }
        });

        // Set ViewModel Observers
        observeViewModel();

        // Init resendCodeButton Timer
        endMillis = sharedPrefs.getLong("endMillis", System.currentTimeMillis());
        if (System.currentTimeMillis() >= endMillis) {
            viewModel.resendCode(email);
            endMillis = System.currentTimeMillis() + currentMillis;
        }
        sendCodeButtonInit();

    }

    private void observeViewModel() {

        viewModel.getOnTimerUpdate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String adjustedTime) {
                resendCodeButton.setText(adjustedTime);
                designHandler.setTextGradient(resendCodeButton);
            }
        });

        viewModel.getOnTimerFinish().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                resendCodeButton.setEnabled(true);
                resendCodeButton.setText("resend code");
                designHandler.setTextGradient(resendCodeButton);
                currentMillis = 300000;
            }
        });

        viewModel.getOnSignUpConfirmationSuccess().observe(this, new Observer<AuthSignUpResult>() {
            @Override
            public void onChanged(AuthSignUpResult authSignUpResult) {
                dismissLoadingDialog();
                Intent switchActivityIntent = new Intent(VerificationActivity.this, AuthenticationActivity.class);
                startActivity(switchActivityIntent);
                finish();
                Toast.makeText(VerificationActivity.this, "Verification success, please login again using credentials",
                        Toast.LENGTH_SHORT).show();
                /*
                if(email != null) {
                    Call<UserDTO> call = userAPI.getUserByEmail(email.replace(" ", "").toLowerCase());
                    call.enqueue(new Callback<UserDTO>() {
                        @Override
                        public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                            UserDTO userDTO = response.body();

                            dismissLoadingDialog();

                            if(userDTO == null) {
                                Intent switchActivityIntent = new Intent(VerificationActivity.this, RegistrationFormActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("email", email.replace(" ", "").toLowerCase());
                                switchActivityIntent.putExtras(bundle);
                                startActivity(switchActivityIntent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDTO> call, Throwable t) {
                            Log.e("AmazonAPI", "Request failed");
                            Intent switchActivityIntent = new Intent(VerificationActivity.this, AuthenticationActivity.class);
                            startActivity(switchActivityIntent);
                            viewModel.signOut();
                            finish();
                        }
                    });
                }
                */
            }
        });

        viewModel.getOnSignUpConfirmationFailure().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                dismissLoadingDialog();
                errorConfirmationCodeTextView.setVisibility(View.VISIBLE);
                AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                errorConfirmationCodeTextView.setText(exceptionHandler.getStringFromAuthException(error));
            }
        });

        viewModel.getOnResendCodeSuccess().observe(this, new Observer<AuthSignUpResult>() {
            @Override
            public void onChanged(AuthSignUpResult authSignUpResult) {
                sendCodeButtonInit();
            }
        });

        viewModel.getOnResendCodeFailure().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException error) {
                errorConfirmationCodeTextView.setVisibility(View.VISIBLE);
                AmplifyExceptionHandler exceptionHandler = new AmplifyExceptionHandler();
                errorConfirmationCodeTextView.setText(exceptionHandler.getStringFromAuthException(error));
            }
        });

    }

    private void sendCodeButtonInit() {
        currentMillis = endMillis - System.currentTimeMillis();
        resendCodeButton.setEnabled(false);
        sharedPrefsEditor.putLong("endMillis", System.currentTimeMillis() + currentMillis);
        sharedPrefsEditor.apply();
        viewModel.startCodeTimer(currentMillis);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        viewModel.signOut();
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