package edu.unina.natour21.view.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.KeyboardHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.RegistrationFormViewModel;

public class RegistrationFormActivity extends AppCompatActivity {

    private static final String TAG = RegistrationFormActivity.class.getSimpleName();

    private FirebaseAnalytics firebaseAnalytics;

    private RegistrationFormViewModel viewModel;

    private DatePickerDialog datePickerDialog;

    private TextView registrationFormTextView;
    private EditText nicknameEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private Button datePickerButton;
    private Spinner sexSpinner;
    private Spinner heightSpinner;
    private Spinner weightSpinner;
    private Button doneButton;
    private TextView errorTextView;

    private String email = "";
    private String formattedBirthdate = "yyyy-MM-dd";
    private int selectedYear;
    private int selectedMonth;

    private Dialog loadingDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Get Bundle Data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        }

        // Set ViewModel
        viewModel = new ViewModelProvider(this).get(RegistrationFormViewModel.class);

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setFullscreen(this);

        // Set Content View
        setContentView(R.layout.activity_registration_form);

        // Find Views
        registrationFormTextView = (TextView) findViewById(R.id.registrationFormRegistrationFormTextView);
        nicknameEditText = (EditText) findViewById(R.id.registrationFormEditTextNickname);
        nameEditText = (EditText) findViewById(R.id.registrationFormEditTextName);
        surnameEditText = (EditText) findViewById(R.id.registrationFormEditTextSurname);
        datePickerButton = (Button) findViewById(R.id.registrationFormDatePickerButton);
        sexSpinner = (Spinner) findViewById(R.id.registrationFormSexSpinner);
        heightSpinner = (Spinner) findViewById(R.id.registrationFormHeightSpinner);
        weightSpinner = (Spinner) findViewById(R.id.registrationFormWeightSpinner);
        errorTextView = (TextView) findViewById(R.id.registrationFormErrorTextView);
        doneButton = (Button) findViewById(R.id.registrationFormDoneButton);

        // Set Gradients
        designHandler.setTextGradient(registrationFormTextView);
        designHandler.setTextGradient(doneButton);

        // Set Button Listeners
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardHandler.hideKeyboard(RegistrationFormActivity.this);

                errorTextView.setVisibility(View.INVISIBLE);
                errorTextView.setText("");

                String nickname = nicknameEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String surname = surnameEditText.getText().toString();

                if (nickname.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Missing fields.");
                    return;
                }

                Integer sex = Integer.valueOf(sexToInteger(sexSpinner.getSelectedItem().toString()));
                String birthdate = formattedBirthdate;
                Integer height = Integer.valueOf(heightSpinner.getSelectedItem().toString().replace(" ", "").replace("cm", "").replace("-", "0"));
                Float weight = Float.valueOf(weightSpinner.getSelectedItem().toString().replace(" ", "").replace("kg", "").replace("-", "0"));

                name = name.replace(" ", "");
                name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1).toLowerCase(Locale.ROOT);
                surname = surname.replace(" ", "");
                surname = surname.substring(0, 1).toUpperCase(Locale.ROOT) + surname.substring(1).toLowerCase(Locale.ROOT);

                nameEditText.setText(name);
                surnameEditText.setText(surname);

                if (viewModel.checkNicknameFieldValidity(nickname)) {
                    if (viewModel.checkNameAndSurnameFieldValidity(name, surname)) {
                        if (viewModel.checkBirthdateValidity(selectedYear, selectedMonth, Calendar.getInstance())) {
                            if (viewModel.checkWeightAndHeightValidity(height, weight)) {
                                errorTextView.setVisibility(View.INVISIBLE);
                                errorTextView.setText("");

                                nicknameEditText.setEnabled(false);
                                nameEditText.setEnabled(false);
                                surnameEditText.setEnabled(false);
                                sexSpinner.setEnabled(false);
                                datePickerButton.setEnabled(false);
                                heightSpinner.setEnabled(false);
                                weightSpinner.setEnabled(false);

                                showLoadingDialog();

                                viewModel.saveNewUser(nickname, name, surname, sex, birthdate, height, weight);
                            } else {
                                errorTextView.setVisibility(View.VISIBLE);
                                errorTextView.setText("Height/weight not valid.");
                            }
                        } else {
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.setText("You must be at least 14.");
                        }
                    } else {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("Name or surname not valid.");
                    }
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Nickname not valid.");
                }

            }
        });

        // Init Spinners
        initSpinners();

        // Init DatePicker
        initDatePicker();

        // Set ViewModel Observers
        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.getOnSaveUser().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean result) {
                dismissLoadingDialog();
                if (result) {
                    errorTextView.setVisibility(View.INVISIBLE);
                    errorTextView.setText("");
                    Log.i(TAG, "Next step: go to dashboard");
                    Intent switchActivityIntent = new Intent(RegistrationFormActivity.this, RouteExplorationActivity.class);
                    switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(switchActivityIntent);
                    finish();
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Something went wrong.");
                    Log.i(TAG, "Error: save error");
                    viewModel.signOut();
                    Intent switchActivityIntent = new Intent(RegistrationFormActivity.this, AuthenticationActivity.class);
                    startActivity(switchActivityIntent);
                    finish();
                }
            }
        });

    }

    private void initSpinners() {
        // Init Sex Spinner
        List<String> sexSpinnerArray = new ArrayList<String>();
        sexSpinnerArray.add("   M");
        sexSpinnerArray.add("   F");
        sexSpinnerArray.add("   Other");

        ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_dropdown, sexSpinnerArray);

        sexAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        sexSpinner.setAdapter(sexAdapter);

        // Init Height Spinner
        List<String> heightSpinnerArray = new ArrayList<String>();
        heightSpinnerArray.add("   -");
        for (int i = 100; i <= 250; i++) {
            heightSpinnerArray.add("   " + i + "cm");
        }

        ArrayAdapter<String> heightAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_dropdown, heightSpinnerArray);

        heightAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        heightSpinner.setAdapter(heightAdapter);

        // Init Weight Spinner
        List<String> weightSpinnerArray = new ArrayList<String>();
        weightSpinnerArray.add("   -");
        for (int i = 30; i <= 150; i++) {
            weightSpinnerArray.add("   " + i + "kg");
        }

        ArrayAdapter<String> weightAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_dropdown, weightSpinnerArray);

        weightAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        weightSpinner.setAdapter(weightAdapter);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.i("Date", year + "/" + (month + 1) + "/" + day);
                if (!viewModel.checkBirthdateValidity(year, month + 1, Calendar.getInstance())) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("You must be at least 14.");
                } else {
                    errorTextView.setVisibility(View.INVISIBLE);
                    errorTextView.setText("");
                }

                selectedYear = year;
                selectedMonth = month + 1;

                formattedBirthdate = dateToFormattedString(day, month + 1, year);
                String date = dateToString(day, month + 1, year);
                datePickerButton.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 14;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerButton.setText(dateToString(day, month + 1, year));
        formattedBirthdate = dateToFormattedString(day, month + 1, year);

        datePickerDialog = new DatePickerDialog(this, R.style.MaterialCalendarTheme, dateSetListener, year, month, day);

    }

    private String dateToString(int day, int month, int year) {
        return day + "/" + month + "\n" + year;
    }

    private String dateToFormattedString(int day, int month, int year) {
        return year + "-" + month + "-" + day;
    }

    private Integer sexToInteger(String sex) {
        sex = sex.replace(" ", "");
        Integer sexInt;
        switch (sex) {
            case "M":
                sexInt = 0;
                break;
            case "F":
                sexInt = 1;
                break;
            default:
                sexInt = 2;
                break;
        }

        return sexInt;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        viewModel.signOut();
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