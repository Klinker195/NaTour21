package edu.unina.natour21.dto;

import java.io.Serializable;
import java.util.LinkedList;

public class FavCollectionDTO implements Serializable {

    private static final long serialVersionUID = 1739407699313667547L;

    private Long id;
    private UserDTO author;
    private String title;
    private LinkedList<PostDTO> posts;

    public FavCollectionDTO() {
        super();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LinkedList<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(LinkedList<PostDTO> posts) {
        this.posts = posts;
    }

}
