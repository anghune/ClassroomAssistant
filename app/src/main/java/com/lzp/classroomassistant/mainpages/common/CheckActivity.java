package com.lzp.classroomassistant.mainpages.common;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Course;
import com.lzp.classroomassistant.data.FaceRecogni;
import com.lzp.classroomassistant.data.Seat;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.message.TitleItemDecoration;
import com.lzp.classroomassistant.net.HttpManager;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.PopupMenuUtil;
import com.lzp.classroomassistant.util.ToastUtil;
import com.lzp.classroomassistant.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import rx.Observable;

public class CheckActivity extends BaseActivity implements CheckListAdapter.Listener{

    @InjectView(R.id.checkRecycleId)
    RecyclerView mRecyclerView;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    private List<User> mUsers = new ArrayList<>();
    private List<String> mNames = new ArrayList<>();
    private int mDivider;
    private CheckListAdapter mCheckListAdapter;
    private TitleItemDecoration mTitleItemDecoration;
    private DividerItemDecoration mDividerItemDecoration;
    private Course mCourse;
    private static final String TAG = "CheckActivity";
    @Override
    protected int getLayoutView() {
        return R.layout.activity_check;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mToolbar.setTitle(R.string.check_act_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCourse = (Course)getIntent().getSerializableExtra(Constant.VALUE);
        loadCheckedUser();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mDividerItemDecoration = new  DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST);




    }
    private void loadAllUser(){
        BmobQuery<User> query = new BmobQuery<User>();
        query.setLimit(50);
        query.addWhereEqualTo("type",1);
        query.addWhereRelatedTo("students",new BmobPointer(mCourse));
        if (!mNames.isEmpty()){
            query.addWhereNotContainedIn("name",mNames);
        }
        Observable observable = query.findObjectsObservable(User.class);
        HttpManager.toSubscribe(observable,new ProgressSubscriber<List<User>>(new SubscriberOnNextListener<List<User>>() {
            @Override
            public void onNext(List<User> list) {
//                mUsers.addAll(list);
                for (User user: list){
                    user.setOrgan(Constant.UNCHECK_CLASS);
                    mUsers.add(user);
                    Log.d("wang","没有签到的人 " + user.getName());
                }
                mDivider = list.size();
                Log.d("wang","签到分隔符  "+ mDivider);
                if (appData.getSeatList().size()!= 0){
                    for (Seat seat: appData.getSeatList()){
                        User user = seat.getStudent();
                        user.setOrgan(Constant.CHECK_CLASS);
                        getFaceRecogResult(user);
                        mUsers.add(user);
                    }
                }
                mTitleItemDecoration = new TitleItemDecoration(CheckActivity.this,(ArrayList<User>) mUsers);
                mCheckListAdapter = new CheckListAdapter(mUsers,CheckActivity.this);
                mRecyclerView.setAdapter(mCheckListAdapter);
                mRecyclerView.addItemDecoration(mTitleItemDecoration);
                mRecyclerView.addItemDecoration(mDividerItemDecoration);

            }
        }));

    }
    private void loadCheckedUser(){
        BmobQuery<Seat> query = new BmobQuery<Seat>();
        query.setLimit(50);
        query.include("student");
        query.addWhereEqualTo("course",new BmobPointer(mCourse));
        final Observable observable = query.findObjectsObservable(Seat.class);
        HttpManager.toSubscribe(observable, new ProgressSubscriber<List<Seat>>(new SubscriberOnNextListener<List<Seat>>() {
            @Override
            public void onNext(List<Seat> list) {
                    appData.setSeatList(list);
                    for (Seat seat: list){
                        mNames.add(seat.getStudent().getName());
                        Log.d("wang", "已经签到了的 " + seat.getStudent().getName());
                    }
                loadAllUser();
            }
        },this));

    }

    private void getFaceRecogResult(final User user){
        BmobQuery<FaceRecogni> query = new BmobQuery<>();
        query.include("teacher,student,course");
        query.addWhereEqualTo("teacher",BmobUser.getCurrentUser(User.class));
        query.addWhereEqualTo("student",user);
        query.addWhereEqualTo("course",mCourse);
        query.order("-createdAt").findObjects(new FindListener<FaceRecogni>() {
            @Override
            public void done(List<FaceRecogni> list, BmobException e) {
                if (e == null){
                    user.setResult(list.get(0).getResult());
                    mCheckListAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG,e.getMessage().toString());
                }
            }
        });
    }

    @Override
    public void onClickMore(View view, final int pos) {
        PopupMenuUtil.showPopupMenu(R.menu.check_menu, this, view, new PopupMenuUtil.ItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.faceMenu:
                        FaceRecogni faceRecogni = new FaceRecogni(BmobUser.getCurrentUser(User.class),
                                mCourse,mUsers.get(pos),Constant.SEND_FACE_RECOGNITION);
                        faceRecogni.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null){
                                    ToastUtil.showToast("给 "+mUsers.get(pos).getName()+" 发送人脸识别成功！");
                                }else {
                                    Log.d("Wang",e.getMessage().toString());
                                }
                            }
                        });
                        break;
                }
                return true;
            }
        });
    }
}
