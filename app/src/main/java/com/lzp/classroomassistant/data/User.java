package com.lzp.classroomassistant.data;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by WangChunhe on 2017/4/15.
 *
 * 为了表示唯一性采用学号当成BmobUser的username
 * name则表示姓名
 */

public class User extends BmobUser  {

    private String school;
    private String college;
    private String name;
    private Integer type; // 用户类型 1表示学生 ， 2 表示教师
    private BmobRelation courses;
    private String organ; //用于将用户进行分类
    private String avatar;
    private int result;// 表示人脸识别结果状态码 1：表示验证失败  2：表示验证成功


    public User() {
    }


    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public User(String school, String college, String name, Integer type) {
        this.school = school;
        this.college = college;
        this.name = name;

        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return com.google.common.base.Objects.equal(super.getUsername(), user.getUsername())
                && com.google.common.base.Objects.equal(name,user.getName());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(name,super.getUsername());
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BmobRelation getCourses() {
        return courses;
    }

    public void setCourses(BmobRelation courses) {
        this.courses = courses;
    }



}
