package edu.unina.natour21;

public class PostDTO {

    private Long idPost;
    private String pics;
    private String gpx;

    public PostDTO() {
        super();
    }

    public Long getId() {
        return idPost;
    }

    public void setId(Long id) {
        idPost = id;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        pics = pics;
    }

    public String getGpx() {
        return gpx;
    }

    public void setGpx(String gpx) {
        gpx = gpx;
    }

    @Override
    public String toString() {
        return "Post{" +
                "Id=" + idPost +
                ", Pics=" + pics +
                ", Gpx=" + gpx +
                '}';
    }

}
