package com.lzp.classroomassistant.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Created by WangChunhe on 2016/12/11.
 */

public class AlertDialogUtil {
     private   static AlertDialog sDialog;

   public static void showDialog(Context context, View view, String title, final AlertDialogListener listener){
          sDialog  = new AlertDialog.Builder(context)
                 .setTitle(title)
                  .setView(view)
                  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                        listener.sureOnClick();
                     }
                  })
                  .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                     }
                  })
                 .create();
      sDialog.show();

   }
    public static void showDialog(Context context,  String title, final AlertDialogListener listener){
        showDialog(context,null,title,listener);

    }

   public interface AlertDialogListener{
      void sureOnClick();


   }


}
