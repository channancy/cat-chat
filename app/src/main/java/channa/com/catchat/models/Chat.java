package channa.com.catchat.models;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Nancy on 4/26/2017.
 */

public class Chat implements Comparable<Chat> {

    private String avatarUrl;
    private String title;
    private String lastMessage;
    private HashMap<String, Object> dateCreated;
    private HashMap<String, Object> dateLastChanged;

    public Chat() {

    }

    public Chat(String avatarUrl, String title, String lastMessage, HashMap<String,Object> dateCreated) {
        this.avatarUrl = avatarUrl;
        this.title = title;
        this.lastMessage = lastMessage;
        this.dateCreated = dateCreated;

        // Date last changed will always be set to ServerValue.TIMESTAMP
        HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
        dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
        this.dateLastChanged = dateLastChangedObj;
    }


    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public int compareTo(Chat chat) {
        Log.d(TAG, "compareTo: ");

        if (getDateCreatedLong() == chat.getDateCreatedLong()) {
            return 0;
        }
        else if (getDateCreatedLong() > chat.getDateCreatedLong()) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
