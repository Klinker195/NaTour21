package edu.unina.natour21;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import edu.unina.natour21.dto.PostDTO;
import edu.unina.natour21.models.Post;
import io.jenetics.jpx.WayPoint;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);


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


    }
}