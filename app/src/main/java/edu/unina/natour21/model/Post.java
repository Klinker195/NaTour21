package edu.unina.natour21.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.sql.Timestamp;
import java.util.LinkedList;

import edu.unina.natour21.dto.PostDTO;
import io.jenetics.jpx.GPX;

public class Post {

    private Long id;
    private String title;
    private String description;
    private Timestamp timestamp;
    private Boolean accessibility;
    private Integer duration;
    private Integer difficulty;
    private GPX route;
    private LinkedList<Bitmap> pics;
    private Integer ups;
    private Integer downs;
    private Boolean isReported;
    private User author;

    public Post(PostDTO postDTO) {
        super();

        this.id = postDTO.getId();
        this.title = postDTO.getTitle();
        this.description = postDTO.getDescription();
        this.timestamp = Timestamp.valueOf(postDTO.getTimestamp());
        this.accessibility = postDTO.getAccessibility();
        this.duration = postDTO.getDuration();
        this.difficulty = postDTO.getDifficulty();
        this.ups = postDTO.getUps();
        this.downs = postDTO.getDowns();
        this.isReported = postDTO.getIsReported();
        this.author = new User(postDTO.getAuthor());

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

    public LinkedList<Bitmap> getPics() {
        return pics;
    }

    public void setPics(LinkedList<Bitmap> pics) {
        this.pics = pics;
    }

    public Integer getTotalUps() { return this.ups - this.downs; };

    public Integer getUps() {
        return ups;
    }

    public void setUps(Integer ups) {
        this.ups = ups;
    }

    public Integer getDowns() {
        return downs;
    }

    public void setDowns(Integer downs) {
        this.downs = downs;
    }

    public Boolean getReported() {
        return isReported;
    }

    public void setReported(Boolean reported) {
        isReported = reported;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
