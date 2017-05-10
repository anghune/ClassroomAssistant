package com.lzp.classroomassistant.util;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

/**
 * Created by WangChunhe on 2017/5/8.
 */

public class PopupMenuUtil {

    public static void showPopupMenu(int layRes, Context context, View topView, final ItemClickListener itemClickListener){
        PopupMenu popupMenu = new PopupMenu(context,topView);
        popupMenu.getMenuInflater()
                .inflate(layRes,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return itemClickListener.onMenuItemClick(item);
            }
        });
        popupMenu.show();
    }

    public interface ItemClickListener{
        boolean onMenuItemClick(MenuItem item);
    }
}
