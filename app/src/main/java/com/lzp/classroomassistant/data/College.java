package com.lzp.classroomassistant.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by WangChunhe on 2017/4/14.
 */

public class College extends BmobObject {
    private Integer id;
    private String Name;
    private School school;

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

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}
