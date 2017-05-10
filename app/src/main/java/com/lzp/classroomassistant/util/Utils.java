package com.lzp.classroomassistant.util;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.lzp.classroomassistant.data.User;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobUser;

/**
 * Created by wangchunhe on 16/6/12.
 */
public class Utils {

    private static final String TAG = "Utils";
    public static int getCourseBgColor(int colorFlag) {
        switch (colorFlag) {
            case 0:
                return Color.parseColor("#95e987");
            case 1:
                return Color.parseColor("#ffb67e");
            case 2:
                return Color.parseColor("#8cc7fe");
            case 3:
                return Color.parseColor("#7ba3eb");
            case 4:
                return Color.parseColor("#e3ade8");
            case 5:
                return Color.parseColor("#f9728b");
            case 6:
                return Color.parseColor("#85e9cd");
            case 7:
                return Color.parseColor("#f5a8cf");
            case 8:
                return Color.parseColor("#a9e2a0");
            case 9:
                return Color.parseColor("#70cec7");
            default:
                return Color.GRAY;
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 数字转换中文
     */
    public static String intToZH(int i) {
        String[] zh = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] unit = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十"};

        String str = "";
        StringBuffer sb = new StringBuffer(String.valueOf(i));
        sb = sb.reverse();
        int r = 0;
        int l = 0;
        for (int j = 0; j < sb.length(); j++) {
            r = Integer.valueOf(sb.substring(j, j + 1));
            if (j != 0)
                l = Integer.valueOf(sb.substring(j - 1, j));
            if (j == 0) {
                if (r != 0 || sb.length() == 1)
                    str = zh[r];
                continue;
            }
            if (j == 1 || j == 2 || j == 3 || j == 5 || j == 6 || j == 7 || j == 9) {
                if (r != 0)
                    str = zh[r] + unit[j] + str;
                else if (l != 0)
                    str = zh[r] + str;
                continue;
            }
            if (j == 4 || j == 8) {
                str = unit[j] + str;
                if ((l != 0 && r == 0) || r != 0)
                    str = zh[r] + str;
                continue;
            }
        }
        if (str.equals("七"))
            str = "日";
        return str;
    }


    public static String num2Letter(int i){
        String letter = "";
        switch (i){
            case 1:
                letter = "A";
                break;
            case 2:
                letter = "B";
                break;
            case 3:
                letter = "C";
                break;
            case 4:
                letter = "D";
                break;
            case 5:
                letter = "E";
                break;
            case 6:
                letter = "F";
                break;
            case 7:
                letter = "G";
                break;
            case 8:
                letter = "H";
                break;
            case 9:
                letter = "I";
                break;
            case 10:
                letter = "J";
                break;
            case 11:
                letter = "K";
                break;
            case 12:
                letter = "L";
                break;
            case 13:
                letter = "M";
                break;
            case 14:
                letter = "O";
                break;
            default:
                break;
        }
        return letter;
    }
    public static boolean hasCheckPermission(int section){
        Date date = new Date();
        String res;
        Calendar today = Calendar.getInstance();
        int m = today.get(Calendar.MONTH) + 1;
        String y = String.valueOf(today.get(Calendar.YEAR));
        int d = today.get(Calendar.DATE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        res = simpleDateFormat.format(date);
        Log.d("wang",   y+" 年 "+ m+ " 月 "+ d+" 日");

        if (section == 1){
            String start = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("07").append(":").append("50").toString();
            Log.d("wang","res "+ res  + " start "+ start + "   " + compareDate(start,res));
            String endStudent = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("08").append(":").append("00").toString();
            String endTeacher = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("09").append(":").append("40").toString();

            Log.d("wang","res "+ res  + " endStudent "+ endStudent + "   " + compareDate(res,endStudent));
            return isCheck(res,start,endStudent,endTeacher);
        }else if (section == 3){
            String start = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("09").append(":").append("40").toString();
            Log.d("wang","res "+ res  + " start "+ start + "   " + compareDate(start,res));
            String endStudent = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("10").append(":").append("05").toString();
            String endTeacher = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("11").append(":").append("45").toString();

            Log.d("wang","res "+ res  + " endStudent "+ endStudent + "   " + compareDate(res,endStudent));
            return isCheck(res,start,endStudent,endTeacher);
        } else if (section == 5){
            String start = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("13").append(":").append("50").toString();
            Log.d("wang","res "+ res  + " start "+ start + "   " + compareDate(start,res));
            String endStudent = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("14").append(":").append("00").toString();
            String endTeacher = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("15").append(":").append("40").toString();

            Log.d("wang","res "+ res  + " endStudent "+ endStudent + "   " + compareDate(res,endStudent));
            return isCheck(res,start,endStudent,endTeacher);
        } else if (section == 7){
            String start = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("15").append(":").append("40").toString();
            Log.d("wang","res "+ res  + " start "+ start + "   " + compareDate(start,res));
            String endStudent = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("16").append(":").append("05").toString();
            String endTeacher = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("17").append(":").append("45").toString();

            Log.d("wang","res "+ res  + " endStudent "+ endStudent + "   " + compareDate(res,endStudent));
            return  isCheck(res,start,endStudent,endTeacher);
        } else if (section == 9){
            String start = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("18").append(":").append("50").toString();
            Log.d("wang","res "+ res  + " start "+ start + "   " + compareDate(start,res));
            String endStudent = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("19").append(":").append("00").toString();
            String endTeacher = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("20").append(":").append("40").toString();

            Log.d("wang","res "+ res  + " endStudent "+ endStudent + "   " + compareDate(res,endStudent));
            return isCheck(res,start,endStudent,endTeacher);
        } else if (section == 11){
            String start = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("20").append(":").append("40").toString();
            Log.d("wang","res "+ res  + " start "+ start + "   " + compareDate(start,res));
            String endStudent = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("21").append(":").append("05").toString();
            String endTeacher = new StringBuilder(y).append("-").append(m).append("-").append(d).append(" ").append("22").append(":").append("45").toString();

            Log.d("wang","res "+ res  + " endStudent "+ endStudent + "   " + compareDate(res,endStudent));
            return isCheck(res,start,endStudent,endTeacher);
        }

        return false;
    }


    private static boolean isCheck(String now, String start, String endStudent, String endTeacher ){
        if (getCurrUserType()== Constant.STUDENT_TYPE  ){
            if ((compareDate(start, now) && compareDate(now, endStudent))){
                return true;
            } else {
                return false;
            }
        } else if (getCurrUserType() == Constant.TEACHER_TYPE ){
            if ((compareDate(start, now) && compareDate(now, endTeacher))){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }




    /**
     *  比较日期，形如 s：2016年11月12日 与 e：2016年11月13日，返回true,
     *   如果s比e早，则返回true,否则返回false
     * @param s
     * @param e
     * @return
     */

    public static boolean compareDate(String s,String e){
        boolean flag = false;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            java.util.Date sDay = simpleDateFormat.parse(s);
            java.util.Date eDay = simpleDateFormat.parse(e);
            if (sDay.before(eDay)) flag = true;

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return flag;
    }

    public static int getCurrUserType(){
        User user =  BmobUser.getCurrentUser(User.class);
        return user.getType();
    }

    public static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static String[] getPath(Context context, Uri uri) {
            String[] info = new String[3];
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data",OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                int nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE);
                if (cursor.moveToFirst()) {
                    info[0] = cursor.getString(column_index);
                    info[1] = cursor.getString(nameIndex);
                    info[2] = FormentFileSize(cursor.getLong(sizeIndex));
                    Log.i(TAG," name " + cursor.getString(nameIndex) + " size  "+ cursor.getString(sizeIndex));
                    return info;
                }
            } catch (Exception e) {
                // Eat it
            }
        }
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
        return null;
    }


    public static String getFilePath(Context context, Uri uri){
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {

                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 转换文件大小
     * */
    public static String FormentFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static void followScreenOrientation(Context context, Camera camera){
        final int orientation = context.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(270);
        }
    }

}
