package com.lzp.classroomassistant.mainpages.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Course;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.net.HttpManager;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.util.Constant;

import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;

public class AddCourseActivity extends BaseActivity implements CourseListAdapter.AddCouresListener {

    private static final String TAG = "AddCourseActivity";
    @InjectView(R.id.couresListViewId)
    ListView mCourseListView;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    private int mResultCode = 0;
    private CourseListAdapter mListAdapter;
    private HttpManager mHttpManager = HttpManager.getInstance();

    public static void start(Activity context){
        Intent intent = new Intent(context,AddCourseActivity.class);
        context.startActivityForResult(intent, Constant.ACTIVITY_REQUEST_CODE);
    }
    @Override
    protected int getLayoutView() {
        return R.layout.activity_add_course;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        mToolbar.setTitle(R.string.add_course_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadAllCourse();
    }

    private void loadAllCourse(){
        BmobQuery<Course> query = new BmobQuery<>();
        query.setLimit(50);
        query.include("teacher");
        Observable observable = query.findObjectsObservable(Course.class);
        HttpManager.toSubscribe(observable,new ProgressSubscriber<List<Course>>(new SubscriberOnNextListener<List<Course>>() {
            @Override
            public void onNext(List<Course> list) {
                appData.setAllCourseList(list);
                mListAdapter = new CourseListAdapter(appData.getCourseList(),list,
                        AddCourseActivity.this, AddCourseActivity.this);
                mCourseListView.setAdapter(mListAdapter);
            }
        },this));
    }

    @Override
    public void onClick(View view, int pos) {
        TextView textView = (TextView)view;
        if (textView.getText().equals(getString(R.string.remove_course))){
            removeCourse(pos);
        } else {
            addCourse(pos);
        }
//        if (appData.getCourseList().get(pos).isSelect()){
//            appData.getCourseList().get(pos).setSelect(false);
//            updateSelect(pos,false);
//
//        } else {
//            appData.getCourseList().get(pos).setSelect(true);
//            updateSelect(pos,true);
//        }

        mResultCode = 100;
        setResult(mResultCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(mResultCode);
    }

    public void removeCourse(final int pos){
        final User user = BmobUser.getCurrentUser(User.class);
        final Course course = appData.getAllCourseList().get(pos);
        BmobRelation useRelation = new BmobRelation();
        useRelation.remove(course);
        user.setCourses(useRelation);
        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    appData.getCourseList().remove(course);
                    mListAdapter.notifyDataSetChanged();
                } else {
                    Log.d("wang",e.toString());
                }
            }
        });
        BmobRelation couRelation = new BmobRelation();
        couRelation.remove(user);
        course.setStudents(couRelation);
        course.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Log.d(TAG," 更新成功");
                } else {
                    Log.d("wang",e.toString());
                }
            }
        });
    }

    private void addCourse(int pos){
        final User user = BmobUser.getCurrentUser(User.class);
        final Course course = appData.getAllCourseList().get(pos);
        BmobRelation useRelation = new BmobRelation();
        useRelation.add(course);
        user.setCourses(useRelation);
        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Log.d(TAG," 更新成功");
                    appData.getCourseList().add(course);
                    mListAdapter.notifyDataSetChanged();
                } else {
                    Log.d("wang",e.toString());
                }
            }
        });
        BmobRelation couRelation = new BmobRelation();
        couRelation.add(user);
        course.setStudents(couRelation);
        course.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Log.d(TAG," 更新成功");
                } else {
                    Log.d("wang ", e.toString());
                }
            }
        });
    }

    @Override
    public void onLongClick(View view, int pos) {

    }
}
