package edu.unina.natour21.view.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.auth.AuthException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.unina.natour21.R;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.utility.NatourFileHandler;
import edu.unina.natour21.viewmodel.PostDetailsViewModel;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.WayPoint;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapsInitializer.Renderer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.material.slider.Slider;

public class PostDetailsActivity extends AppCompatActivity implements OnMapsSdkInitializedCallback, OnMapReadyCallback {

    public final String TAG = this.getClass().getSimpleName();

    private PostDetailsViewModel viewModel;

    private ImageView reportButtonImageView;
    private ImageView backButtonImageView;

    private ScrollView scrollView;
    private ImageView picImageView;
    private TextView titleTextView;
    private Button rateButton;
    private FloatingActionButton reportBackgroundButton;
    private ImageView reportImageView;
    private FloatingActionButton accessibilityBackgroundButton;
    private ImageView accessibilityImageView;
    private ImageView addToFavImageView;

    private ImageView userPropicImageView;
    private TextView userNicknameTextView;
    private TextView userNameSurnameTextView;
    private ImageView userFollowImageView;
    private ImageView editButtonImageView;

    private CardView routeCardView;

    private MapView routeMapView;
    private GoogleMap googleMap;

    private ConstraintLayout infoConstraintLayout;

    private TextView descriptionHeaderTextView;
    private TextView descriptionTextView;
    private LinearLayout difficultyLinearLayout;
    private TextView durationTextView;
    private TextView otherInfoHeaderTextView;
    private TextView accessibilityTextView;
    private ImageView accessibilityMarkerImageView;

    private ImageView popupPostReportBackButtonImageView;
    private EditText popupPostReportTitleEditText;
    private EditText popupPostReportDescriptionEditText;
    private ImageView popupPostReportImageViewDoneButton;

    private EditText popupPostUpdateTitleEditText;
    private EditText popupPostUpdateDescriptionEditText;
    private Slider popupPostUpdateDurationSlider;
    private TextView popupPostUpdateDurationSliderValueTextView;
    private LinearLayout popupPostUpdateDifficultyLinearLayout;
    private CheckBox popupPostUpdateAccessibilityCheckBox;
    private Button popupPostUpdateRouteTraceButton;
    private Button popupPostUpdateImportGPXButton;
    private Button popupPostUpdatePictureButton;
    private ImageView popupPostUpdateImageViewCancelButton;
    private ImageView popupPostUpdateImageViewDoneButton;
    private ImageView popupPostUpdateImageViewDeleteButton;
    private ImageView popupPostUpdateBackButtonImageView;

    private TextView creationDateTimestampTextView;
    private TextView editDateTextView;
    private TextView editDateTimestampTextView;

    private ImageView popupConfirmImageViewYesButton;
    private ImageView popupConfirmImageViewNoButton;

    private Dialog loadingDialog;

    private ArrayList<LatLng> pointArrayList;

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
                            viewModel.getEditedPost().setStartLat(pointArrayList.get(0).latitude);
                            viewModel.getEditedPost().setStartLng(pointArrayList.get(0).longitude);
                            viewModel.getEditedPost().setRoute(viewModel.generateGPXFromPointArrayList(pointArrayList));
                            popupPostUpdateRouteTraceButton.setTextColor(getColor(R.color.edit_text_color));
                            popupPostUpdateRouteTraceButton.setText("Route selected, tap to change");
                            popupPostUpdateImportGPXButton.setTextColor(getColor(R.color.lightgrey_text));
                            popupPostUpdateImportGPXButton.setText("Import GPX file");
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
                            viewModel.getEditedPost().setRoute(viewModel.readGPXFile(getContentResolver().openInputStream(uri)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(PostDetailsActivity.this, "File not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                        popupPostUpdateImportGPXButton.setTextColor(getColor(R.color.edit_text_color));
                        popupPostUpdateImportGPXButton.setText("Route selected, tap to change");
                        popupPostUpdateRouteTraceButton.setTextColor(getColor(R.color.lightgrey_text));
                        popupPostUpdateRouteTraceButton.setText("Tap to trace route");
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

                        Bitmap bitmap = null;
                        ContentResolver contentResolver = getContentResolver();
                        try {
                            if(Build.VERSION.SDK_INT < 28) {
                                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                            } else {
                                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                                bitmap = ImageDecoder.decodeBitmap(source);
                            }
                        } catch (Exception e) {
                            Toast.makeText(PostDetailsActivity.this, "Error reading image",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        if(bitmap != null) {
                            LinkedList<Bitmap> bitmapLinkedList = new LinkedList<Bitmap>();
                            bitmapLinkedList.add(bitmap);
                            viewModel.getEditedPost().setPics(bitmapLinkedList);
                            Log.i(TAG, "Successfully loaded image");
                        } else {
                            Toast.makeText(PostDetailsActivity.this, "Couldn't find image",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // Set ViewModel
        viewModel = new ViewModelProvider(this).get(PostDetailsViewModel.class);

        Intent intent = getIntent();
        Post tmpPost = intent.getParcelableExtra("postDetails");

        Log.i(TAG, tmpPost.getTimestamp().toString());

        viewModel.setCurrentPost(tmpPost);

        infoConstraintLayout = (ConstraintLayout) findViewById(R.id.postDetailsInfoConstraintLayout);

        if(viewModel.getCurrentPost() == null) Log.i(TAG, "Post is null");
        if(viewModel.getCurrentPost() != null) {
            reportButtonImageView = (ImageView) findViewById(R.id.postDetailsReportButtonImageView);
            backButtonImageView = (ImageView) findViewById(R.id.postDetailsBackButtonImageView);

            picImageView = (ImageView) findViewById(R.id.postDetailsPicImageView);
            titleTextView = (TextView) findViewById(R.id.postDetailsTitleTextView);
            rateButton = (Button) findViewById(R.id.postDetailsRateButton);
            reportBackgroundButton = (FloatingActionButton) findViewById(R.id.postDetailsReportBackgroundButton);
            reportImageView = (ImageView) findViewById(R.id.postDetailsReportImageView);
            accessibilityBackgroundButton = (FloatingActionButton) findViewById(R.id.postDetailsAccessibilityBackgroundButton);
            accessibilityImageView = (ImageView) findViewById(R.id.postDetailsAccessibilityImageView);
            addToFavImageView = (ImageView) findViewById(R.id.postDetailsAddToFavImageView);

            userPropicImageView = (ImageView) findViewById(R.id.postDetailsUserPropicImageView);
            userNicknameTextView = (TextView) findViewById(R.id.postDetailsUserNicknameTextView);
            userNameSurnameTextView = (TextView) findViewById(R.id.postDetailsUserNameSurnameTextView);
            userFollowImageView = (ImageView) findViewById(R.id.postDetailsUserFollowImageView);
            editButtonImageView = (ImageView) findViewById(R.id.postDetailsEditButtonImageView);

            scrollView = (ScrollView) findViewById(R.id.postDetailsScrollView);
            routeCardView = (CardView) findViewById(R.id.postDetailsRouteCardView);
            routeMapView = (MapView) findViewById(R.id.postDetailsRouteMapView);
            descriptionHeaderTextView = (TextView) findViewById(R.id.postDetailsDescriptionHeaderTextView);
            descriptionTextView = (TextView) findViewById(R.id.postDetailsDescriptionTextView);
            difficultyLinearLayout = (LinearLayout) findViewById(R.id.postDetailsDifficultyLinearLayout);
            durationTextView = (TextView) findViewById(R.id.postDetailsDurationTextView);
            otherInfoHeaderTextView = (TextView) findViewById(R.id.postDetailsOtherInfoHeaderTextView);
            accessibilityTextView = (TextView) findViewById(R.id.postDetailsAccessibilityTextView);
            accessibilityMarkerImageView = (ImageView) findViewById(R.id.postDetailsAccessibilityMarkerImageView);

            creationDateTimestampTextView = (TextView) findViewById(R.id.postDetailsCreationDateTimestampTextView);
            editDateTextView = (TextView) findViewById(R.id.postDetailsEditDateTextView);
            editDateTimestampTextView = (TextView) findViewById(R.id.postDetailsEditDateTimestampTextView);

            backButtonImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            reportButtonImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: Open popup
                    showReportDialog();
                }
            });

            creationDateTimestampTextView.setText(viewModel.getCurrentPost().getTimestamp().toString() + " GMT+1");
            if(viewModel.getCurrentPost().getEditTimestamp() != null) {
                editDateTextView.setVisibility(View.VISIBLE);
                editDateTimestampTextView.setVisibility(View.VISIBLE);
                editDateTimestampTextView.setText(viewModel.getCurrentPost().getEditTimestamp().toString() + " GMT+1");
            } else {
                editDateTextView.setVisibility(View.GONE);
                editDateTimestampTextView.setVisibility(View.GONE);
            }

            NatourFileHandler fileHandler = new NatourFileHandler();

            String[] picsArray = intent.getStringArrayExtra("postPics");

            if(picsArray != null) {
                Bitmap bitmap = fileHandler.openBitmapFromFilename(getBaseContext(), picsArray[0]);
                if(bitmap != null) {
                    picImageView.setImageBitmap(bitmap);
                    LinkedList<Bitmap> bitmapLinkedList = new LinkedList<Bitmap>();
                    bitmapLinkedList.add(bitmap);
                    viewModel.getCurrentPost().setPics(bitmapLinkedList);
                    fileHandler.deleteFileFromFilename(getBaseContext(), picsArray[0]);
                }
            }

            titleTextView.setText(viewModel.getCurrentPost().getTitle());
            rateButton.setVisibility(View.GONE);
            if(!viewModel.getCurrentPost().getReported()) {
                reportBackgroundButton.setVisibility(View.GONE);
                reportImageView.setVisibility(View.GONE);
            }
            if(!viewModel.getCurrentPost().getAccessibility()) {
                accessibilityBackgroundButton.setVisibility(View.GONE);
                accessibilityImageView.setVisibility(View.GONE);
            }
            // TODO: Verify addToFav ImageView Button

            if(viewModel.getCurrentPost().getAuthor() != null) {
                userPropicImageView.setImageBitmap(viewModel.getCurrentPost().getAuthor().getPropic());
                userNicknameTextView.setText(viewModel.getCurrentPost().getAuthor().getNickname());
                userNameSurnameTextView.setText(viewModel.getCurrentPost().getAuthor().getName() + " " + viewModel.getCurrentPost().getAuthor().getSurname());
                editButtonImageView.setVisibility(View.GONE);

                editButtonImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUpdateDialog();
                    }
                });

                String propic = intent.getStringExtra("userPropic");

                if(propic != null) {
                    Bitmap bitmap = fileHandler.openBitmapFromFilename(getBaseContext(), propic);
                    if(bitmap != null) {
                        userPropicImageView.setImageBitmap(bitmap);
                        viewModel.getCurrentPost().getAuthor().setPropic(bitmap);
                        fileHandler.deleteFileFromFilename(getBaseContext(), propic);
                    }
                }

                // TODO: Verify userFollow ImageView Button
                // showLoadingDialog();
                viewModel.checkUserPermissions();
            }

            // TODO: Add MapView link
            MapsInitializer.initialize(getApplicationContext(), Renderer.LATEST, this);

            routeMapView.onCreate(savedInstanceState);
            routeMapView.getMapAsync(this);

            descriptionHeaderTextView.setVisibility(View.GONE);
            descriptionTextView.setVisibility(View.GONE);

            if(viewModel.getCurrentPost().getDescription() != null && !viewModel.getCurrentPost().getDescription().isEmpty()) {
                descriptionHeaderTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(viewModel.getCurrentPost().getDescription());
            }

            for(int i = 0; i < difficultyLinearLayout.getChildCount(); i++) {
                if(i > viewModel.getCurrentPost().getDifficulty() - 1) {
                    difficultyLinearLayout.getChildAt(i).setVisibility(View.GONE);
                }
            }
            durationTextView.setText(viewModel.getCurrentPost().getDuration() + " minutes");
            if(!viewModel.getCurrentPost().getAccessibility()) {
                otherInfoHeaderTextView.setVisibility(View.GONE);
                accessibilityTextView.setVisibility(View.GONE);
                accessibilityMarkerImageView.setVisibility(View.GONE);
            }
        } else {
            infoConstraintLayout.setVisibility(View.GONE);
        }

        viewModel.setEditedPost(viewModel.getCurrentPost());

        observeViewModel();
    }

    @Override
    public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
        switch (renderer) {
            case LATEST:
                Log.d(TAG, "The latest version of the renderer is used.");
                break;
            case LEGACY:
                Log.d(TAG, "The legacy version of the renderer is used.");
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.i("[MAPS]", "onMapReady");

        googleMap.getUiSettings().setScrollGesturesEnabled(false);

        Route route = viewModel.getCurrentPost().getRoute().getRoutes().get(0);
        ArrayList<WayPoint> wayPointArrayList = new ArrayList<WayPoint>(route.getPoints());

        PolylineOptions polylineOptions = new PolylineOptions();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(int i = 0; i < wayPointArrayList.size(); i++) {
            int hue = 147;
            LatLng point = new LatLng(wayPointArrayList.get(i).getLatitude().doubleValue(), wayPointArrayList.get(i).getLongitude().doubleValue());
            Log.i(TAG, point.latitude + " " + point.longitude);
            if(i == 0) hue = 188;
            Marker tmpMarker = googleMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(hue)));
            if(i == 0 && tmpMarker != null) {
                tmpMarker.setTitle("Start");
                tmpMarker.showInfoWindow();
            }
            polylineOptions.add(point);
            builder.include(point);
        }

        int padding = 150;

        LatLngBounds bounds = builder.build();

        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.animateCamera(cameraUpdate);
            }
        });

        googleMap.addPolyline(polylineOptions).setColor(getResources().getColor(R.color.bluegreen_nav_icon));
    }


    private void showUpdateDialog() {
        final Dialog updateDialog = new Dialog(this);
        updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateDialog.setCancelable(true);
        updateDialog.setContentView(R.layout.popup_post_update);

        updateDialog.show();

        viewModel.setEditedPost(viewModel.getCurrentPost());

        // Find Views
        popupPostUpdateTitleEditText = (EditText) updateDialog.findViewById(R.id.popupPostUpdateTitleEditText);
        popupPostUpdateDescriptionEditText = (EditText) updateDialog.findViewById(R.id.popupPostUpdateDescriptionEditText);
        popupPostUpdateDurationSlider = (Slider) updateDialog.findViewById(R.id.popupPostUpdateDurationSlider);
        popupPostUpdateDurationSliderValueTextView = (TextView) updateDialog.findViewById(R.id.popupPostUpdateDurationSliderValueTextView);
        popupPostUpdateDifficultyLinearLayout = (LinearLayout) updateDialog.findViewById(R.id.popupPostUpdateDifficultyLinearLayout);
        popupPostUpdateAccessibilityCheckBox = (CheckBox) updateDialog.findViewById(R.id.popupPostUpdateAccessibilityCheckBox);
        popupPostUpdateRouteTraceButton = (Button) updateDialog.findViewById(R.id.popupPostUpdateRouteTraceButton);
        popupPostUpdateImportGPXButton = (Button) updateDialog.findViewById(R.id.popupPostUpdateImportGPXButton);
        popupPostUpdatePictureButton = (Button) updateDialog.findViewById(R.id.popupPostUpdatePictureButton);
        popupPostUpdateBackButtonImageView = (ImageView) updateDialog.findViewById(R.id.popupPostUpdateBackButtonImageView);
        popupPostUpdateImageViewDoneButton = (ImageView) updateDialog.findViewById(R.id.popupPostUpdateImageViewDoneButton);
        popupPostUpdateImageViewCancelButton = (ImageView) updateDialog.findViewById(R.id.popupPostUpdateImageViewCancelButton);
        popupPostUpdateImageViewDeleteButton = (ImageView) updateDialog.findViewById(R.id.popupPostUpdateImageViewDeleteButton);

        // Set Data
        popupPostUpdateTitleEditText.setText(viewModel.getCurrentPost().getTitle());
        popupPostUpdateDescriptionEditText.setText(viewModel.getCurrentPost().getDescription());
        popupPostUpdateDurationSlider.setValue(viewModel.getCurrentPost().getDuration());
        popupPostUpdateDurationSliderValueTextView.setText(viewModel.getCurrentPost().getDuration() + "'");
        redrawPopupPostUpdateDifficultyDots(viewModel.getCurrentPost().getDifficulty() - 1);
        popupPostUpdateAccessibilityCheckBox.setChecked(viewModel.getCurrentPost().getAccessibility());

        popupPostUpdateRouteTraceButton.setTextColor(getColor(R.color.edit_text_color));
        popupPostUpdateRouteTraceButton.setText("Route selected, tap to change");

        popupPostUpdatePictureButton.setTextColor(getColor(R.color.edit_text_color));
        popupPostUpdatePictureButton.setText("Picture selected, tap to change");

        // Set Listeners
        popupPostUpdateBackButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog.dismiss();
            }
        });

        popupPostUpdateRouteTraceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(PostDetailsActivity.this, PostCreationMapsActivity.class);
                switchActivityIntent.putParcelableArrayListExtra("pointArrayList", viewModel.getPointArrayListFromGPX(viewModel.getEditedPost().getRoute()));
                PostCreationMapActivityResultLauncher.launch(switchActivityIntent);
            }
        });

        popupPostUpdatePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(Intent.ACTION_GET_CONTENT);
                // switchActivityIntent.setTypeAndNormalize("text/xml");
                switchActivityIntent.setType("image/jpeg");
                switchActivityIntent.addCategory(Intent.CATEGORY_OPENABLE);
                PictureChooserActivityResultLauncher.launch(switchActivityIntent);
            }
        });

        popupPostUpdateImportGPXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(Intent.ACTION_GET_CONTENT);
                switchActivityIntent.setType("application/octet-stream");
                switchActivityIntent.addCategory(Intent.CATEGORY_OPENABLE);
                GPXChooserActivityResultLauncher.launch(switchActivityIntent);
            }
        });

        popupPostUpdateImageViewCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setEditedPost(viewModel.getCurrentPost());
                updateDialog.dismiss();
            }
        });

        popupPostUpdateImageViewDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getEditedPost().setTitle(popupPostUpdateTitleEditText.getText().toString());
                viewModel.getEditedPost().setDescription(popupPostUpdateDescriptionEditText.getText().toString());
                viewModel.getEditedPost().setDuration(((Float)popupPostUpdateDurationSlider.getValue()).intValue());
                viewModel.getEditedPost().setAccessibility(popupPostUpdateAccessibilityCheckBox.isChecked());

                showLoadingDialog();
                viewModel.updatePost(getFilesDir().getAbsolutePath());
                updateDialog.dismiss();
            }
        });

        popupPostUpdateImageViewDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        popupPostUpdateDurationSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                String stringValue = String.valueOf((int) value);
                popupPostUpdateDurationSliderValueTextView.setText(stringValue + '\'');
            }
        });

        for(int i = 0; i < popupPostUpdateDifficultyLinearLayout.getChildCount(); i++) {
            int finalI = i;
            popupPostUpdateDifficultyLinearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int n = finalI;
                    redrawDifficultyDots(n);
                }
            });
        }

        // Set Window
        Window window = updateDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(R.drawable.natour_components_edittext_background);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setBackgroundBlurRadius(10);
        }
    }

    private void redrawDifficultyDots(int index) {
        Drawable enabledDifficultyDot = getResources().getDrawable(R.drawable.natour_difficulty_filter_circle);
        Drawable disabledDifficultyDot = getResources().getDrawable(R.drawable.natour_difficulty_filter_circle_disabled);

        viewModel.getEditedPost().setDifficulty(index + 1);

        for(int i = 0; i < popupPostUpdateDifficultyLinearLayout.getChildCount(); i++) {
            ImageView difficultyDot = (ImageView) popupPostUpdateDifficultyLinearLayout.getChildAt(i);
            if(i <= index) {
                difficultyDot.setImageDrawable(enabledDifficultyDot);
            } else {
                difficultyDot.setImageDrawable(disabledDifficultyDot);
            }
        }
    }

    private void showConfirmationDialog() {
        final Dialog confirmationDialog = new Dialog(this);
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDialog.setCancelable(true);
        confirmationDialog.setContentView(R.layout.popup_confirm);

        confirmationDialog.show();

        // Find Views
        popupConfirmImageViewYesButton = (ImageView) confirmationDialog.findViewById(R.id.popupConfirmImageViewYesButton);
        popupConfirmImageViewNoButton = (ImageView) confirmationDialog.findViewById(R.id.popupConfirmImageViewNoButton);

        // Set Listeners
        popupConfirmImageViewYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();
                viewModel.deleteCurrentPost();
            }
        });

        popupConfirmImageViewNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.dismiss();
            }
        });

        // Set Window
        Window window = confirmationDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(R.drawable.natour_components_edittext_background);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setBackgroundBlurRadius(10);
        }
    }

    private void showReportDialog() {
        final Dialog reportDialog = new Dialog(this);
        reportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reportDialog.setCancelable(true);
        reportDialog.setContentView(R.layout.popup_post_report);

        reportDialog.show();

        // Find Views
        popupPostReportBackButtonImageView = (ImageView) reportDialog.findViewById(R.id.popupPostReportBackButtonImageView);
        popupPostReportTitleEditText = (EditText) reportDialog.findViewById(R.id.popupPostReportTitleEditText);
        popupPostReportDescriptionEditText = (EditText) reportDialog.findViewById(R.id.popupPostReportDescriptionEditText);
        popupPostReportImageViewDoneButton = (ImageView) reportDialog.findViewById(R.id.popupPostReportImageViewDoneButton);

        // Set Listeners
        popupPostReportBackButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportDialog.hide();
            }
        });

        popupPostReportImageViewDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportDialog.hide();

                String reportTitle = popupPostReportTitleEditText.getText().toString();
                String reportDescription = popupPostReportDescriptionEditText.getText().toString();

                if(!reportTitle.replace(" ", "").isEmpty() && !reportDescription.replace(" ", "").isEmpty()) {
                    showLoadingDialog();
                    viewModel.createReport(reportTitle, reportDescription, viewModel.getCurrentPost());
                } else {
                    Toast.makeText(PostDetailsActivity.this, "Missing fields",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set Window
        Window window = reportDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(R.drawable.natour_components_edittext_background);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setBackgroundBlurRadius(10);
        }
    }

    private void observeViewModel() {

        viewModel.getOnReportSaveSuccess().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                dismissLoadingDialog();
                viewModel.getCurrentPost().setReported(true);
                reportBackgroundButton.setVisibility(View.VISIBLE);
                reportImageView.setVisibility(View.VISIBLE);
                Toast.makeText(PostDetailsActivity.this, "Post reported",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOnReportSaveFailure().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                dismissLoadingDialog();
                Toast.makeText(PostDetailsActivity.this, "Couldn't send report",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOnPostDeletionSuccess().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                dismissLoadingDialog();
                Toast.makeText(PostDetailsActivity.this, "Post deleted successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getOnPostDeletionFailure().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                dismissLoadingDialog();
                Toast.makeText(PostDetailsActivity.this, "Couldn't delete post",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOnUserQuerySuccess().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                if(viewModel.getCurrentPost().getAuthor().getEmail().equals(viewModel.getCurrentUser().getEmail())
                        || viewModel.getCurrentUser().getAdmin()) {
                    editButtonImageView.setVisibility(View.VISIBLE);
                } else {
                    editButtonImageView.setVisibility(View.GONE);
                }
                // dismissLoadingDialog();
            }
        });

        viewModel.getOnFetchUserAttributesFailure().observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException authException) {
                dismissLoadingDialog();
                Toast.makeText(PostDetailsActivity.this, "Couldn't verify permissions",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOnIncorrectFile().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                Toast.makeText(PostDetailsActivity.this, "File not compatible",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOnPostQuerySuccess().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                dismissLoadingDialog();
                Toast.makeText(PostDetailsActivity.this, "Post edited successfully",
                        Toast.LENGTH_SHORT).show();
                refreshView();
            }
        });

        viewModel.getOnPostQueryFailure().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                dismissLoadingDialog();
                Toast.makeText(PostDetailsActivity.this, "Couldn't edit post",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void refreshView() {
        if(viewModel.getCurrentPost() != null) {
            picImageView.setImageBitmap(viewModel.getCurrentPost().getPics().get(0));

            titleTextView.setText(viewModel.getCurrentPost().getTitle());
            rateButton.setVisibility(View.GONE);
            if(!viewModel.getCurrentPost().getReported()) {
                reportBackgroundButton.setVisibility(View.GONE);
                reportImageView.setVisibility(View.GONE);
            }
            if(!viewModel.getCurrentPost().getAccessibility()) {
                accessibilityBackgroundButton.setVisibility(View.GONE);
                accessibilityImageView.setVisibility(View.GONE);
            }
            // TODO: Verify addToFav ImageView Button

            if(viewModel.getCurrentPost().getAuthor() != null) {
                userPropicImageView.setImageBitmap(viewModel.getCurrentPost().getAuthor().getPropic());
                userNicknameTextView.setText(viewModel.getCurrentPost().getAuthor().getNickname());
                userNameSurnameTextView.setText(viewModel.getCurrentPost().getAuthor().getName() + " " + viewModel.getCurrentPost().getAuthor().getSurname());
                editButtonImageView.setVisibility(View.GONE);
            }

            // TODO: Add MapView link
            MapsInitializer.initialize(getApplicationContext(), Renderer.LATEST, this);

            if(googleMap != null) {
                googleMap.clear();
                onMapReady(googleMap);
            }

            descriptionTextView.setText(viewModel.getCurrentPost().getDescription());
            Log.i(TAG, "DIFFICULTY: " + viewModel.getCurrentPost().getDifficulty());
            for(int i = 0; i < difficultyLinearLayout.getChildCount(); i++) {
                if(i > viewModel.getCurrentPost().getDifficulty() - 1) {
                    difficultyLinearLayout.getChildAt(i).setVisibility(View.GONE);
                }
            }
            durationTextView.setText(viewModel.getCurrentPost().getDuration() + " minutes");
            if(!viewModel.getCurrentPost().getAccessibility()) {
                otherInfoHeaderTextView.setVisibility(View.GONE);
                accessibilityTextView.setVisibility(View.GONE);
                accessibilityMarkerImageView.setVisibility(View.GONE);
            }
            showLoadingDialog();
            viewModel.checkUserPermissions();
        } else {
            infoConstraintLayout.setVisibility(View.GONE);
        }

        if(viewModel.getCurrentPost().getEditTimestamp() != null) {
            editDateTextView.setVisibility(View.VISIBLE);
            editDateTimestampTextView.setVisibility(View.VISIBLE);
            editDateTimestampTextView.setText(viewModel.getCurrentPost().getEditTimestamp().toString() + " GMT+1");
        } else {
            editDateTextView.setVisibility(View.GONE);
            editDateTimestampTextView.setVisibility(View.GONE);
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

    private void redrawPopupPostUpdateDifficultyDots(int index) {
        Drawable enabledDifficultyDot = getResources().getDrawable(R.drawable.natour_difficulty_filter_circle);
        Drawable disabledDifficultyDot = getResources().getDrawable(R.drawable.natour_difficulty_filter_circle_disabled);

        for(int i = 0; i < popupPostUpdateDifficultyLinearLayout.getChildCount(); i++) {
            ImageView difficultyDot = (ImageView) popupPostUpdateDifficultyLinearLayout.getChildAt(i);
            if(i <= index) {
                difficultyDot.setImageDrawable(enabledDifficultyDot);
            } else {
                difficultyDot.setImageDrawable(disabledDifficultyDot);
            }
        }
    }

}