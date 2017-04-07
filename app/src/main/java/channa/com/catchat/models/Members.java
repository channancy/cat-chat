package channa.com.catchat.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nancy on 4/6/2017.
 */

public class Members {

    private List<String> memberIDList;

    public Members() {

    }

    public Members(List<String> memberIDList) {
        this.memberIDList = memberIDList;
    }

    public List<String> getMemberIDList() {
        return memberIDList;
    }

    public void setMemberIDList(List<String> memberIDList) {
        this.memberIDList = memberIDList;
    }

    public Map<String, Object> toMap(String key) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("memberIDList", memberIDList);

        return result;
    }
}
