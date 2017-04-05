package channa.com.catchat.models;

/**
 * Created by Nancy on 4/4/2017.
 */

public class Message {

    public static final int INCOMING = 0;
    public static final int OUTGOING = 1;

    private String avatarUrl;
    private String name;
    private String text;
    private String photoUrl;
    private int type;

    public Message() {

    }

    public Message(String avatarUrl, String name, String text, String photoUrl, int type) {
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.text = text;
        this.photoUrl = photoUrl;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
