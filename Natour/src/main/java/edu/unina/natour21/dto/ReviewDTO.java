package edu.unina.natour21.dto;

import java.io.Serializable;

public class ReviewDTO implements Serializable {

    private static final long serialVersionUID = -938161862068275037L;

    private Long id;
    private PostDTO post;
    private UserDTO author;
    private Float rate;
    private String description;

    public ReviewDTO() { super(); }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PostDTO getPost() {
        return post;
    }

    public void setPost(PostDTO post) {
        this.post = post;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
