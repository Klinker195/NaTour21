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
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.unina.natour21.dto.FavCollectionDTO;
import edu.unina.natour21.dto.PostDTO;
import edu.unina.natour21.dto.ReportDTO;
import edu.unina.natour21.dto.UserDTO;
import edu.unina.natour21.model.FavCollection;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.model.User;
import edu.unina.natour21.retrofit.AmazonAPI;
import edu.unina.natour21.retrofit.IFavCollectionAPI;
import edu.unina.natour21.retrofit.IPostAPI;
import edu.unina.natour21.retrofit.IReportAPI;
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

public class PostDetailsViewModel extends ViewModelBase {

    private static final String TAG = PostDetailsViewModel.class.getSimpleName();

    private final MutableLiveData<Void> onReportSaveSuccess = new MutableLiveData<Void>();
    private final MutableLiveData<Void> onReportSaveFailure = new MutableLiveData<Void>();

    private final MutableLiveData<Void> onUserQuerySuccess = new MutableLiveData<>();
    private final MutableLiveData<Void> onUserQueryFailure = new MutableLiveData<>();

    private final MutableLiveData<AuthException> onFetchUserAttributesFailure = new MutableLiveData<>();

    private final MutableLiveData<Void> onIncorrectFile = new MutableLiveData<>();

    private final MutableLiveData<Void> onPostQuerySuccess = new MutableLiveData<>();
    private final MutableLiveData<Void> onPostQueryFailure = new MutableLiveData<>();

    private final MutableLiveData<Void> onPostDeletionSuccess = new MutableLiveData<>();
    private final MutableLiveData<Void> onPostDeletionFailure = new MutableLiveData<>();

    private final MutableLiveData<Boolean> onAddPostToFavCollectionSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> onAddPostToFavCollectionFailure = new MutableLiveData<>();

    private final MutableLiveData<Boolean> onRemovePostFromCollectionSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> onRemovePostFromCollectionFailure = new MutableLiveData<>();

    private final MutableLiveData<ArrayList<FavCollection>> onCurrentUserFavCollectionSuccess = new MutableLiveData<ArrayList<FavCollection>>();
    private final MutableLiveData<Void> onCurrentUserFavCollectionFailure = new MutableLiveData<Void>();
    private ArrayList<FavCollection> userFavCollections = new ArrayList<FavCollection>();

    private Post currentPost;
    private Post editedPost;
    private User currentUser;

    private static final double LN2 = 0.6931471805599453;
    private static final int WORLD_PX_HEIGHT = 256;
    private static final int WORLD_PX_WIDTH = 256;
    private static final int ZOOM_MAX = 21;

    public GPX readGPXFile(InputStream fileInputStream) {
        GPX gpx = super.readGPXFile(fileInputStream);
        if(gpx == null) onIncorrectFile();
        return gpx != null ? gpx : null;
    }

    public void removePostFromCollection(Long collectionId) {
        IFavCollectionAPI favCollectionAPI = AmazonAPI.getClient().create(IFavCollectionAPI.class);
        Call<Boolean> favCollectionCall = favCollectionAPI.removePostFromFavCollection(collectionId, currentPost.getId());

        favCollectionCall.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body() != null) {
                    onRemovePostFromCollectionSuccess();
                } else {
                    onRemovePostFromCollectionFailure();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                onRemovePostFromCollectionFailure();
            }
        });

    }

    public void addPostToFavCollection(Long collectionId) {
        IFavCollectionAPI favCollectionAPI = AmazonAPI.getClient().create(IFavCollectionAPI.class);
        Call<Boolean> favCollectionCall = favCollectionAPI.addPostToFavCollection(collectionId, currentPost.getId());

        favCollectionCall.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body() != null) {
                    onAddPostToFavCollectionSuccess();
                } else {
                    onAddPostToFavCollectionFailure();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                onAddPostToFavCollectionFailure();
            }
        });
    }

    public void getCurrentUserFavCollections() {
        IFavCollectionAPI favCollectionAPI = AmazonAPI.getClient().create(IFavCollectionAPI.class);
        Call<List<FavCollectionDTO>> favCollectionCall = favCollectionAPI.getFavCollectionByEmail(currentUser.getEmail());

        favCollectionCall.enqueue(new Callback<List<FavCollectionDTO>>() {
            @Override
            public void onResponse(Call<List<FavCollectionDTO>> call, Response<List<FavCollectionDTO>> response) {
                if (response.body() != null) {
                    ArrayList<FavCollectionDTO> favCollectionDTOArrayList = new ArrayList<FavCollectionDTO>(response.body());
                    ArrayList<FavCollection> favCollectionArrayList = new ArrayList<FavCollection>();
                    for (FavCollectionDTO favCollectionDTO : favCollectionDTOArrayList) {
                        favCollectionArrayList.add(new FavCollection(favCollectionDTO));
                    }
                    userFavCollections = favCollectionArrayList;
                    onCurrentUserFavCollectionSuccess(favCollectionArrayList);
                } else {
                    onCurrentUserFavCollectionFailure();
                }
            }

            @Override
            public void onFailure(Call<List<FavCollectionDTO>> call, Throwable t) {
                onCurrentUserFavCollectionFailure();
            }
        });
    }

    public void createReport(String title, String description, Post post) {
        Log.d(TAG, "\nREPORT TITLE: " + title);
        Log.d(TAG, "ISSUES/INACCURACIES: " + description);

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setTitle(title);
        reportDTO.setDescription(description);
        PostDTO postDTO = new PostDTO(post);
        reportDTO.setPost(postDTO);

        Log.i(TAG, "POST ID: " + reportDTO.getPost().getId());

        IReportAPI reportAPI = AmazonAPI.getClient().create(IReportAPI.class);
        Call<Boolean> reportCall = reportAPI.saveNewReport(reportDTO);

        Log.i(TAG, reportCall.request().toString());

        reportCall.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body() != null) {
                    onReportSaveSuccess();
                    Log.d(TAG, "SUCCESS");
                } else {
                    onReportSaveFailure();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d(TAG, "REPORT NOT CREATED");
                t.printStackTrace();
                onReportSaveFailure();
            }
        });
    }

    public void deleteCurrentPost() {
        IPostAPI postAPI = AmazonAPI.getClient().create(IPostAPI.class);
        Call<Void> postCall = postAPI.deletePostById(currentPost.getId());

        postCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "POST DELETION SUCCESS");
                onPostDeletionSuccess();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "POST NOT DELETED");
                onPostDeletionFailure();
            }
        });

    }

    public void updatePost(@NonNull String gpxPath) throws NullPointerException {

        Long routeId = editedPost.getId();
        String routeName = editedPost.getTitle();
        String routeDescription = editedPost.getDescription();
        GPX routeGPX = editedPost.getRoute();
        Double routeStartLat = editedPost.getStartLat();
        Double routeStartLng = editedPost.getStartLng();
        Bitmap routeBitmap = editedPost.getPics().get(0);
        Integer routeDifficulty = editedPost.getDifficulty();
        Integer routeDuration = editedPost.getDuration();
        Boolean routeAccessibility = editedPost.getAccessibility();
        User author = editedPost.getAuthor();

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
                        Call<UserDTO> userCall = userAPI.getUserByEmail(editedPost.getAuthor().getEmail());
                        Log.d(TAG, "fetchUserAttributes accept");
                        userCall.enqueue(new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                Log.d(TAG, "userCall onResponse");
                                if (response.body() != null) {
                                    PostDTO postDTO = new PostDTO();

                                    postDTO.setId(routeId);

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
                                                currentPost = editedPost;
                                                Log.d(TAG, "SUCCESS");
                                            } else {
                                                onPostQueryFailure();
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
                onFetchUserAttributesFailure.postValue(value);
            }
        });
    }

    public void checkUserPermissions() {
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
                                if (response.body() != null) {
                                    currentUser = new User(response.body());
                                    onUserQuerySuccess();
                                } else {
                                    onUserQuerySuccess();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDTO> call, Throwable t) {
                                onUserQueryFailure();
                            }
                        });
                    }
                }
            }
        }, new Consumer<AuthException>() {
            @Override
            public void accept(@NonNull AuthException value) {
                onFetchUserAttributesFailure(value);
            }
        });
    }

    public ArrayList<LatLng> getPointArrayListFromGPX(GPX route) {
        ArrayList<WayPoint> wayPointArrayList = new ArrayList<WayPoint>(route.getRoutes().get(0).getPoints());
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();

        for (WayPoint wayPoint : wayPointArrayList) {
            latLngArrayList.add(new LatLng(wayPoint.getLatitude().doubleValue(), wayPoint.getLongitude().doubleValue()));
        }

        return latLngArrayList;
    }

    public int getBoundsZoomLevel(LatLngBounds bounds, int mapWidthPx, int mapHeightPx) {

        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;

        double latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI;

        double lngDiff = ne.longitude - sw.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

        double latZoom = zoom(mapHeightPx, WORLD_PX_HEIGHT, latFraction);
        double lngZoom = zoom(mapWidthPx, WORLD_PX_WIDTH, lngFraction);

        int result = Math.min((int)latZoom, (int)lngZoom);
        result -= 2;
        if(result <= 0) result = 1;
        return Math.min(result, ZOOM_MAX);
    }

    private double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private double zoom(int mapPx, int worldPx, double fraction) {
        return Math.floor(Math.log(mapPx / worldPx / fraction) / LN2);
    }

    private void onReportSaveSuccess() {
        onReportSaveSuccess.postValue(null);
    }

    private void onReportSaveFailure() {
        onReportSaveFailure.postValue(null);
    }

    private void onRemovePostFromCollectionSuccess() {
        onRemovePostFromCollectionSuccess.postValue(true);
    }

    private void onRemovePostFromCollectionFailure() {
        onRemovePostFromCollectionFailure.postValue(false);
    }

    private void onCurrentUserFavCollectionSuccess(ArrayList<FavCollection> favCollectionArrayList) {
        onCurrentUserFavCollectionSuccess.postValue(favCollectionArrayList);
    }

    private void onCurrentUserFavCollectionFailure() {
        onCurrentUserFavCollectionFailure.postValue(null);
    }

    public void onPostDeletionSuccess() {
        onPostDeletionSuccess.postValue(null);
    }

    public void onPostDeletionFailure() {
        onPostDeletionFailure.postValue(null);
    }

    public void onUserQuerySuccess() {
        onUserQuerySuccess.postValue(null);
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

    public void onAddPostToFavCollectionSuccess() {
        onAddPostToFavCollectionSuccess.postValue(true);
    }

    public void onAddPostToFavCollectionFailure() {
        onAddPostToFavCollectionFailure.postValue(false);
    }

    public void onFetchUserAttributesFailure(AuthException authException) {
        onFetchUserAttributesFailure.postValue(authException);
    }

    public void onIncorrectFile() {
        onIncorrectFile.postValue(null);
    }


    public LiveData<Boolean> getOnRemovePostFromCollectionSuccess() {
        return onRemovePostFromCollectionSuccess;
    }

    public LiveData<Boolean> getOnRemovePostFromCollectionFailure() {
        return onRemovePostFromCollectionFailure;
    }

    public LiveData<Void> getOnPostDeletionSuccess() {
        return onPostDeletionSuccess;
    }

    public LiveData<Void> getOnPostDeletionFailure() {
        return onPostDeletionFailure;
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


    public LiveData<Void> getOnUserQuerySuccess() {
        return onUserQuerySuccess;
    }

    public LiveData<AuthException> getOnFetchUserAttributesFailure() {
        return onFetchUserAttributesFailure;
    }

    public LiveData<Void> getOnReportSaveSuccess() {
        return onReportSaveSuccess;
    }

    public LiveData<Void> getOnReportSaveFailure() {
        return onReportSaveFailure;
    }

    public LiveData<Void> getOnIncorrectFile() {
        return onIncorrectFile;
    }

    public LiveData<Boolean> getOnAddPostToFavCollectionSuccess() {
        return onAddPostToFavCollectionSuccess;
    }

    public LiveData<Boolean> getOnAddPostToFavCollectionFailure() {
        return onAddPostToFavCollectionFailure;
    }

    public LiveData<ArrayList<FavCollection>> getOnCurrentUserFavCollectionSuccess() {
        return onCurrentUserFavCollectionSuccess;
    }

    public LiveData<Void> getOnCurrentUserFavCollectionFailure() {
        return onCurrentUserFavCollectionFailure;
    }


    public Post getCurrentPost() {
        return currentPost;
    }

    public void setCurrentPost(Post currentPost) {
        this.currentPost = currentPost;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Post getEditedPost() {
        return editedPost;
    }

    public void setEditedPost(Post editedPost) {
        this.editedPost = editedPost;
    }

    public ArrayList<FavCollection> getUserFavCollections() {
        return userFavCollections;
    }
}
