package edu.unina.natour21.view.activity;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.amplifyframework.core.Amplify;
import com.google.firebase.analytics.FirebaseAnalytics;

import edu.unina.natour21.R;
import edu.unina.natour21.application.NatourApplication;
import edu.unina.natour21.utility.KeyboardHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.IntroductionViewModel;

public class IntroductionActivity extends AppCompatActivity {

    private static final String TAG = IntroductionActivity.class.getSimpleName();

    private FirebaseAnalytics firebaseAnalytics;

    private IntroductionViewModel viewModel;

    private AnimatedVectorDrawable moonAnimation;
    private ImageView moonImageView;
    private AnimatedVectorDrawable earthAnimation;
    private ImageView earthImageView;
    private TextView natourTextView;
    private Button signInButton;
    private ConstraintLayout planetLayout;

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        KeyboardHandler.hideKeyboard(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(Amplify.Auth.getCurrentUser() != null) {
            Intent switchActivityIntent = new Intent(this, RouteExplorationActivity.class);
            this.startActivity(switchActivityIntent);
            finish();
        }

        // Set ViewModel
        viewModel = new ViewModelProvider(this).get(IntroductionViewModel.class);

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setFullscreen(this);

        // Set Content View
        setContentView(R.layout.natour_introduction_activity);

        // Find Views
        moonImageView = findViewById(R.id.introductionMoonImageView);
        earthImageView = findViewById(R.id.introductionEarthImageView);
        natourTextView = (TextView) findViewById(R.id.introductionNatourTextView);
        signInButton = (Button) findViewById(R.id.introductionStartButton);
        planetLayout = (ConstraintLayout) findViewById(R.id.introductionPlanetLayout);

        // Start Anims
        moonAnimation = (AnimatedVectorDrawable) moonImageView.getDrawable();
        moonAnimation.start();
        earthAnimation = (AnimatedVectorDrawable) earthImageView.getDrawable();
        earthAnimation.start();
        natourTextView.startAnimation((Animation) AnimationUtils.loadAnimation(IntroductionActivity.this, R.anim.up_smooth_in));
        signInButton.startAnimation((Animation) AnimationUtils.loadAnimation(IntroductionActivity.this, R.anim.up_smooth_in_slow));
        planetLayout.startAnimation((Animation) AnimationUtils.loadAnimation(IntroductionActivity.this, R.anim.up_smooth_in_slower));

        // Set Gradients
        designHandler.setTextGradient(natourTextView);
        designHandler.setTextGradient(signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(IntroductionActivity.this, AuthenticationActivity.class);
                finish();
                startActivity(switchActivityIntent);
            }
        });
    }
}