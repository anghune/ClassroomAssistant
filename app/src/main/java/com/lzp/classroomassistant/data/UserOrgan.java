package com.lzp.classroomassistant.data;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by WangChunhe on 2017/4/24.
 */

public class UserOrgan extends BmobObject {
    private User userName;
    private BmobRelation organiza;

    public User getUserName() {
        return userName;
    }

    public void setUserName(User userName) {
        this.userName = userName;
    }

    public BmobRelation getOrganiza() {
        return organiza;
    }

    public void setOrganiza(BmobRelation organiza) {
        this.organiza = organiza;
    }
}
