package channa.com.catchat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by Nancy on 4/4/2017.
 * http://stackoverflow.com/questions/36658833/firebase-servervalue-timestamp-in-java-data-models-objects
 */

public class Message {

    private String avatarUrl;
    private String name;
    private String text;
    private String photoUrl;
    private String userID;
    private HashMap<String, Object> timestampCreated;

    public Message() {

    }

    public Message(String avatarUrl, String name, String text, String photoUrl, String userID) {
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.text = text;
        this.photoUrl = photoUrl;
        this.userID = userID;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
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


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long) timestampCreated.get("timestamp");
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
}
