package edu.unina.natour21.view.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.auth.AuthException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.slider.Slider;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.activation.MimeType;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.PostCreationViewModel;
import io.jenetics.jpx.GPX;

public class PostCreationActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();

    private PostCreationViewModel viewModel;

    private LinearLayout difficultyLinearLayout;
    private Slider durationSlider;
    private TextView durationValueTextView;
    private Button doneButton;
    private TextView otherSettingsBoxTextView;
    private TextView routePathTextViewButton;
    private TextView routeGPXTextViewButton;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private CheckBox accessibilityCheckBox;
    private TextView picturesTapTextView;
    private ImageView picturesPreviewImageView;
    private ImageView picturesPreviewBackgroundImageView;
    private Button picturesClearButton;
    private ImageView backButtonImageView;

    private ArrayList<LatLng> pointArrayList;
    private Integer selectedDifficultyDotIndex = 4;
    private Integer routeDurationIndex = 30;
    private GPX tmpGPX;
    private Double startLat = 0d;
    private Double startLng = 0d;

    private Dialog loadingDialog;

    ActivityResultLauncher<Intent> PostCreationMapActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        pointArrayList = data.getParcelableArrayListExtra("pointArrayList");
                        //viewModel.writeGPXFile(pointArrayList, getFilesDir().getAbsolutePath());
                        if(pointArrayList != null && !pointArrayList.isEmpty()) {
                            startLat = pointArrayList.get(0).latitude;
                            startLng = pointArrayList.get(0).longitude;
                            tmpGPX = viewModel.generateGPXFromPointArrayList(pointArrayList);
                            routePathTextViewButton.setTextSize(16f);
                            routePathTextViewButton.setTextColor(getColor(R.color.edit_text_color));
                            routePathTextViewButton.setText("Route selected, tap to change");
                            routeGPXTextViewButton.setTextSize(18f);
                            routeGPXTextViewButton.setTextColor(getColor(R.color.lightgrey_text));
                            routeGPXTextViewButton.setText("Import GPX file");
                        }
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> GPXChooserActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        try {
                            tmpGPX = viewModel.readGPXFile(getContentResolver().openInputStream(uri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            tmpGPX = null;
                            Toast.makeText(PostCreationActivity.this, "File not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                        routeGPXTextViewButton.setTextSize(16f);
                        routeGPXTextViewButton.setTextColor(getColor(R.color.edit_text_color));
                        routeGPXTextViewButton.setText("Route selected, tap to change");
                        routePathTextViewButton.setTextSize(18f);
                        routePathTextViewButton.setTextColor(getColor(R.color.lightgrey_text));
                        routePathTextViewButton.setText("Tap to trace route");
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> PictureChooserActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        picturesPreviewImageView.setImageURI(null);
                        picturesTapTextView.setVisibility(View.GONE);
                        picturesPreviewImageView.setImageURI(uri);
                        picturesPreviewImageView.setVisibility(View.VISIBLE);
                        picturesPreviewBackgroundImageView.setVisibility(View.VISIBLE);
                        picturesClearButton.setVisibility(View.VISIBLE);
                        // TODO: Do something with image
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set ViewModel
        viewModel = new ViewModelProvider(this).get(PostCreationViewModel.class);

        // Set Content View
        setContentView(R.layout.activity_post_creation);

        // Find view by ID
        backButtonImageView = (ImageView) findViewById(R.id.postCreationBackButtonImageView);
        difficultyLinearLayout = (LinearLayout) findViewById(R.id.postCreationDifficultyLinearLayout);
        durationSlider = (Slider) findViewById(R.id.postCreationDurationSlider);
        durationValueTextView = (TextView) findViewById(R.id.postCreationDurationValueTextView);
        doneButton = (Button) findViewById(R.id.postCreationDoneButton);
        otherSettingsBoxTextView = (TextView) findViewById(R.id.postCreationOtherSettingsBoxTextView);
        routePathTextViewButton = (TextView) findViewById(R.id.postCreationRoutePathTextView);
        routeGPXTextViewButton = (TextView) findViewById(R.id.postCreationImportGPXTapTextView);
        nameEditText = (EditText) findViewById(R.id.postCreationRouteNameEditText);
        descriptionEditText = (EditText) findViewById(R.id.postCreationDescriptionEditText);
        accessibilityCheckBox = (CheckBox) findViewById(R.id.postCreationOtherSettingsAccessibilityCheckBox);
        picturesTapTextView = (TextView) findViewById(R.id.postCreationPicturesTapTextView);
        picturesPreviewImageView = (ImageView) findViewById(R.id.postCreationPicturesPreviewImageView);
        picturesPreviewBackgroundImageView = (ImageView) findViewById(R.id.postCreationPicturesPreviewBackgroundImageView);
        picturesClearButton = (Button) findViewById(R.id.postCreationPicturesClearButton);

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setTextGradient(doneButton);
        designHandler.setTextGradient(otherSettingsBoxTextView);
        designHandler.setTextGradient(picturesClearButton);
        designHandler.setFullscreen(this);

        // Observe ViewModel
        viewModel.getOnUserQueryFailure().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                dismissLoadingDialog();
                Toast.makeText(PostCreationActivity.this, "Couldn't find user",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOnPostQuerySuccess().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                dismissLoadingDialog();
                Toast.makeText(PostCreationActivity.this, "Post created",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getOnPostQueryFailure().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                dismissLoadingDialog();
                Toast.makeText(PostCreationActivity.this, "Couldn't create post",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOnFetchUserAttributesFailure().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException authException) {
                Toast.makeText(PostCreationActivity.this, "Couldn't fetch user attributes",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOnIncorrectFile().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                Toast.makeText(PostCreationActivity.this, "File not compatible",
                        Toast.LENGTH_SHORT).show();
            }
        });

        backButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        routePathTextViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(PostCreationActivity.this, PostCreationMapsActivity.class);
                PostCreationMapActivityResultLauncher.launch(switchActivityIntent);
                // startActivity(switchActivityIntent);
            }
        });

        routeGPXTextViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(Intent.ACTION_GET_CONTENT);
                switchActivityIntent.setType("application/octet-stream");
                switchActivityIntent.addCategory(Intent.CATEGORY_OPENABLE);
                GPXChooserActivityResultLauncher.launch(switchActivityIntent);
            }
        });

        picturesTapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(Intent.ACTION_GET_CONTENT);
                // switchActivityIntent.setTypeAndNormalize("text/xml");
                switchActivityIntent.setType("image/jpeg");
                switchActivityIntent.addCategory(Intent.CATEGORY_OPENABLE);
                PictureChooserActivityResultLauncher.launch(switchActivityIntent);
            }
        });

        picturesClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picturesPreviewImageView.setImageURI(null);
                picturesPreviewImageView.setVisibility(View.GONE);
                picturesPreviewBackgroundImageView.setVisibility(View.GONE);
                picturesClearButton.setVisibility(View.GONE);
                picturesTapTextView.setVisibility(View.VISIBLE);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String routeName = nameEditText.getText().toString();
                String routeDescription = descriptionEditText.getText().toString();
                GPX routeGPX = tmpGPX;
                Bitmap routeBitmap = null;
                if(picturesPreviewImageView.getDrawable() != null) {
                    routeBitmap = ((BitmapDrawable) picturesPreviewImageView.getDrawable()).getBitmap();
                }
                Integer routeDifficulty = selectedDifficultyDotIndex + 1;
                Integer routeDuration = routeDurationIndex;
                Boolean routeAccessibility = accessibilityCheckBox.isChecked();

                if(routeName != null && !routeName.isEmpty()) {
                    if(routeDescription == null) routeDescription = "";
                    if(routeGPX != null) {
                        try {
                            viewModel.createPost(
                                    routeName,
                                    routeDescription,
                                    routeGPX,
                                    startLat,
                                    startLng,
                                    getFilesDir().getAbsolutePath(),
                                    routeBitmap,
                                    routeDifficulty,
                                    routeDuration,
                                    routeAccessibility);
                            showLoadingDialog();
                        } catch(NullPointerException e) {
                            dismissLoadingDialog();
                            e.printStackTrace();
                            Toast.makeText(PostCreationActivity.this, "Critical error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PostCreationActivity.this, "Path is missing",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PostCreationActivity.this, "Route name is missing",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        durationSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                String stringValue = String.valueOf((int) value);
                durationValueTextView.setText(stringValue + '\'');
                routeDurationIndex = (int) value;
            }
        });

        for(int i = 0; i < difficultyLinearLayout.getChildCount(); i++) {
            int finalI = i;
            difficultyLinearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int n = finalI;
                    redrawDifficultyDots(n);
                    selectedDifficultyDotIndex = n;
                }
            });
        }
    }

    private void redrawDifficultyDots(int index) {
        Drawable enabledDifficultyDot = getResources().getDrawable(R.drawable.natour_difficulty_filter_circle);
        Drawable disabledDifficultyDot = getResources().getDrawable(R.drawable.natour_difficulty_filter_circle_disabled);

        for(int i = 0; i < difficultyLinearLayout.getChildCount(); i++) {
            ImageView difficultyDot = (ImageView) difficultyLinearLayout.getChildAt(i);
            if(i <= index) {
                difficultyDot.setImageDrawable(enabledDifficultyDot);
            } else {
                difficultyDot.setImageDrawable(disabledDifficultyDot);
            }
        }
    }

    private void showLoadingDialog() {
        if(loadingDialog == null) {
            loadingDialog = new Dialog(this);
        }
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.popup_loading);

        ProgressBar loadingDialogProgressBar = (ProgressBar) findViewById(R.id.popupLoadingProgressBar);

        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if(loadingDialog != null) {
            if(loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

}