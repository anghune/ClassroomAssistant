package com.lzp.classroomassistant;

import android.app.Application;
import android.os.StrictMode;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechUtility;
import com.lzp.classroomassistant.net.UniversalImageLoader;
import com.lzp.classroomassistant.util.Okhttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import okhttp3.OkHttpClient;

/**
 * Created by WangChunhe on 2017/4/15.
 */

public class App extends Application {

    private static App instance;
    public static Picasso mPicasso;

    public static App getInstance() {
        return instance;
    }
    public static Picasso getPicasso() {
        return mPicasso;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(App.this,  "appid=590d4be1");
        Setting.setShowLog(false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        instance = this;
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
        }
        //uil初始化
        UniversalImageLoader.initImageLoader(this);

    }

    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void setUpPicasso() {
        Picasso picasso = new Picasso.Builder(this)
                .downloader(new Okhttp3Downloader((new OkHttpClient())))
                .build();
        Picasso.setSingletonInstance(picasso);
        mPicasso = picasso;
    }
}
