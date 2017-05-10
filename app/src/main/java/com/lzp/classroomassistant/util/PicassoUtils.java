package com.lzp.classroomassistant.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.lzp.classroomassistant.App;
import com.lzp.classroomassistant.R;
import com.squareup.picasso.Picasso;


/**
 * Created by toryang on 02/11/2016.
 */

public class PicassoUtils {

    private static Picasso picasso = App.getPicasso();
    private static Context context = App.getInstance();


    public static void loadImage(String url, ImageView imageView){
        loadImage(url,imageView, R.drawable.icon_head);
    }

    public static void loadImage(String url, ImageView imageView, int placeholder){

        picasso.with(context)
                .load(url)
                .placeholder(ContextCompat.getDrawable(context,placeholder))
                .into(imageView);
    }

    public static void loadImage(String url, ImageView imageView, int placeholder, int errorImage){
        picasso.with(context)
                .load(url)
                .placeholder(ContextCompat.getDrawable(context,placeholder))
                .error(ContextCompat.getDrawable(context,errorImage))
                .into(imageView);

    }

}
