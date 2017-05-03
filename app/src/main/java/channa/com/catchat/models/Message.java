package channa.com.catchat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by Nancy on 4/4/2017.
 * http://stackoverflow.com/questions/33096128/when-making-a-pojo-in-firebase-can-you-use-servervalue-timestamp?lq=1
 */

public class Message {

    private String name;
    private String text;
    private String photoUrl;
    private String userID;
    private HashMap<String, Object> dateCreated;
    private HashMap<String, Object> dateLastChanged;

    public Message() {

    }

    public Message(String name, String text, String photoUrl, String userID, HashMap<String,Object> dateCreated) {
        this.name = name;
        this.text = text;
        this.photoUrl = photoUrl;
        this.userID = userID;
        this.dateCreated = dateCreated;

        // Date last changed will always be set to ServerValue.TIMESTAMP
        HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
        dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
        this.dateLastChanged = dateLastChangedObj;
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

    public HashMap<String, Object> getDateLastChanged() {
        return dateLastChanged;
    }

    public HashMap<String, Object> getDateCreated() {
        // If there is a dateCreated object already, then return that
        if (dateCreated != null) {
            return dateCreated;
        }
        // Otherwise make a new object set to ServerValue.TIMESTAMP
        HashMap<String, Object> dateCreatedObj = new HashMap<String, Object>();
        dateCreatedObj.put("date", ServerValue.TIMESTAMP);
        return dateCreatedObj;
    }

    // Use the method described in http://stackoverflow.com/questions/25500138/android-chat-crashes-on-datasnapshot-getvalue-for-timestamp/25512747#25512747
    // to get the long values from the date object.
    @Exclude
    public long getDateLastChangedLong() {

        return (long)dateLastChanged.get("date");
    }

    @Exclude
    public long getDateCreatedLong() {
        return (long)dateCreated.get("date");
    }
}
