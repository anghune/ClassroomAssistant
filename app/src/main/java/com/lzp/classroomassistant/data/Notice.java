package com.lzp.classroomassistant.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by WangChunhe on 2017/5/1.
 */

public class Notice extends BmobObject {
    private String noticeId;
    private String content;
    private User author;
    private Integer count;


    public Notice(String noticeId, String content, User author,Integer count) {
        this.noticeId = noticeId;
        this.content = content;
        this.author = author;
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }


}
