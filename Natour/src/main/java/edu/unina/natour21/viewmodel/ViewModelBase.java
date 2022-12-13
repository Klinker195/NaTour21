package edu.unina.natour21.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amplifyframework.core.Amplify;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class ViewModelBase extends ViewModel {

    private static final String TAG = ViewModelBase.class.getSimpleName();

    private final MutableLiveData<Void> onWriteGPXFileError = new MutableLiveData<>();
    private final MutableLiveData<Void> onWriteGPXFileSuccess = new MutableLiveData<>();

    public GPX generateGPXFromPointArrayList(ArrayList<LatLng> pointArrayList, String author) {
        GPX newGpx = null;
        if (pointArrayList != null && !pointArrayList.isEmpty()) {
            Route.Builder routeBuilder = Route.builder();
            for (LatLng point : pointArrayList) {
                routeBuilder.addPoint(waypoint -> waypoint.lat(point.latitude).lon(point.longitude));
            }
            Route gpxRoute = routeBuilder.build();

            GPX.Builder gpxBuilder;

            if(author != null && !author.replace(" ", "").isEmpty()) {
                gpxBuilder = GPX.builder()
                        .addRoute(gpxRoute)
                        .creator(author);
            } else {
                gpxBuilder = GPX.builder()
                        .addRoute(gpxRoute);
            }

            newGpx = gpxBuilder.build();

            Log.d(TAG, newGpx.toString());
        }
        return newGpx;
    }

    public void writeGPXFile(ArrayList<LatLng> pointArrayList, String path) {
        if (pointArrayList != null && !pointArrayList.isEmpty()) {
            path += "/tmp.gpx";
            Route.Builder routeBuilder = Route.builder();
            for (LatLng point : pointArrayList) {
                routeBuilder.addPoint(waypoint -> waypoint.lat(point.latitude).lon(point.longitude));
            }
            Route gpxRoute = routeBuilder.build();
            GPX newGpx = GPX.builder()
                    .addRoute(gpxRoute)
                    .build();
            Log.d(TAG, newGpx.toString());
            try {
                // GPX.write(newGpx, "/storage/emulated/0/Download/tmp.gpx");
                Log.d(TAG, "Path: " + path);
                GPX.write(newGpx, path);
                GPX tmpGpx = GPX.read(path);
                Log.d(TAG, tmpGpx.toString() + " - " + tmpGpx.getRoutes().toString());
                File fdelete = new File(path);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Log.d(TAG, path + " deleted.");
                    } else {
                        Log.d(TAG, path + " not found.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                onWriteGPXFileError();
            }
        }
    }

    public void writeGPXFile(GPX gpx, String title, String path) {
        if(gpx != null) {
            path += "/" + title;
            Log.d(TAG, gpx.toString());
            try {
                Log.d(TAG, "Path: " + path);
                GPX.write(gpx, path);
            } catch (IOException e) {
                e.printStackTrace();
                onWriteGPXFileError();
            }
            onWriteGPXFileSuccess();
        }
    }

    public GPX readGPXFile(InputStream fileInputStream) {
        GPX parsedGPX;
        try {
            parsedGPX = GPX.read(fileInputStream);
            Route newRoute = Route.builder().build();
            LinkedList<WayPoint> wayPointsLinkedList = new LinkedList<WayPoint>();
            if (!parsedGPX.getRoutes().isEmpty()) {
                wayPointsLinkedList = new LinkedList<WayPoint>();
                LinkedList<Route> routeLinkedList = new LinkedList<Route>(parsedGPX.getRoutes());
                for (Route route : routeLinkedList) {
                    wayPointsLinkedList.addAll(new LinkedList<WayPoint>(route.getPoints()));
                }
            } else if (!parsedGPX.getWayPoints().isEmpty()) {
                wayPointsLinkedList = new LinkedList<WayPoint>(parsedGPX.getWayPoints());
            } else if (!parsedGPX.getTracks().isEmpty()) {
                LinkedList<TrackSegment> trackSegmentsLinkedList = new LinkedList<TrackSegment>();
                LinkedList<Track> tracksLinkedList = new LinkedList<Track>(parsedGPX.getTracks());
                tracksLinkedList.forEach(track -> trackSegmentsLinkedList.addAll(new LinkedList<TrackSegment>(track.getSegments())));
                for (TrackSegment trackSegment : trackSegmentsLinkedList) {
                    wayPointsLinkedList.addAll(new LinkedList<WayPoint>(trackSegment.getPoints()));
                }
            }
            for (WayPoint wayPoint : wayPointsLinkedList) {
                newRoute = newRoute.toBuilder().addPoint(wayPoint).build();
            }

            newRoute.getPoints().forEach(wayPoint -> Log.d(TAG, wayPoint.getLatitude() + " " + wayPoint.getLongitude()));
            return GPX.builder().addRoute(newRoute).build();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void signOut() {
        Amplify.Auth.fetchAuthSession(
                success -> {
                    if (success.isSignedIn()) Amplify.Auth.signOut(
                            () -> Log.i(TAG, "Amplify sign out success"),
                            error -> Log.e(TAG, "Amplify sign out.\n" + error.toString())
                    );
                },
                error -> Log.e(TAG, "Amplify fetch auth session error.\n" + error.toString())
        );
    }

    public boolean checkEmailAndPasswordValidity(String email, String password) {
        return checkEmailValidity(email) && !password.isEmpty();
    }

    public boolean checkEmailValidity(String email) {
        boolean check = false;

        if (!email.isEmpty()) {
            Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                check = true;
            }
        }

        return check;
    }

    private void onWriteGPXFileSuccess() {
        onWriteGPXFileSuccess.postValue(null);
    }

    private void onWriteGPXFileError() {
        onWriteGPXFileError.postValue(null);
    }

    public LiveData<Void> getOnWriteGPXFileSuccess() {
        return onWriteGPXFileSuccess;
    }

    public LiveData<Void> getOnWriteGPXFileError() {
        return onWriteGPXFileError;
    }
}
