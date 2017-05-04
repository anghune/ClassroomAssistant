package com.lzp.classroomassistant.net;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.lzp.classroomassistant.App;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.util.ToastUtil;
import com.lzp.classroomassistant.view.WaitUIElement;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by toryang on 07/11/2016.
 */

public class ProgressSubscriber<T> extends Subscriber<T> implements DialogInterface.OnCancelListener{

    private SubscriberOnNextListener mSubscriberOnNextListener;
    private WaitUIElement waitUIElement;

    private boolean isShowDialog = true;

    public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Context context) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        waitUIElement = new WaitUIElement(context);
    }

    public ProgressSubscriber(SubscriberOnNextListener subscriberOnNextListener) {
        isShowDialog = false;
        this.mSubscriberOnNextListener  = subscriberOnNextListener;
    }

    private void showProgressDialog(String msg) {
        if (isShowDialog){
            waitUIElement.showProcessDialog(msg, this);
        }
    }

    private void showProgressDialog(){
        showProgressDialog("努力加载中...");
    }

    private void dismissProgressDialog(){
        if (isShowDialog){
            if (waitUIElement.isShowingProgress())
                waitUIElement.dismissProcessDialog();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgressDialog();
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
        Log.d(" wang ","   onCompleted ");
    }

    @Override
    public void onError(Throwable e) {
        dismissProgressDialog();
        Log.d(" wang ","   onError ");
        if (e instanceof SocketTimeoutException) {
            ToastUtil.showToast(App.getInstance().getString(R.string.network_break));
        } else if (e instanceof ConnectException) {
            ToastUtil.showToast(App.getInstance().getString(R.string.network_break));
        } else {
            if (e.getMessage().equals("用户登录状态异常"))
                gotoLogin();
        }
    }

    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
            Log.d(" wang ","   onNext ");
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    public void gotoLogin(){

    }

}
