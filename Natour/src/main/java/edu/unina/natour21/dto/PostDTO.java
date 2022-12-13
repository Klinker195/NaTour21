package edu.unina.natour21.dto;

import java.io.Serializable;
import java.util.LinkedList;

import edu.unina.natour21.model.Post;

public class PostDTO implements Serializable {

    private static final long serialVersionUID = -3342309476806257604L;

    private Long id;
    private String title;
    private String description;
    private String timestamp;
    private String editTimestamp;
    private Boolean accessibility;
    private Integer duration;
    private Integer difficulty;
    private String route;
    private Double startLat;
    private Double startLng;
    private LinkedList<String> pics;
    private Boolean isReported;
    private Float rate;
    private UserDTO author;

    public PostDTO() {
        super();
    }

    public PostDTO(Post post) {
        super();
        this.id = post.getId();
        this.title = post.getTitle();
        if (post.getDescription() != null) this.description = post.getDescription();
        this.timestamp = post.getTimestamp().toString();
        if (post.getEditTimestamp() != null)
            this.editTimestamp = post.getEditTimestamp().toString();
        this.accessibility = post.getAccessibility();
        this.duration = post.getDuration();
        this.difficulty = post.getDifficulty();
        // this.route = post.getRoute();
        this.startLat = post.getStartLat();
        this.startLng = post.getStartLng();
        // this.pics = post.getPics();
        this.isReported = post.getReported();
        this.rate = post.getRate();
        // this.author = post.getAuthor();
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

    public String getEditTimestamp() {
        return editTimestamp;
    }

    public void setEditTimestamp(String editTimestamp) {
        this.editTimestamp = editTimestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
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

    public LinkedList<String> getPics() {
        return pics;
    }

    public void setPics(LinkedList<String> pics) {
        this.pics = pics;
    }

    public Boolean getIsReported() {
        return isReported;
    }

    public void setIsReported(Boolean isReported) {
        this.isReported = isReported;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
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

    /*
    public LinkedList<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(LinkedList<ReviewDTO> reviews) {
        this.reviews = reviews;
    }
    */

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

}
