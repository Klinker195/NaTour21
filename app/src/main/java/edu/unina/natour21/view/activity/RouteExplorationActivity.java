package edu.unina.natour21.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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

import com.amplifyframework.core.Amplify;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.prefs.Preferences;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.RouteExplorationViewModel;

public class RouteExplorationActivity extends AppCompatActivity {

    private Button signOutButton;
    private ActionBar actionBar;
    private BottomNavigationView navigationView;
    private TextView natourTextView;
    private TextView welcomeTextView;
    private ImageView createPostImageViewButton;

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;

    private RouteExplorationViewModel viewModel;

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.nav_community:


                    /*
                    actionBar.setTitle("Home");
                    HomeFragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "");
                    fragmentTransaction.commit();
                    */
                    return true;

                case R.id.nav_home:
                    Amplify.Auth.signOut(
                            () -> finish(),
                            error -> Log.e("AmplifySignOut", error.toString())
                    );
                    if(Amplify.Auth.getCurrentUser() != null) Amplify.Auth.signOut(
                            () -> finish(),
                            error -> Log.e("AmplifySignOut", error.toString())
                    );
                    /*
                    actionBar.setTitle("Profile");
                    ProfileFragment fragment1 = new ProfileFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.content, fragment1);
                    fragmentTransaction1.commit();
                    */
                    return true;

                case R.id.nav_profile:
                    /*
                    actionBar.setTitle("Users");
                    UsersFragment fragment2 = new UsersFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "");
                    fragmentTransaction2.commit();
                    */
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set ViewModel
        // viewModel = new ViewModelProvider(this).get(RouteExplorationViewModel.class);

        // Set Content View
        setContentView(R.layout.activity_route_exploration);

        // Set Action Bar
        // actionBar = getSupportActionBar();
        // actionBar.setTitle("Nav Activity");

        // Set View Model
        viewModel = new ViewModelProvider(this).get(RouteExplorationViewModel.class);

        // Find Views
        navigationView = (BottomNavigationView) findViewById(R.id.routeExplorationNavigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        natourTextView = (TextView) findViewById(R.id.routeExplorationNatourTextView);
        welcomeTextView = (TextView) findViewById(R.id.routeExplorationWelcomeTextView);
        createPostImageViewButton = (ImageView) findViewById(R.id.routeExplorationCreatePostImageViewButton);
        // actionBar.setTitle("Home");

        // SharedPreferences sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        //welcomeTextView.setVisibility(View.INVISIBLE);

        sharedPrefs = getSharedPreferences("accessPrefs", Context.MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();

        // TODO: Next two lines are only for testing purposes
        sharedPrefsEditor.putBoolean("firstAccess", true);
        sharedPrefsEditor.apply();


        if(sharedPrefs.getBoolean("firstAccess", true)) {
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

        // Set First Fragment
        // viewModel.setFragment(RouteExplorationFragment.newInstance(), getSupportFragmentManager().beginTransaction());

        /*
        signOutButton = (Button) findViewById(R.id.button3);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Amplify.Auth.getCurrentUser() != null) Amplify.Auth.signOut(
                        () -> finish(),
                        error -> Log.e("AmplifySignOut", error.toString())
                );
            }
        });
        */

    }
}