package com.lzp.classroomassistant.model.i;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;

/**
 * @author :smile
 * @project:UpdateCacheListener
 * @date :2016-02-01-16:23
 */
public abstract class UpdateCacheListener extends CloudCodeListener {
    public abstract void done(BmobException e);

    @Override
    public void done(Object o, BmobException e) {
        done(e);
    }
}
