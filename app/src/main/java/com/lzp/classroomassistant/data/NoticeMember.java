package com.lzp.classroomassistant.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by WangChunhe on 2017/5/2.
 */

public class NoticeMember extends BmobObject {
    private Notice notice;
    private User member;
    private boolean isConfirm;
    private int mCount;

    public NoticeMember(Notice notice, User member, boolean isConfirm) {
        this.notice = notice;
        this.member = member;
        this.isConfirm = isConfirm;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
    }

    public boolean isConfirm() {
        return isConfirm;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }
}
