package edu.unina.natour21.model;

import edu.unina.natour21.dto.ReviewDTO;

public class Review {

    private Long id;
    private Post post;
    private User author;
    private Float rate;
    private String description;

    public Review(ReviewDTO reviewDTO) {
        this.id = reviewDTO.getId();
        this.post = new Post(reviewDTO.getPost());
        this.author = new User(reviewDTO.getAuthor());
        this.rate = reviewDTO.getRate();
        this.description = reviewDTO.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
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
