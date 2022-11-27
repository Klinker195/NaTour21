package edu.unina.natour21.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.sql.Date;
import java.util.LinkedList;

import edu.unina.natour21.dto.PostDTO;
import edu.unina.natour21.dto.UserDTO;

public class User implements Parcelable {

    private final String email;
    // private String uuid;
    private String name;
    private String surname;
    private String nickname;
    private Bitmap propic;
    private Boolean isAdmin;
    private Integer sex;
    private Date birthdate;
    private Integer height;
    private Float weight;
    private LinkedList<Post> posts;
    private LinkedList<User> followedUsers;

    public User(UserDTO userDTO) {
        this.nickname = userDTO.getNickname();
        this.name = userDTO.getName();
        this.surname = userDTO.getSurname();
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

    // public String getUuid() { return uuid; }

    protected User(Parcel in) {
        this.email = in.readString();
        this.name = in.readString();
        this.surname = in.readString();
        this.nickname = in.readString();
        // this.propic = (Bitmap) in.readParcelable(Bitmap.class.getClassLoader());
        if(in.readByte() == 0) {
            this.isAdmin = false;
        } else {
            this.isAdmin = true;
        }
        this.sex = in.readInt();
        this.height = in.readInt();
        this.weight = in.readFloat();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    /*
    public void setEmail(String email) {
        this.email = email;
    }
    */

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(nickname);
        // dest.writeParcelable(propic, flags);
        if(isAdmin == null || isAdmin == false) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
        }
        dest.writeInt(sex);
        dest.writeInt(height);
        dest.writeFloat(weight);
    }
}
