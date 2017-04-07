package channa.com.catchat.models;

import java.util.Map;

/**
 * Created by Nancy on 4/6/2017.
 */

public class Members {

    private Map<String, Boolean> memberIDList;

    public Members() {

    }

    public Members(Map<String, Boolean> memberIDList) {
        this.memberIDList = memberIDList;
    }

    public Map<String, Boolean> getMemberIDList() {
        return memberIDList;
    }

    public void setMemberIDList(Map<String, Boolean> memberIDList) {
        this.memberIDList = memberIDList;
    }
}
