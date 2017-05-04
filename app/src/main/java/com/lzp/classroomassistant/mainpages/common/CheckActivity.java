package com.lzp.classroomassistant.mainpages.common;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Course;
import com.lzp.classroomassistant.data.Seat;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.net.HttpManager;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.view.CheckTitleItemDecoration;
import com.lzp.classroomassistant.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import rx.Observable;

public class CheckActivity extends BaseActivity {

    @InjectView(R.id.checkRecycleId)
    RecyclerView mRecyclerView;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    private List<User> mUsers = new ArrayList<>();
    private List<String> mNames = new ArrayList<>();
    private int mDivider;
    private CheckListAdapter mCheckListAdapter;
    private CheckTitleItemDecoration mTitleItemDecoration;
    private DividerItemDecoration mDividerItemDecoration;
    private Course mCourse;
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
                mUsers.addAll(list);
                for (User user: list){
                    Log.d("wang","没有签到的人 " + user.getName());
                }
                mDivider = list.size();
                Log.d("wang","签到分隔符  "+ mDivider);
                if (appData.getSeatList().size()!= 0){
                    for (Seat seat: appData.getSeatList()){
                        mUsers.add(seat.getStudent());
                    }
                }
                mTitleItemDecoration = new CheckTitleItemDecoration(CheckActivity.this,(ArrayList<User>) mUsers, mDivider);
                mCheckListAdapter = new CheckListAdapter(mUsers);
                mRecyclerView.setAdapter(mCheckListAdapter);
                mRecyclerView.addItemDecoration(mTitleItemDecoration);
                mRecyclerView.addItemDecoration(mDividerItemDecoration);

            }
        }));


//        final Observable observable = query.findObjectsByTableObservable();
//        HttpManager.toSubscribe(observable, new ProgressSubscriber<JSONArray>(new SubscriberOnNextListener<JSONArray>() {
//            @Override
//            public void onNext(JSONArray jsonArray) {
//                Gson gson = new Gson();
//                if (!jsonArray.toString().isEmpty()){
//                    List<Seat> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<Seat>>(){}.getType());
//                    for (Seat seat: list){
////                    mUsers.add(seat.getStudent());
//                        mNames.add(seat.getStudent().getUsername());
//                    }
//                }
//                addUser();
//            }
//        },this));

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
}
