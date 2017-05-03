package channa.com.catchat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by Nancy on 4/26/2017.
 */

public class Chat {

    private String chatID;
    private String userID;
    private String name;
    private String lastMessage;
    private HashMap<String, Object> dateCreated;
    private HashMap<String, Object> dateLastChanged;

    public Chat() {

    }

    public Chat(String chatID, String userID, String name, String lastMessage, HashMap<String,Object> dateCreated) {
        this.chatID = chatID;
        this.userID = userID;
        this.name = name;
        this.lastMessage = lastMessage;
        this.dateCreated = dateCreated;

        // Date last changed will always be set to ServerValue.TIMESTAMP
        HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
        dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
        this.dateLastChanged = dateLastChangedObj;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
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
