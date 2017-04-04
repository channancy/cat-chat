package channa.com.catchat.models;

/**
 * Created by Nancy on 4/4/2017.
 */

public class Message {

    private String avatarUrl;
    private String name;
    private String text;
    private String photoUrl;

    public Message() {

    }

    public Message(String avatarUrl, String name, String text, String photoUrl) {
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.text = text;
        this.photoUrl = photoUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
