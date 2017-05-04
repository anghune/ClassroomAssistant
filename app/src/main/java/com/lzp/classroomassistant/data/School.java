package com.lzp.classroomassistant.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by WangChunhe on 2017/4/14.
 */

public class School extends BmobObject {
    private Integer id;
    private String Name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }
}
