package channa.com.catchat.models;

/**
 * Created by Nancy on 4/26/2017.
 */

public class Chat {

    private String avatarUrl;
    private String title;
    private String lastMessage;
    private String lastTimestamp;

    public Chat() {

    }

    public Chat(String avatarUrl, String title, String lastMessage, String lastTimestamp) {
        this.avatarUrl = avatarUrl;
        this.title = title;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
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

    public String getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(String lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
}
