package com.lzp.classroomassistant.model.i;


import com.lzp.classroomassistant.data.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;

/**
 * @author :smile
 * @project:QueryUserListener
 * @date :2016-02-01-16:23
 */
public abstract class QueryUserListener extends CloudCodeListener {

    public abstract void done(User s, BmobException e);

    @Override
    public void done(Object o, BmobException e) {

    }
}
