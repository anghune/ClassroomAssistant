package com.lzp.classroomassistant;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lzp.classroomassistant.util.ActivityManager;

import butterknife.ButterKnife;

/**
 * Created by WangChunhe on 2017/2/15.
 */

public abstract class BaseActivity extends AppCompatActivity{
    View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getLayoutView();
        rootView = LayoutInflater.from(this).inflate(layout,null);
        setContentView(rootView);
        ButterKnife.inject(this);
        initViews(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);



    }

    protected abstract int getLayoutView();

    protected abstract void initViews(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            View view = getCurrentFocus();
            if (isShouldHidekeyboard(view, ev)){
                hideKeyboard(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isShouldHidekeyboard(View v, MotionEvent event){
        if (v != null && (v instanceof EditText)) {
            int[] l = {0,0};
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom){
                //点击EditText的事件，忽略它
                return false;
            }else {
                return true;
            }

        }
        return false;
    }

    public void hideKeyboard(IBinder token){
        if (token != null){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromInputMethod(token,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }





}
