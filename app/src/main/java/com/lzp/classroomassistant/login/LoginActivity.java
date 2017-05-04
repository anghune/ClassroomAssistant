package com.lzp.classroomassistant.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.mainpages.MainActivity;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.ToastUtil;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.schoolTxtId)
    TextView mSelectSchoolTxt;
    @InjectView(R.id.edit_username)
    EditText mUserNameEdit;
    @InjectView(R.id.edit_pass)
    EditText mPassEdit;
    @InjectView(R.id.loginBtnId)
    Button mLoginBtn;
    @InjectView(R.id.registerTxtId)
    TextView mRegisterTxt;

    @OnClick({R.id.registerTxtId, R.id.schoolTxtId,R.id.loginBtnId})
    void onClick(View view){
        switch (view.getId()){
            case R.id.registerTxtId:{
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

                 }
            break;
            case R.id.schoolTxtId: {
                Intent intent = new Intent(LoginActivity.this,SchoolActivity.class);
                intent.putExtra(Constant.KEY,Constant.TYPE_SCHOOL_LOGIN);
                startActivityForResult(intent,Constant.ACTIVITY_REQUEST_CODE);
                Log.d(" wang ","  onClick ");

                }
            break;
            case R.id.loginBtnId:
                login();
                break;
            default:
                break;
        }
    }
    @OnTouch({R.id.edit_username})
    boolean onTouch(View view, MotionEvent motionEvent){
        switch (view.getId()){
            case R.id.edit_username:
                clearEditText(mUserNameEdit,motionEvent);
                break;
        }
        return false;
    }
    @Override
    protected int getLayoutView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

//        addListener();

    }



    private void addListener(){
        mSelectSchoolTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SchoolActivity.class);
                intent.putExtra(Constant.KEY,Constant.TYPE_SCHOOL_LOGIN);
                startActivityForResult(intent,Constant.ACTIVITY_REQUEST_CODE);
            }
        });
        mRegisterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(){
        if (mSelectSchoolTxt.getText().toString().equals("学校")||
                mUserNameEdit.getText().toString().isEmpty()|| mPassEdit.getText().toString().isEmpty()){
            ToastUtil.showToast("请填写完整信息!");
        } else {
            User user = new User();
            user.setPassword(mPassEdit.getText().toString());
            user.setUsername(mUserNameEdit.getText().toString());
            user.login(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        ToastUtil.showToast(" 登录成功 ");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()));
                        startActivity(intent);
                    } else {
                        if (e.getErrorCode() == 101){
                            ToastUtil.showToast(" 用户名或密码不正确 ");
                        } else {
                            ToastUtil.showToast(" 登录失败 ");

                        }
                    }
                }
            });
        }
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_REQUEST_CODE && resultCode == Constant.CODE_SCHOOL_LOGIN){
            String school = data.getStringExtra(Constant.VALUE);
            mSelectSchoolTxt.setText(String.format(getString(R.string.login_school), school));
        }
    }
    public boolean clearEditText(EditText text, MotionEvent motionEvent) {
        Drawable drawable = text.getCompoundDrawables()[2];
        if (drawable == null)
            return false;
        if (motionEvent.getAction() == MotionEvent.ACTION_UP && motionEvent.getX() > text.getWidth()
                - text.getPaddingRight() - drawable.getIntrinsicWidth()){
            text.setText("");
        }
        return true;
    }
}
