package edu.unina.natour21.models;

import edu.unina.natour21.dto.ReportDTO;

public class Report {

    private Long id;
    private Post post;
    private String title;
    private String description;

    public Report(ReportDTO reportDTO) {
        this.id = reportDTO.getId();
        this.post = new Post(reportDTO.getPost());
        this.title = reportDTO.getTitle();
        this.description = reportDTO.getDescription();
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

}
