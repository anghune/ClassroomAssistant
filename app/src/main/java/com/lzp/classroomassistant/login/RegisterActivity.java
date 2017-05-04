package com.lzp.classroomassistant.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {

    @InjectView(R.id.schoolTxtId)
    TextView mSchoolTxt;
    @InjectView(R.id.collegeTxtId)
    TextView mCollegeTxt;
    @InjectView(R.id.edit_username)
    EditText mNameEdit;
    @InjectView(R.id.userNumberEditId)
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
    @OnTouch({R.id.edit_username,R.id.userNumberEditId})
    boolean onTouch(View view, MotionEvent motionEvent){
        switch (view.getId()){
            case R.id.edit_username:
                clearEditText(mNameEdit,motionEvent);
                break;
            case R.id.userNumberEditId:
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
                || mPassEdit.getText().toString().isEmpty()){
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
            user.signUp(new SaveListener<User>() {
                @Override
                public void done(User s, BmobException e) {
                    if(e==null){
                        UserOrgan userOrgan = new UserOrgan();
                        userOrgan.setUserName(s);
                        userOrgan.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null){
                                    ToastUtil.showToast(user.getName()+" 注册成功! 请登录" );
                                    finish();
                                }
                            }
                        });
                    }else{
                        if (e.getErrorCode() == 202){
                            ToastUtil.showToast(user.getName()+" 用户名已经存在 " );
                        }
                    }
                }
            });
//            Observable<User> observable = user.signUpObservable(User.class);
//            HttpManager.toSubscribe(observable,new ProgressSubscriber<User>(new SubscriberOnNextListener<User>() {
//                @Override
//                public void onNext(User  e) {
//                    if(e == null){
//                        ToastUtil.showToast("注册成功:");
//                        finish();
//                    }else{
////                        if (e.getErrorCode() == 202){
//                            ToastUtil.showToast(user.getUsername() + " 学号/工号已注册！" );
////                        }
//                    }
//                }
//            }));
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
}
