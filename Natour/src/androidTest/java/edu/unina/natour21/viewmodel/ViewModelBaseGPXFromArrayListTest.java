package edu.unina.natour21.viewmodel;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;

/**
 * <pre>
 * +-------+-----------+-----------------------------------------------+
 * | CE Id | Parameter | Value                                         |
 * +-------+-----------+-----------------------------------------------+
 * | CE1   | array     | <i>VALID</i> - Array not empty and not null          |
 * | CE2   | array     | <i>INVALID</i> - Null array                          |
 * | CE3   | array     | <i>INVALID</i> - Empty array                         |
 * | CE4   | author    | <i>VALID</i> - String not blank and not null         |
 * | CE5   | author    | <i>INVALID</i> - Null string                         |
 * | CE6   | author    | <i>INVALID</i> - Blank string                        |
 * +-------+-----------+-----------------------------------------------+
 * </pre>
 */
@RunWith(JUnit4.class)
public class ViewModelBaseGPXFromArrayListTest extends TestCase {

    ViewModelBase viewModel = new ViewModelBase();

    /**
     * Generate GPX object from ArrayList of LatLng objects and author as not blank and not null String object.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE1 | CE4
     * </p>
     *
     * <i>Result:</i> GPX object will be created with the points of the ArrayList and the creator will be the author String.
     */
    @Test
    public void generateGPXFromPointArrayList_CE1_CE4() {
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
        latLngArrayList.add(new LatLng(30d, 40d));
        latLngArrayList.add(new LatLng(40d, 60d));
        latLngArrayList.add(new LatLng(50d, 90d));

        String author = "author@gmail.com";

        GPX gpx = viewModel.generateGPXFromPointArrayList(latLngArrayList, author);

        ArrayList<WayPoint> wayPointArrayList = new ArrayList<WayPoint>(gpx.getRoutes().get(0).getPoints());

        boolean check = true;

        for(int i = 0; i < 3; i++) {
            Double lat1 = wayPointArrayList.get(i).getLatitude().doubleValue();
            Double lat2 = latLngArrayList.get(i).latitude;
            Double lng1 = wayPointArrayList.get(i).getLongitude().doubleValue();
            Double lng2 = latLngArrayList.get(i).longitude;

            if(!lat1.equals(lat2) || !lng1.equals(lng2)) check = false;
        }

        if(!gpx.getCreator().equals(author)) check = false;

        assertTrue(check);
    }

    /**
     * Generate GPX object from ArrayList of LatLng objects and author as null String object.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE1 | CE5
     * </p>
     *
     * <i>Result:</i> GPX object will be created with the points of the ArrayList and the creator will be a GPX default.
     */
    @Test
    public void generateGPXFromPointArrayList_CE1_CE5() {
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
        latLngArrayList.add(new LatLng(30d, 40d));
        latLngArrayList.add(new LatLng(40d, 60d));
        latLngArrayList.add(new LatLng(50d, 90d));

        String author = null;

        GPX gpx = viewModel.generateGPXFromPointArrayList(latLngArrayList, author);

        ArrayList<WayPoint> wayPointArrayList = new ArrayList<WayPoint>(gpx.getRoutes().get(0).getPoints());

        boolean check = true;

        for(int i = 0; i < 3; i++) {
            Double lat1 = wayPointArrayList.get(i).getLatitude().doubleValue();
            Double lat2 = latLngArrayList.get(i).latitude;
            Double lng1 = wayPointArrayList.get(i).getLongitude().doubleValue();
            Double lng2 = latLngArrayList.get(i).longitude;

            if(!lat1.equals(lat2) || !lng1.equals(lng2)) check = false;
        }

        assertTrue(check);
    }

    /**
     * Generate GPX object from ArrayList of LatLng objects and author as blank String object.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE1 | CE6
     * </p>
     *
     * <i>Result:</i> GPX object will be created with the points of the ArrayList and the creator will be a GPX default.
     */
    @Test
    public void generateGPXFromPointArrayList_CE1_CE6() {
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
        latLngArrayList.add(new LatLng(30d, 40d));
        latLngArrayList.add(new LatLng(40d, 60d));
        latLngArrayList.add(new LatLng(50d, 90d));

        String author = "";

        GPX gpx = viewModel.generateGPXFromPointArrayList(latLngArrayList, author);

        ArrayList<WayPoint> wayPointArrayList = new ArrayList<WayPoint>(gpx.getRoutes().get(0).getPoints());

        boolean check = true;

        for(int i = 0; i < 3; i++) {
            Double lat1 = wayPointArrayList.get(i).getLatitude().doubleValue();
            Double lat2 = latLngArrayList.get(i).latitude;
            Double lng1 = wayPointArrayList.get(i).getLongitude().doubleValue();
            Double lng2 = latLngArrayList.get(i).longitude;

            if(!lat1.equals(lat2) || !lng1.equals(lng2)) check = false;
        }

        assertTrue(check);
    }

    /**
     * Generate GPX object from a NULL ArrayList of LatLng objects and author as not blank and not null String object.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE2 | CE4
     * </p>
     *
     * <i>Result:</i> GPX object will be NULL.
     */
    @Test
    public void generateGPXFromPointArrayList_CE2_CE4() {
        ArrayList<LatLng> latLngArrayList = null;

        String author = "author@gmail.com";

        GPX gpx = viewModel.generateGPXFromPointArrayList(latLngArrayList, author);

        assertNull(gpx);
    }

    /**
     * Generate GPX object from a NULL ArrayList of LatLng objects and author as null String object.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE2 | CE5
     * </p>
     *
     * <i>Result:</i> GPX object will be NULL.
     */
    @Test
    public void generateGPXFromPointArrayList_CE2_CE5() {
        ArrayList<LatLng> latLngArrayList = null;

        String author = null;

        GPX gpx = viewModel.generateGPXFromPointArrayList(latLngArrayList, author);

        assertNull(gpx);
    }

    /**
     * Generate GPX object from a NULL ArrayList of LatLng objects and author as blank String object.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE2 | CE6
     * </p>
     *
     * <i>Result:</i> GPX object will be NULL.
     */
    @Test
    public void generateGPXFromPointArrayList_CE2_CE6() {
        ArrayList<LatLng> latLngArrayList = null;

        String author = "";

        GPX gpx = viewModel.generateGPXFromPointArrayList(latLngArrayList, author);

        assertNull(gpx);
    }

    /**
     * Generate GPX object from an empty ArrayList of LatLng objects and author as not blank and not null String object.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE3 | CE4
     * </p>
     *
     * <i>Result:</i> GPX object will be NULL.
     */
    @Test
    public void generateGPXFromPointArrayList_CE3_CE4() {
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();

        String author = "author@gmail.com";

        GPX gpx = viewModel.generateGPXFromPointArrayList(latLngArrayList, author);

        assertNull(gpx);
    }

    /**
     * Generate GPX object from an empty ArrayList of LatLng objects and author as null String object.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE3 | CE5
     * </p>
     *
     * <i>Result:</i> GPX object will be NULL.
     */
    @Test
    public void generateGPXFromPointArrayList_CE3_CE5() {
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();

        String author = null;

        GPX gpx = viewModel.generateGPXFromPointArrayList(latLngArrayList, author);

        assertNull(gpx);
    }

    /**
     * Generate GPX object from an empty ArrayList of LatLng objects and author as blank String object.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE3 | CE6
     * </p>
     *
     * <i>Result:</i> GPX object will be NULL.
     */
    @Test
    public void generateGPXFromPointArrayList_CE3_CE6() {
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();

        String author = "";

        GPX gpx = viewModel.generateGPXFromPointArrayList(latLngArrayList, author);

        assertNull(gpx);
    }

}