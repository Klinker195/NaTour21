package edu.unina.natour21.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.amplifyframework.core.Amplify;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.KeyboardHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.RouteExplorationViewModel;

public class RouteExplorationActivity extends AppCompatActivity {

    private static final String TAG = RouteExplorationActivity.class.getSimpleName();

    private FirebaseAnalytics firebaseAnalytics;

    private RouteExplorationViewModel viewModel;

    private Button signOutButton;
    private ActionBar actionBar;
    private BottomNavigationView navigationView;
    private TextView natourTextView;
    private TextView welcomeTextView;
    private ImageView createPostImageViewButton;

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;

    /*
    MODE:
    Use 0 for community and 1 for post fav list.
    */
    private static final String ARG_NAME_COMMUNITY_1 = "MODE";

    private final BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.nav_community:
                    Bundle args = new Bundle();
                    args.putInt(ARG_NAME_COMMUNITY_1, 0);
                    Navigation.findNavController(getSupportFragmentManager().getPrimaryNavigationFragment().getView()).navigate(R.id.action_global_community, args);
                    return true;

                case R.id.nav_home:
                    Navigation.findNavController(getSupportFragmentManager().getPrimaryNavigationFragment().getView()).navigate(R.id.action_global_home);
                    return true;

                case R.id.nav_profile:
                    Navigation.findNavController(getSupportFragmentManager().getPrimaryNavigationFragment().getView()).navigate(R.id.action_global_profile);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        KeyboardHandler.hideKeyboard(this);

        // Set Content View
        setContentView(R.layout.activity_route_exploration);

        // Set View Model
        viewModel = new ViewModelProvider(this).get(RouteExplorationViewModel.class);

        // Find Views
        navigationView = (BottomNavigationView) findViewById(R.id.routeExplorationNavigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        natourTextView = (TextView) findViewById(R.id.routeExplorationNatourTextView);
        welcomeTextView = (TextView) findViewById(R.id.routeExplorationWelcomeTextView);
        createPostImageViewButton = (ImageView) findViewById(R.id.routeExplorationCreatePostImageViewButton);

        sharedPrefs = getSharedPreferences("accessPrefs", Context.MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();

        // Next two lines should be only for testing purposes
        sharedPrefsEditor.putBoolean("firstAccess", true);
        sharedPrefsEditor.apply();


        if (sharedPrefs.getBoolean("firstAccess", true)) {
            welcomeTextView.setVisibility(View.VISIBLE);
            sharedPrefsEditor.putBoolean("firstAccess", false);
            sharedPrefsEditor.apply();
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_1500ms_offset_1000ms);
            welcomeTextView.startAnimation(fadeOutAnimation);
            welcomeTextView.setVisibility(View.INVISIBLE);
        } else {
            welcomeTextView.setVisibility(View.INVISIBLE);
        }

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setFullscreen(this);
        designHandler.setTextGradient(natourTextView);

        createPostImageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(RouteExplorationActivity.this, PostCreationActivity.class);
                startActivity(switchActivityIntent);
            }
        });
    }
}