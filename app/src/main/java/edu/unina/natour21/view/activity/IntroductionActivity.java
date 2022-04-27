package edu.unina.natour21.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.IntroductionViewModel;

public class IntroductionActivity extends AppCompatActivity {

    private IntroductionViewModel viewModel;

    private AnimatedVectorDrawable moonAnimation;
    private ImageView moonImageView;
    private AnimatedVectorDrawable earthAnimation;
    private ImageView earthImageView;
    private TextView natourTextView;
    private Button signInButton;
    private ConstraintLayout planetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                startActivity(switchActivityIntent);
                finish();
            }
        });

        /*
        Amplify.Auth.fetchAuthSession(
                result -> Log.i("AmplifyQuickstart", result.toString()),
                error -> Log.e("AmplifyQuickstart", error.toString())
        );
        */

        /*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://ec2-15-161-60-195.eu-south-1.compute.amazonaws.com:8080/post/1";

                JsonObjectRequest jsonRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            Gson gson = new Gson();
                            PostDTO postDTO = gson.fromJson(response.toString(), PostDTO.class);

                            Log.i("POST", response.toString());
                            Log.i("POST", postDTO.toString());

                            Post post = new Post(postDTO);

                            Log.i("POST", post.toString());

                            if(post.getPics() != null) {
                                Log.i("IMG", "Image set!");
                                imageView.setImageBitmap(post.getPics());
                            }

                            if(post.getGpx() != null) {
                                Log.i("GPX", "Gpx set!");
                                String gpx;
                                List<WayPoint> pointList = post.getGpx().getTracks().get(0).getSegments().get(0).getPoints();

                                StringBuilder sb = new StringBuilder("");

                                for(int i = 0; i < pointList.size(); i++) {
                                    String tmpString = "Lat: " + pointList.get(i).getLatitude() + " | Lng: " + pointList.get(i).getLongitude() + "\n";
                                    sb.append(tmpString);
                                }

                                gpx = sb.toString();

                                textView.setText(gpx);
                            }

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("LOG", error.toString());
                    }
                });

                queue.add(jsonRequest);
            }
        });
         */
    }

    /*
    @Override
    protected void onPause() {
        super.onPause();
        earthAnimation.stop();
        moonAnimation.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        earthAnimation.start();
        moonAnimation.start();
    }
     */

}