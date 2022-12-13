package edu.unina.natour21.viewmodel;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.unina.natour21.dto.PostDTO;
import edu.unina.natour21.dto.UserDTO;
import edu.unina.natour21.retrofit.AmazonAPI;
import edu.unina.natour21.retrofit.IPostAPI;
import edu.unina.natour21.retrofit.IUserAPI;
import edu.unina.natour21.view.activity.PostFilteringMapsActivity;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCreationViewModel extends ViewModelBase {

    private static final String TAG = PostCreationViewModel.class.getSimpleName();

    private final MutableLiveData<Void> onUserQueryFailure = new MutableLiveData<>();

    private final MutableLiveData<Void> onPostQuerySuccess = new MutableLiveData<>();
    private final MutableLiveData<Void> onPostQueryFailure = new MutableLiveData<>();

    private final MutableLiveData<AuthException> onFetchUserAttributesFailure = new MutableLiveData<>();

    private final MutableLiveData<Void> onIncorrectFile = new MutableLiveData<>();

    public GPX readGPXFile(InputStream fileInputStream) {
        GPX gpx = super.readGPXFile(fileInputStream);
        if(gpx == null) onIncorrectFile();
        return gpx != null ? gpx : null;
    }

    public void createPost(
            @NonNull String routeName,
            String routeDescription,
            @NonNull GPX routeGPX,
            @NonNull Double routeStartLat,
            @NonNull Double routeStartLng,
            @NonNull String gpxPath,
            Bitmap routeBitmap,
            @NonNull Integer routeDifficulty,
            @NonNull Integer routeDuration,
            @NonNull Boolean routeAccessibility) throws NullPointerException {

        if (gpxPath == null) throw new NullPointerException();

        gpxPath += "/tmp.gpx";

        String routePictureBase64 = null;

        if (routeBitmap != null) {
            ByteArrayOutputStream bitmapOutputStream = new ByteArrayOutputStream();
            routeBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bitmapOutputStream);
            byte[] byteArray = bitmapOutputStream.toByteArray();
            routePictureBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        Log.d(TAG, "\nROUTE NAME: " + routeName);
        Log.d(TAG, "DESCRIPTION: " + routeDescription);
        Log.d(TAG, "COORDS:\n");
        routeGPX.getRoutes().forEach(route -> route.getPoints().forEach(wayPoint -> Log.d(TAG, "    LAT: " + wayPoint.getLatitude() + " | LNG: " + wayPoint.getLongitude())));
        if (routePictureBase64 != null) {
            Log.d(TAG, "DRAWABLE: " + routePictureBase64.substring(0, 100) + " [...]");
        }
        Log.d(TAG, "DIFFICULTY: " + routeDifficulty.toString() + " | DURATION: " + routeDuration.toString() + " | ACCESSIBILITY: " + routeAccessibility.toString());

        String finalRoutePictureBase64 = routePictureBase64;
        String finalGpxPath = gpxPath;

        Amplify.Auth.fetchUserAttributes(new Consumer<List<AuthUserAttribute>>() {
            @Override
            public void accept(@NonNull List<AuthUserAttribute> attributes) {
                for (AuthUserAttribute value : attributes) {
                    if (value.getKey().getKeyString().equals("email")) {
                        Log.d(TAG, value.getKey().getKeyString());
                        Log.d(TAG, value.getValue());
                        IUserAPI userAPI = AmazonAPI.getClient().create(IUserAPI.class);
                        Call<UserDTO> userCall = userAPI.getUserByEmail(value.getValue().replace(" ", ""));
                        Log.d(TAG, "fetchUserAttributes accept");
                        userCall.enqueue(new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                Log.d(TAG, "userCall onResponse");
                                if (response.body() != null) {
                                    PostDTO postDTO = new PostDTO();

                                    postDTO.setTitle(routeName);
                                    postDTO.setDescription(routeDescription);

                                    try {
                                        GPX.write(routeGPX, finalGpxPath);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        onPostQueryFailure();
                                        return;
                                    }

                                    byte[] byteArray;
                                    try {
                                        byteArray = Files.readAllBytes(Paths.get(finalGpxPath));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        onPostQueryFailure();
                                        return;
                                    }
                                    String routeGpxBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                                    File gpxFile = new File(finalGpxPath);

                                    if (gpxFile.exists()) {
                                        if (gpxFile.delete()) {
                                            Log.d(TAG, gpxFile + " DELETED");
                                        } else {
                                            Log.d(TAG, gpxFile + " NOT FOUND");
                                        }
                                    }

                                    postDTO.setRoute(routeGpxBase64);

                                    postDTO.setStartLat(routeStartLat);
                                    postDTO.setStartLng(routeStartLng);

                                    if (finalRoutePictureBase64 != null) {
                                        Log.d(TAG, finalRoutePictureBase64);
                                        LinkedList<String> pics = new LinkedList<String>();
                                        pics.add(finalRoutePictureBase64);
                                        postDTO.setPics(pics);
                                    }

                                    postDTO.setDifficulty(routeDifficulty);
                                    postDTO.setDuration(routeDuration);

                                    postDTO.setRate(0f);
                                    postDTO.setReported(false);

                                    postDTO.setAccessibility(routeAccessibility);

                                    postDTO.setAuthor(response.body());

                                    IPostAPI postAPI = AmazonAPI.getClient().create(IPostAPI.class);
                                    Call<PostDTO> postCall = postAPI.saveNewPost(postDTO);
                                    postCall.enqueue(new Callback<PostDTO>() {
                                        @Override
                                        public void onResponse(Call<PostDTO> call, Response<PostDTO> response) {
                                            Log.d(TAG, "postCall onResponse");
                                            if (response.body() != null) {
                                                onPostQuerySuccess();
                                                Log.d(TAG, "SUCCESS");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<PostDTO> call, Throwable t) {
                                            Log.d(TAG, "POST NOT CREATED");
                                            t.printStackTrace();
                                            onPostQueryFailure();
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "NULL USER");
                                    Log.d(TAG, response.message());
                                    onUserQueryFailure();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDTO> call, Throwable t) {
                                Log.d(TAG, "USER NOT FOUND");
                                t.printStackTrace();
                                onUserQueryFailure();
                            }
                        });
                    }
                }
            }
        }, new Consumer<AuthException>() {
            @Override
            public void accept(@NonNull AuthException value) {
                value.printStackTrace();
                onFetchUserAttributesFailure(value);
            }
        });
    }


    public void onUserQueryFailure() {
        onUserQueryFailure.postValue(null);
    }

    public void onPostQuerySuccess() {
        onPostQuerySuccess.postValue(null);
    }

    public void onPostQueryFailure() {
        onPostQueryFailure.postValue(null);
    }

    public void onFetchUserAttributesFailure(AuthException authException) {
        onFetchUserAttributesFailure.postValue(authException);
    }

    public void onIncorrectFile() {

    }

    public LiveData<Void> getOnIncorrectFile() {
        return onIncorrectFile;
    }

    public LiveData<Void> getOnUserQueryFailure() {
        return onUserQueryFailure;
    }

    public LiveData<Void> getOnPostQuerySuccess() {
        return onPostQuerySuccess;
    }

    public LiveData<Void> getOnPostQueryFailure() {
        return onPostQueryFailure;
    }

    public LiveData<AuthException> getOnFetchUserAttributesFailure() {
        return onFetchUserAttributesFailure;
    }
}
