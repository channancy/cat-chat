package channa.com.catchat.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nancy on 1/22/2017.
 */

public class User implements Comparable<User> {
    private String id;
    private String name;
    private String email;
    private String avatarUrl;

    public User() {

    }

    public User(String id, String name, String email, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("email", email);
        result.put("avatarUrl", avatarUrl);

        return result;
    }

    @Override
    public int compareTo(User user) {
        return name.compareTo(user.name);
    }
}
