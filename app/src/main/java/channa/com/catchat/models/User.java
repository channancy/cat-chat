package channa.com.catchat.models;

import org.json.JSONObject;

/**
 * Created by Nancy on 1/22/2017.
 */

public class User {
    private String mName;
    private String mEmail;
    private JSONObject mFriends;

    public User() {

    }

    public User(String name, String email, JSONObject friends) {
        this.mName = name;
        this.mEmail = email;
        this.mFriends = friends;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public JSONObject getFriends() {
        return mFriends;
    }

    public void setFriends(JSONObject friends) {
        mFriends = friends;
    }
}
