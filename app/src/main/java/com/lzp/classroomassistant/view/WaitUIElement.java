package com.lzp.classroomassistant.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.lzp.classroomassistant.R;

import java.lang.ref.WeakReference;


/**
 * Created by qhung on 16/9/26.
 */

public class WaitUIElement {
    private WeakReference<Activity> context = null;
    private NetLoadingDialog progressDialog = null;

    public WaitUIElement(WeakReference<Activity> context) {
        this.context = context;
    }

    public WaitUIElement(Context context){
        this.context = new WeakReference<Activity>((Activity) context);
    }

    public void showProcessDialog(String messageStr, DialogInterface.OnCancelListener listener) {
        if (progressDialog != null && progressDialog.isShowing()) return;
        if (progressDialog == null && getContext() != null) {

            progressDialog = new NetLoadingDialog(getContext()).createDialog(getContext(), 1,
                    TextUtils.isEmpty(messageStr) ? context.get().getString(R.string.loading) : messageStr);
            progressDialog.setOnCancelListener(listener);
            progressDialog.show();
        }
    }


    public void dismissProcessDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public boolean isShowingProgress() {
        return progressDialog != null && progressDialog.isShowing();
    }

    public Context getContext() {
        return context.get();
    }
}


