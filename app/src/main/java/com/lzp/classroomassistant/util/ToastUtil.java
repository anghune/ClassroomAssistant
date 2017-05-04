package com.lzp.classroomassistant.util;

import android.widget.Toast;

import com.lzp.classroomassistant.App;

/**
 * Created by toryang on 09/11/2016.
 */

public class ToastUtil {
    static Toast sToast;
    public static final int SUCCESS = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    public static final int INFO = 4;
    public static final int DEFAULT = 5;

    public static void showToast( String text) {
        if (sToast == null){
            sToast = Toast.makeText(App.getInstance(), text, Toast.LENGTH_SHORT);
            sToast.show();
        } else {
            sToast.setText(text);
            sToast.setDuration(Toast.LENGTH_SHORT);
            sToast.show();
        }

    }



}
