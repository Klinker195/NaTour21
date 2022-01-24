package edu.unina.natour21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.unina.natour21.utility.NatourUIDesignHelper;

public class MainActivity extends AppCompatActivity {

    AnimatedVectorDrawable moonAnimation;
    ImageView moonImageView;
    AnimatedVectorDrawable earthAnimation;
    ImageView earthImageView;

    TextView natourTextView;

    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.natour_signin_activity);

        moonImageView = findViewById(R.id.moonImageView);
        moonAnimation = (AnimatedVectorDrawable) moonImageView.getDrawable();
        moonAnimation.start();

        earthImageView = findViewById(R.id.earthImageView);
        earthAnimation = (AnimatedVectorDrawable) earthImageView.getDrawable();
        earthAnimation.start();

        natourTextView = (TextView) findViewById(R.id.natourTextView);

        signInButton = (Button) findViewById(R.id.signInButton);

        NatourUIDesignHelper designHelper = new NatourUIDesignHelper();

        designHelper.setTextGradient(natourTextView);
        designHelper.setTextGradient(signInButton);

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
}