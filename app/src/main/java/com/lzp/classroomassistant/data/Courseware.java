package com.lzp.classroomassistant.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by WangChunhe on 2017/5/4.
 */

public class Courseware extends BmobObject {
    private String name;
    private String size;
    private String path;
    private User author;

    public Courseware(String name, String size, String path, User author) {
        this.name = name;
        this.size = size;
        this.path = path;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
