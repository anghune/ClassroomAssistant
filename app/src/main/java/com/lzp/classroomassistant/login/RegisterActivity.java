package com.lzp.classroomassistant.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.data.UserOrgan;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.ToastUtil;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.lzp.classroomassistant.R.id.userNumberEditId;
import static com.lzp.classroomassistant.util.Constant.PERMISSIONS_REQUEST_CAMERA;

public class RegisterActivity extends BaseActivity {

    @InjectView(R.id.schoolTxtId)
    TextView mSchoolTxt;
    @InjectView(R.id.collegeTxtId)
    TextView mCollegeTxt;
    @InjectView(R.id.edit_username)
    EditText mNameEdit;
    @InjectView(userNumberEditId)
    EditText mNumberEdit;
    @InjectView(R.id.edit_pass)
    EditText mPassEdit;
    @InjectView(R.id.usetype_group)
    RadioGroup mRadioGroup;
    @InjectView(R.id.radio_student)
    RadioButton mStudentRadio;
    @InjectView(R.id.radio_teacher)
    RadioButton mTeacherRadio;


    private Integer userType = 1;
    // 拍照得到的照片文件
    private File mPictureFile;
    private Uri mCameraTempUri;

//    private String mLocalAvatar;
//    private File mTmpFile;


    private static final String TAG = "RegisterActivity";
    @OnClick({R.id.schoolTxtId,R.id.collegeTxtId,R.id.registerBtnId})
    void onClick(View view){
        switch (view.getId()){
            case R.id.schoolTxtId:
                startSchoolActivity(Constant.TYPE_SCHOOL_REGISTER);
                break;

            case R.id.collegeTxtId:
                startSchoolActivity(Constant.TYPE_COLLEGE_REGISTER);
                break;

            case R.id.registerBtnId:
                    registe();

                break;
            default:
                break;
        }
    }
    @OnTouch({R.id.edit_username, userNumberEditId})
    boolean onTouch(View view, MotionEvent motionEvent){
        switch (view.getId()){
            case R.id.edit_username:
                clearEditText(mNameEdit,motionEvent);
                break;
            case userNumberEditId:
                clearEditText(mNumberEdit,motionEvent);
                break;

        }
        return false;
    }




    @Override
    protected int getLayoutView() {
        return R.layout.activity_register;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        requestPermission();
        addListener();
    }



    private void startSchoolActivity(int type){
        Intent intent = new Intent(RegisterActivity.this,SchoolActivity.class);
        intent.putExtra(Constant.KEY,type);
        startActivityForResult(intent,Constant.ACTIVITY_REQUEST_CODE);
    }

    private void addListener(){
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_teacher) {
                    mTeacherRadio.setChecked(true);
                    userType = 2;
                } else if (i == R.id.radio_student) {
                    mStudentRadio.setChecked(true);
                    userType = 1;
                }
            }
        });
    }

    private void registe(){
        if(mSchoolTxt.getText().toString().equals("学校")|| mCollegeTxt.getText().toString().equals("院系")
                || mNumberEdit.getText().toString().isEmpty() || mNumberEdit.getText().toString().isEmpty()
                || mPassEdit.getText().toString().isEmpty() ){
            ToastUtil.showToast("请填写完整信息!");
        } else {
            String school = mSchoolTxt.getText().toString().replace("学校:","");
            String college = mCollegeTxt.getText().toString().replace("院系:","");
            String name = mNameEdit.getText().toString();
            String number = mNumberEdit.getText().toString();
            String pass = mPassEdit.getText().toString();

            final User user = new User(school,college,name,userType);
            user.setUsername(number);
            user.setPassword(pass);
            signUp(user);


        }
    }

    private void signUp(final User user){
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User s, BmobException e) {
                if(e==null){
                    s.setPassword(mPassEdit.getText().toString());
                    autoLogin(s);
                }else{
                    if (e.getErrorCode() == 202){
                        ToastUtil.showToast(user.getName()+" 学号已经存在 " );
                    }
                }
            }
        });
    }
    private void setUserOrgan(User user){
        UserOrgan userOrgan = new UserOrgan();
        userOrgan.setUserName(user);
        userOrgan.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    Log.d(TAG," 设置组织成功!");
                } else {
                    Log.d(TAG, e.getMessage().toString());
                }
            }
        });
    }
    private void autoLogin(User user){
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    ToastUtil.showToast(" 登录成功,请设置头像 ");
                    setUserOrgan(user);
                    startCameraActivity();

                } else {
                    if (e.getErrorCode() == 101){
                        ToastUtil.showToast(" 用户名或密码不正确 ");
                    } else {
                        ToastUtil.showToast(" 登录失败 ");
                        Log.d(TAG, e.getMessage().toString());

                    }
                }
            }
        });
    }

    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ){
            //申请CAMERA权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CAMERA);
        }

    }

    private void startCameraActivity(){
        Intent intent = new Intent(RegisterActivity.this, CameraActivity.class);
        intent.putExtra(Constant.KEY,Constant.REQUEST_CAMERA_IMAGE);
        startActivity(intent);
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                ToastUtil.showToast("授权成功!");
            } else {
                // Permission Denied
                Process.killProcess(Process.myPid());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_REQUEST_CODE){
            if (resultCode == Constant.CODE_SCHOOL_REGISTER){
                String school = data.getStringExtra(Constant.VALUE);
                mSchoolTxt.setText(String.format(getString(R.string.login_school),school));
            }else if (resultCode == Constant.CODE_COLLEGE_REGISTER){
                String college = data.getStringExtra(Constant.VALUE);
                Log.d("wang"," 返回 学院 " + college);
                mCollegeTxt.setText(String.format(getString(R.string.registe_college),college));
            }
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

    private void setAvatar(String path, final User user){
        final BmobFile bmobFile = new BmobFile(mNameEdit.getText().toString(),"Avatar",path);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    user.setAvatar(bmobFile.getFileUrl());
                    signUp(user);
                } else {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }



}
