package com.lzp.classroomassistant.util;

import android.app.Activity;
import android.os.Process;

import java.util.ArrayList;

/**
 * Created by WangChunhe on 2017/2/15.
 */

public class ActivityManager {

    private ArrayList<Activity> mActivities = new ArrayList<>();

    private ActivityManager(){};
    private static class SingletonHolder{
        private static ActivityManager instance = new ActivityManager();
    }
    public static ActivityManager getInstance(){
        return SingletonHolder.instance;
    }

    public void addActivity(Activity act){
        mActivities.add(act);
    }

    public void removeActivity(Activity act){
        if (mActivities.contains(act)){
            mActivities.remove(act);
        }
    }

    public void exitApp(){
        for (Activity activity : mActivities){
            activity.finish();
        }
        Process.killProcess(Process.myPid());
    }




}
