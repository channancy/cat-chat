package channa.com.catchat.models;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nancy on 1/22/2017.
 */

public class User {
    private String name;
    private String email;
    private JSONObject friendList;

    public User() {

    }

    public User(String name, String email, JSONObject friendList) {
        this.name = name;
        this.email = email;
        this.friendList = friendList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JSONObject getFriendList() {
        return friendList;
    }

    public void setFriendList(JSONObject friendList) {
        friendList = friendList;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("friendList", friendList);

        return result;
    }
}
