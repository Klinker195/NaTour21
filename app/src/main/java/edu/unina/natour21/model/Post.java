package edu.unina.natour21.model;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.LinkedList;

import edu.unina.natour21.dto.PostDTO;
import edu.unina.natour21.dto.ReviewDTO;
import io.jenetics.jpx.GPX;

public class Post implements Parcelable {

    private Long id;
    private String title;
    private String description;
    private Timestamp timestamp;
    private Boolean accessibility;
    private Integer duration;
    private Integer difficulty;
    private GPX route;
    private Double startLat;
    private Double startLng;
    private LinkedList<Bitmap> pics;
    private Boolean isReported;
    private Float rate;
    private User author;
    // private LinkedList<Review> reviews;

    public Post(PostDTO postDTO) {
        super();

        this.id = postDTO.getId();
        this.title = postDTO.getTitle();
        this.description = postDTO.getDescription();
        this.timestamp = Timestamp.valueOf(postDTO.getTimestamp());
        this.accessibility = postDTO.getAccessibility();
        this.duration = postDTO.getDuration();
        this.difficulty = postDTO.getDifficulty();
        this.startLat = postDTO.getStartLat();
        this.startLng = postDTO.getStartLng();
        this.isReported = postDTO.getIsReported();
        this.rate = postDTO.getRate();
        this.author = new User(postDTO.getAuthor());

        /*
        LinkedList<Review> reviewList = new LinkedList<Review>();

        for(ReviewDTO reviewDTO : postDTO.getReviews()) {
            reviewList.add(new Review(reviewDTO));
        }

        this.reviews = reviewList;
        */

        this.pics = new LinkedList<>();

        if(postDTO.getPics() != null) {
            for(String base64String : postDTO.getPics()) {
                byte[] decodedBase64 = Base64.decode(base64String, Base64.DEFAULT);
                this.pics.add(BitmapFactory.decodeByteArray(decodedBase64, 0, decodedBase64.length));
            }
        } else {
            // TODO: Set standard pic or fix in view
        }

        if(postDTO.getRoute() != null) {
            byte[] decodedBase64 = Base64.decode(postDTO.getRoute(), Base64.DEFAULT);
            String gpxXML = new String(decodedBase64);
            this.route = GPX.reader().fromString(gpxXML);
        } else {
            // TODO: Set standard GPX or fix in view
        }

    }

    protected Post(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.description = in.readString();
        this.timestamp = Timestamp.valueOf(in.readString());
        if(in.readByte() == 0) {
            this.accessibility = false;
        } else {
            this.accessibility = true;
        }
        this.duration = in.readInt();
        this.difficulty = in.readInt();
        this.route = (GPX) in.readSerializable();
        this.startLat = in.readDouble();
        this.startLng = in.readDouble();

        // in.readList(pics);

        if(in.readByte() == 0) {
            this.isReported = false;
        } else {
            this.isReported = true;
        }
        this.rate = in.readFloat();
        this.author = (User) in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Boolean accessibility) {
        this.accessibility = accessibility;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public GPX getRoute() {
        return route;
    }

    public void setRoute(GPX route) {
        this.route = route;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLng() {
        return startLng;
    }

    public void setStartLng(Double startLng) {
        this.startLng = startLng;
    }

    public LinkedList<Bitmap> getPics() {
        return pics;
    }

    public void setPics(LinkedList<Bitmap> pics) {
        this.pics = pics;
    }

    public Boolean getReported() {
        return isReported;
    }

    public void setReported(Boolean reported) {
        isReported = reported;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO: FIX BOOLEAN VALUES
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(timestamp.toString());
        if(accessibility == null || accessibility == false) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
        }
        dest.writeInt(duration);
        dest.writeInt(difficulty);
        dest.writeSerializable(route);
        dest.writeDouble(startLat);
        dest.writeDouble(startLng);

        // dest.writeList(pics);

        if(isReported == null || isReported == false) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
        }
        dest.writeFloat(rate);
        dest.writeParcelable(author, flags);
    }

    /*
    public LinkedList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(LinkedList<Review> reviews) {
        this.reviews = reviews;
    }
    */

}
