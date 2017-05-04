package com.lzp.classroomassistant.net;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WangChunhe on 2017/4/12.
 */

public class HttpManager {
    private HttpManager(){};
    private static class SingletonHolder{
        private static HttpManager instance = new HttpManager();
    }
    public static HttpManager getInstance(){
        return SingletonHolder.instance;
    }


    public  static <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
