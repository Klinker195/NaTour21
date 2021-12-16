package edu.unina.natour21;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.jenetics.jpx.GPX;

public class Post {

    private Long Id;
    private Bitmap Pics;
    private GPX Gpx;

    public Post(PostDTO postDTO) {
        super();

        this.Id = postDTO.getId();

        if(postDTO.getPics() != null) {
            byte[] decodedBase64 = Base64.decode(postDTO.getPics(), Base64.DEFAULT);
            this.Pics = BitmapFactory.decodeByteArray(decodedBase64, 0, decodedBase64.length);
        } else {
            this.Pics = null;
        }

        if(postDTO.getGpx() != null) {
            byte[] decodedBase64 = Base64.decode(postDTO.getGpx(), Base64.DEFAULT);
            String gpxXML = new String(decodedBase64);
            Log.i("GPX", gpxXML);
            this.Gpx = GPX.reader().fromString(gpxXML);
        }

    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Bitmap getPics() {
        return Pics;
    }

    public void setPics(Bitmap pics) {
        Pics = pics;
    }

    public GPX getGpx() {
        return Gpx;
    }

    public void setGpx(GPX gpx) {
        Gpx = gpx;
    }

    @Override
    public String toString() {
        return "Post{" +
                "Id=" + Id +
                ", Pics=" + Pics +
                ", Gpx=" + Gpx +
                '}';
    }

}
