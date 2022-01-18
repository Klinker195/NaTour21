package edu.unina.natour21.dto;

import java.io.Serializable;
import java.util.LinkedList;

public class UserDTO implements Serializable {

    private static final long serialVersionUID = -1552640971990839394L;

    private String nickname;
    private String propic;
    private String email;
    private Boolean isAdmin;
    private Integer sex;
    private String birthdate;
    private Integer height;
    private Float weight;
    private LinkedList<PostDTO> posts;
    private LinkedList<UserDTO> followedUsers;

    public UserDTO() {
        super();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public LinkedList<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(LinkedList<PostDTO> posts) {
        this.posts = posts;
    }

    public LinkedList<UserDTO> getFollowedUsers() {
        return followedUsers;
    }

    public void setFollowedUsers(LinkedList<UserDTO> followedUsers) {
        this.followedUsers = followedUsers;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

}
