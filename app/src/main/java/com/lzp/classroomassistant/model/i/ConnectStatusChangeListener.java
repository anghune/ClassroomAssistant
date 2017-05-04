package com.lzp.classroomassistant.model.i;

import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.v3.listener.BmobCallback1;

/**
 * Created by WangChunhe on 2017/4/30.
 */

public abstract class ConnectStatusChangeListener extends BmobCallback1<ConnectionStatus> {
    public ConnectStatusChangeListener() {
    }

    public abstract void onChange(ConnectionStatus var1);


    @Override
    public void done(ConnectionStatus connectionStatus) {
        this.onChange(connectionStatus);
    }
}
