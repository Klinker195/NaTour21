package edu.unina.natour21.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.sql.Date;
import java.util.LinkedList;

import edu.unina.natour21.dto.PostDTO;
import edu.unina.natour21.dto.UserDTO;
import io.jenetics.jpx.GPX;

public class User {

    private String nickname;
    private Bitmap propic;
    private String email;
    private Boolean isAdmin;
    private Integer sex;
    private Date birthdate;
    private Integer height;
    private Float weight;
    private LinkedList<Post> posts;
    private LinkedList<User> followedUsers;

    public User(UserDTO userDTO) {
        this.nickname = userDTO.getNickname();
        this.email = userDTO.getEmail();
        this.isAdmin = userDTO.getIsAdmin();
        this.sex = userDTO.getSex();
        this.birthdate = Date.valueOf(userDTO.getBirthdate());
        this.height = userDTO.getHeight();
        this.weight = userDTO.getWeight();

        this.posts = new LinkedList<>();

        if(userDTO.getPosts() != null) {
            for(PostDTO tmpPostDTO : userDTO.getPosts()) {
                this.posts.add(new Post(tmpPostDTO));
            }
        }

        this.followedUsers = new LinkedList<>();

        if(userDTO.getFollowedUsers() != null) {
            for(UserDTO tmpUserDTO : userDTO.getFollowedUsers()) {
                this.followedUsers.add(new User(tmpUserDTO));
            }
        }

        if(userDTO.getPropic() != null) {
            byte[] decodedBase64 = Base64.decode(userDTO.getPropic(), Base64.DEFAULT);
            this.propic = BitmapFactory.decodeByteArray(decodedBase64, 0, decodedBase64.length);
        } else {
            // TODO: Set standard pic or fix in view
        }

    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Bitmap getPropic() {
        return propic;
    }

    public void setPropic(Bitmap propic) {
        this.propic = propic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
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

    public LinkedList<Post> getPosts() {
        return posts;
    }

    public void setPosts(LinkedList<Post> posts) {
        this.posts = posts;
    }

    public LinkedList<User> getFollowedUsers() {
        return followedUsers;
    }

    public void setFollowedUsers(LinkedList<User> followedUsers) {
        this.followedUsers = followedUsers;
    }

}
