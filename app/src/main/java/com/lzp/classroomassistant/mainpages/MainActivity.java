package com.lzp.classroomassistant.mainpages;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.FaceRecogni;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.event.RefreshEvent;
import com.lzp.classroomassistant.login.CameraActivity;
import com.lzp.classroomassistant.login.LoginActivity;
import com.lzp.classroomassistant.mainpages.common.ViewPagerAdapter;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.ValueEventListener;

public class MainActivity extends BaseActivity implements MainContract.View{

    @InjectView(R.id.tab_curriculum)
    RadioButton mTabCurriculum;
    @InjectView(R.id.tab_letter)
    RadioButton mTabLetter;
    @InjectView(R.id.tab_assistant)
    RadioButton mTabAssistant;
    @InjectView(R.id.tab_personal)
    RadioButton mTabPersonal;
    @InjectView(R.id.tab_vp)
    ViewPager mTabViewPager;

    private static final String TAG = "MainActivity";
    NotificationManager notificationManager;

    @OnClick({R.id.tab_curriculum,R.id.tab_letter,R.id.tab_assistant,R.id.tab_personal})
    void onClick(View view){
        switch (view.getId()){
            case R.id.tab_curriculum:
                mTabViewPager.setCurrentItem(0,true);
                break;
            case R.id.tab_letter:
                mTabViewPager.setCurrentItem(1,true);
                break;
            case R.id.tab_assistant:
                mTabViewPager.setCurrentItem(2,true);
                break;
            case R.id.tab_personal:
                mTabViewPager.setCurrentItem(3,true);
                break;


        }
    }

    private int current = 0;
    private MainContract.Presenter mPresenter;
    private ViewPagerAdapter mAdapter;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        mPresenter = new MainPresenter(this);
        RadioButton[] radioButtons = {
                mTabCurriculum,
                mTabLetter,
                mTabAssistant,
                mTabPersonal
        };

//        mPresenter.setRadioButton(0,radioButtons);
//        requestPermission();
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mPresenter.initFragmentList(radioButtons);
        autoLogin();

        startTimeData();

    }

    private void autoLogin(){
        User user =  BmobUser.getCurrentUser(User.class);
        if(user != null){
            // 允许用户使用应用
            String name = user.getName();
            Log.i(TAG," id " + user.getObjectId() + " name = "+ user.getName() + " 学号 "+ user.getUsername());
            BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()));
            ToastUtil.showToast(name + " 你好，自动登录成功!");
            connectIM();
        }else{
            //缓存用户对象为空时， 可打开用户注册界面…
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void connectIM(){
        User user = BmobUser.getCurrentUser(User.class);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Log.i(TAG,"connect success");
                    //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                    EventBus.getDefault().post(new RefreshEvent());
                } else {
                    Log.e(TAG,e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
//        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
//            @Override
//            public void onChange(ConnectionStatus var1) {
//
//            }
//        });
    }


    @Override
    public void setViewpager(ArrayList<Fragment> fragments, final RadioButton[] radioButtons) {
        mTabViewPager.setOffscreenPageLimit(1);
        mAdapter = new ViewPagerAdapter(fragments,getSupportFragmentManager(),mTabViewPager);
        mAdapter.setOnExtraPageChangeListener(new ViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onExtraPageSelected(int position) {
                setRadioButton(position,radioButtons);
                current = position;
            }

            @Override
            public void onExtraPageScrollStateChanged(int state) {

            }
        });

        mTabViewPager.setAdapter(mAdapter);
        mTabViewPager.setCurrentItem(0);

    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private void setRadioButton(int pos,  RadioButton[] radioButtons){
        for (int i = 0; i < radioButtons.length; i++){
            if (i == pos ){
                radioButtons[i].setChecked(true);
            }else {
                radioButtons[i].setChecked(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (current == 0){
            super.onBackPressed();
        }else {
            mTabViewPager.setCurrentItem(current-1,true);
        }
    }

    private void startTimeData(){
        final BmobRealTimeData data = new BmobRealTimeData();
        // 监听表更新

        data.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject arg0) {
                if(BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))){
                    JSONObject optJSONObject = arg0.optJSONObject("data");
                    Log.d(TAG," optJson = " + optJSONObject.toString());
                    String objectId = optJSONObject.optString("objectId");
                    Log.d(TAG,"objectId " + objectId);
                    queryFaceRecogni(objectId);
                }

            }

            @Override
            public void onConnectCompleted(Exception ex) {
                Log.d("bmob", "连接成功:"+ data.isConnected());
                data.subTableUpdate("FaceRecogni");
            }
        });
    }

    private void queryFaceRecogni(String objectId){
        BmobQuery<FaceRecogni> query = new BmobQuery<>();
        query.include("teacher,student,course");
//        query.addWhereEqualTo("objectId",objectId);
//        query.findObjects(new FindListener<FaceRecogni>() {
//            @Override
//            public void done(List<FaceRecogni> list, BmobException e) {
//                if (e == null){
//                    FaceRecogni faceRecogni = list.get(0);
//                    User currUser = BmobUser.getCurrentUser(User.class);
//                    if (currUser.equals(faceRecogni.getStudent())){
//                        StringBuilder stringBuilder = new StringBuilder(faceRecogni.getTeacher().getName())
//                                .append("老师要求你进行人脸识别");
//                        notification(stringBuilder.toString());
//                        ToastUtil.showToast("老师要求你进行人脸识别 !!!!!");
//
//                    }
//                } else {
//                    Log.d(TAG, e.getMessage().toString());
//                    ToastUtil.showToast(e.getMessage().toString());
//                }
//            }
//        });
        query.getObject(objectId, new QueryListener<FaceRecogni>() {
            @Override
            public void done(FaceRecogni faceRecogni, BmobException e) {
                if (e == null){
                    User currUser = BmobUser.getCurrentUser(User.class);
                    appData.setFaceRecogni(faceRecogni);
                    if (currUser.equals(faceRecogni.getStudent())
                            && faceRecogni.getResult().intValue() == Constant.SEND_FACE_RECOGNITION ){
                        StringBuilder stringBuilder = new StringBuilder(faceRecogni.getTeacher().getName())
                                .append("老师要求你进行人脸识别");
                        notification(stringBuilder.toString());
                        ToastUtil.showToast(stringBuilder.toString());

                    }
                } else {
                    Log.d(TAG, e.getErrorCode()+" " + e.getMessage().toString() );
                    ToastUtil.showToast(e.getMessage().toString());
                }
            }
        });

    }
    private void notification(String content){
        Intent intent = new Intent(this, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.KEY,Constant.REQUEST_FACE_RECOGINTION);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =  new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent);
        builder.setContentTitle("人脸识别");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.app_logo);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notificationManager.notify(0, notification);
    }





}
