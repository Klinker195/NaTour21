package edu.unina.natour21.model;

import java.util.LinkedList;

import edu.unina.natour21.dto.FavCollectionDTO;
import edu.unina.natour21.dto.PostDTO;

public class FavCollection {

    private Long id;
    private User author;
    private String title;
    private LinkedList<Post> posts;

    public FavCollection(FavCollectionDTO favCollectionDTO) {
        super();

        this.id = favCollectionDTO.getId();
        this.author = new User(favCollectionDTO.getAuthor());
        this.title = favCollectionDTO.getTitle();

        posts = new LinkedList<Post>();

        for(PostDTO postDTO : favCollectionDTO.getPosts()) {
            posts.add(new Post(postDTO));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LinkedList<Post> getPosts() {
        return posts;
    }

    public void setPosts(LinkedList<Post> posts) {
        this.posts = posts;
    }

}
