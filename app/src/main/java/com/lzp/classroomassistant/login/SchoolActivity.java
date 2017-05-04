package com.lzp.classroomassistant.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.College;
import com.lzp.classroomassistant.data.School;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.util.Constant;

import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import rx.Observable;

import static com.lzp.classroomassistant.net.HttpManager.toSubscribe;

public class SchoolActivity extends BaseActivity implements SchoolListAdapter.ItemOnClick{
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.schoolRecycleId)
    RecyclerView mRecyclerView;

    private int mType;
    private SchoolListAdapter mSchoolListAdapter;


    @Override
    protected int getLayoutView() {
        return R.layout.activity_school;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initToolBar();
        loadData(mType);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void initToolBar(){
        mType = getIntent().getIntExtra(Constant.KEY,0);
        if (mType == Constant.TYPE_SCHOOL_LOGIN || mType == Constant.TYPE_SCHOOL_REGISTER){
            mToolbar.setTitle(R.string.select_school);
        } else {
            mToolbar.setTitle(R.string.select_college);
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void loadData(int type){
        if (type == Constant.TYPE_SCHOOL_REGISTER || type == Constant.TYPE_SCHOOL_LOGIN){
            BmobQuery<School> query = new BmobQuery<School>();
            query.setLimit(50);
            final Observable observable = query.findObjectsObservable(School.class);
            toSubscribe(observable,new ProgressSubscriber<List<School>>(new SubscriberOnNextListener<List<School>>() {
                @Override
                public void onNext(List<School> list) {
                    for (School school: list){
                        Log.d("wang","学校名称："+ school.getName());
                    }
                    appData.setSchoolList(list);
                    if (mSchoolListAdapter!=null){
                        mSchoolListAdapter.setSchoolList(list);
                        mSchoolListAdapter.setType(mType);
                        mSchoolListAdapter.notifyDataSetChanged();
                    } else {
                        mSchoolListAdapter = new SchoolListAdapter(list,null,SchoolActivity.this,mType);
                        mRecyclerView.setAdapter(mSchoolListAdapter);
                    }
                }
            },SchoolActivity.this));
        }else if (type == Constant.TYPE_COLLEGE_REGISTER){
            BmobQuery<College> query = new BmobQuery<College>();
            query.setLimit(50);
            query.include("teacher");
            final Observable observable = query.findObjectsObservable(College.class);
            toSubscribe(observable,new ProgressSubscriber<List<School>>(new SubscriberOnNextListener<List<College>>() {
                @Override
                public void onNext(List<College> list) {
                    for (College college: list){
                        Log.d("wang","学院名称："+ college.getName());
                    }
                    appData.setCollegeList(list);
                    if (mSchoolListAdapter != null){
                        mSchoolListAdapter.setCollegeList(list);
                        mSchoolListAdapter.setType(mType);
                        mSchoolListAdapter.notifyDataSetChanged();
                    } else {
                        mSchoolListAdapter = new SchoolListAdapter(null,list,SchoolActivity.this,mType);
                        mRecyclerView.setAdapter(mSchoolListAdapter);
                    }
                }
            },SchoolActivity.this));
        }
    }

    @Override
    public void onClick(View view, int pos) {
        if (mType == Constant.TYPE_SCHOOL_LOGIN){
            Intent intent = new Intent(this,LoginActivity.class);
            intent.putExtra(Constant.VALUE,appData.getSchoolList().get(pos).getName());
            setResult(Constant.CODE_SCHOOL_LOGIN,intent);
        }  else if (mType == Constant.TYPE_SCHOOL_REGISTER){
            Intent intent = new Intent(this,RegisterActivity.class);
            intent.putExtra(Constant.VALUE,appData.getSchoolList().get(pos).getName());
            setResult(Constant.CODE_SCHOOL_REGISTER,intent);

        } else if (mType == Constant.TYPE_COLLEGE_REGISTER){
            Intent intent = new Intent(this,RegisterActivity.class);
            intent.putExtra(Constant.VALUE,appData.getCollegeList().get(pos).getName());
            Log.d("wang"," 选中 院系 ：" +appData.getCollegeList().get(pos).getName() );
            setResult(Constant.CODE_COLLEGE_REGISTER,intent);
        }
        finish();
    }

    @Override
    public void onLongClick(View view, int pos) {
//        if (mType == Constant.TYPE_COLLEGE_REGISTER){
//            College college = appData.getCollegeList().get(pos);
//            final School school = appData.getSchoolList().get(1);
//            college.setSchool(school);
//            college.save(new SaveListener<String>() {
//                @Override
//                public void done(String s, BmobException e) {
//                    if (e == null){
//                        ToastUtil.showToast("关联成功 学校" + school.getName());
//                    } else {
//                        ToastUtil.showToast("关联失败！");
//                    }
//                }
//            });
//        }
    }
}
