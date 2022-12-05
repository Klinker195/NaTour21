package edu.unina.natour21.dto;

import java.io.Serializable;

import edu.unina.natour21.model.Post;
import edu.unina.natour21.model.Report;

public class ReportDTO implements Serializable {

    private static final long serialVersionUID = -3952663520058874494L;

    private Long id;
    private PostDTO post;
    private String title;
    private String description;

    public ReportDTO() {
        super();
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

}
