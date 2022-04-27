package edu.unina.natour21.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.RegistrationFormViewModel;

public class RegistrationFormActivity extends AppCompatActivity {

    private RegistrationFormViewModel viewModel;

    private DatePickerDialog datePickerDialog;

    private TextView registrationFormTextView;
    private Button datePickerButton;
    private Spinner sexSpinner;
    private Spinner heightSpinner;
    private Spinner weightSpinner;
    private Button doneButton;

    private String email = "";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO Call Amplify SignOut in ViewModel
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Bundle Data
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
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
        registrationFormTextView = (TextView) findViewById(R.id.registrationFormResetPasswordTextView);
        datePickerButton = (Button) findViewById(R.id.registrationFormDatePickerButton);
        sexSpinner = (Spinner) findViewById(R.id.registrationFormSexSpinner);
        heightSpinner = (Spinner) findViewById(R.id.registrationFormHeightSpinner);
        weightSpinner = (Spinner) findViewById(R.id.registrationFormWeightSpinner);
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

        // Init Spinners
        initSpinners();

        // Init DatePicker
        initDatePicker();

        // Set ViewModel Observers
        observeViewModel();
    }

    private void observeViewModel() {

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
        for(int i = 100; i <= 250; i++) {
            heightSpinnerArray.add("   " + i + "cm");
        }

        ArrayAdapter<String> heightAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_dropdown, heightSpinnerArray);

        heightAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        heightSpinner.setAdapter(heightAdapter);

        // Init Weight Spinner
        List<String> weightSpinnerArray = new ArrayList<String>();
        weightSpinnerArray.add("   -");
        for(int i = 30; i <= 150; i++) {
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
                String date = dateToString(day, month + 1, year);
                datePickerButton.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerButton.setText(dateToString(day, month + 1, year));

        datePickerDialog = new DatePickerDialog(this, R.style.MaterialCalendarTheme, dateSetListener, year, month, day);

    }

    private String dateToString(int day, int month, int year) {
        return day + "/" + month + "\n" + year;
    }

}