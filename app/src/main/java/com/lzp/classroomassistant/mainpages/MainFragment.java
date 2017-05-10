package com.lzp.classroomassistant.mainpages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzp.classroomassistant.BaseFragment;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Course;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.mainpages.common.AddCourseActivity;
import com.lzp.classroomassistant.mainpages.common.SelectSeatActivity;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.ToastUtil;
import com.lzp.classroomassistant.util.Utils;
import com.lzp.classroomassistant.view.CornerTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import rx.Observable;

import static com.lzp.classroomassistant.net.HttpManager.toSubscribe;


public class MainFragment extends BaseFragment {


    @InjectView(R.id.addImageId)
    ImageView mAddCourseImage;
    @InjectView(R.id.weekNames)
    LinearLayout weekNames;
    @InjectView(R.id.sections)
    LinearLayout sections;
    @InjectView(R.id.refreshId)
    SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.containerLayout)
    LinearLayout mContainerLayout;
    @InjectViews({R.id.weekPanel_1, R.id.weekPanel_2, R.id.weekPanel_3, R.id.weekPanel_4,
            R.id.weekPanel_5, R.id.weekPanel_6, R.id.weekPanel_7})
    List<LinearLayout> mWeekViews;


    private int mUserType = 1;
    private int itemHeight;
    private int maxSection = 12;
    private final int mResultCode = 100;
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener;
    private static final String TAG = "MainFragment";

    @OnClick({R.id.addImageId})
    void onClick(View v){
        switch (v.getId()){
            case R.id.addImageId:
                Log.d("wang"," 被点击！！！");
                addCourse();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        itemHeight = getResources().getDimensionPixelSize(R.dimen.sectionHeight);

        initWeekNameView();
        initSectionView();
        initSwipeLayout();

    }

    private void initSwipeLayout() {
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mContainerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContainerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRefreshLayout.setRefreshing(true);
                //自动刷新
               loadCourse();
            }
        });
        //下拉加载
        mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCourse();
            }
        };
        mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
    }


    /**
     * 初始化课程表
     */
    private void initWeekCourseView(List<List<Course>> listList) {
        for (int i = 0; i < mWeekViews.size(); i++) {
            initWeekPanel(mWeekViews.get(i), listList.get(i));
        }
    }

    private void loadData(int type){
        if (type == Constant.STUDENT_TYPE){
            loadStudentCourse();
        } else if (type == Constant.TEACHER_TYPE){
            loadTeacherCourse();
        }
    }
    private void loadCourse(){
        BmobQuery<Course> query = new BmobQuery<Course>();
        query.setLimit(50);
        User user = BmobUser.getCurrentUser(User.class);
        query.include("teacher");
        query.addWhereRelatedTo("courses", new BmobPointer(user));
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                mRefreshLayout.setRefreshing(false);
                appData.setCourseList(list);
                clearChildView();
                initWeekCourseView(filtData(list));
            }
        });
//        final Observable observable = query.findObjectsObservable(Course.class);
//        toSubscribe(observable,new ProgressSubscriber<List<Course>>(new SubscriberOnNextListener<List<Course>>() {
//            @Override
//            public void onNext(List<Course> list) {
//                mRefreshLayout.setRefreshing(false);
//                appData.setCourseList(list);
//                clearChildView();
//                initWeekCourseView(filtData(list));
//            }
//        },mContext));
    }


    private void loadStudentCourse(){
        BmobQuery<Course> query = new BmobQuery<Course>();
        query.setLimit(50);
        query.include("teacher");
        final Observable observable = query.findObjectsObservable(Course.class);
        toSubscribe(observable,new ProgressSubscriber<List<Course>>(new SubscriberOnNextListener<List<Course>>() {
            @Override
            public void onNext(List<Course> list) {
                appData.setCourseList(list);
                initWeekCourseView(filtData(list));
            }
        },mContext));
    }

    private void loadTeacherCourse(){
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Course> query = new BmobQuery<Course>();
        query.setLimit(50);
        query.addWhereEqualTo("teacher",user);
        query.include("teacher");
        final Observable observable = query.findObjectsObservable(Course.class);
        toSubscribe(observable,new ProgressSubscriber<List<Course>>(new SubscriberOnNextListener<List<Course>>() {
            @Override
            public void onNext(List<Course> list) {
                appData.setCourseList(list);
                initWeekCourseView(filtData(list));
            }
        },mContext));
    }




    private List<List<Course>> filtData(List<Course> courseList){

        int size = courseList.size();
        List<List<Course>> courseModels = new ArrayList<>();
        List<Course> models_1 = new ArrayList<>();
        List<Course> models_2 = new ArrayList<>();
        List<Course> models_3 = new ArrayList<>();
        List<Course> models_4 = new ArrayList<>();
        List<Course> models_5 = new ArrayList<>();
        List<Course> models_6 = new ArrayList<>();
        List<Course> models_7 = new ArrayList<>();
        courseModels.add(models_1);
        courseModels.add(models_2);
        courseModels.add(models_3);
        courseModels.add(models_4);
        courseModels.add(models_5);
        courseModels.add(models_6);
        courseModels.add(models_7);
        for (int i = 0; i <  size; i++){
//            if (Utils.getCurrUserType() == Constant.TEACHER_TYPE ||
//                    (courseList.get(i).isSelect() && Utils.getCurrUserType() == Constant.STUDENT_TYPE)){
                if (courseList.get(i).getWeek() == 1){
                    models_1.add(courseList.get(i));
                }else if (courseList.get(i).getWeek() == 2){
                    models_2.add(courseList.get(i));
                } else if (courseList.get(i).getWeek() == 3){
                    models_3.add(courseList.get(i));
                }else if (courseList.get(i).getWeek() == 4){
                    models_4.add(courseList.get(i));
                }else if (courseList.get(i).getWeek() == 5){
                    models_5.add(courseList.get(i));
                }else if (courseList.get(i).getWeek() == 6){
                    models_6.add(courseList.get(i));
                }else if (courseList.get(i).getWeek() == 7){
                    models_7.add(courseList.get(i));
                }
//            }
        }
        return courseModels;
    }

    /**
     * 顶部周一到周日的布局
     **/
    private void initWeekNameView() {
        for (int i = 0; i < mWeekViews.size() + 1; i++) {
            TextView tvWeekName = new TextView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
            if (i != 0) {
                lp.weight = 1;
                tvWeekName.setText("周" + Utils.intToZH(i));
                if (i == getWeekDay()) {
                    tvWeekName.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    tvWeekName.setTextColor(Color.parseColor("#4A4A4A"));
                }
            } else {
                lp.weight = 0.8f;
                tvWeekName.setText(getMonth() + "月");
            }
            tvWeekName.setGravity(Gravity.CENTER_HORIZONTAL);
            tvWeekName.setLayoutParams(lp);
            weekNames.addView(tvWeekName);
        }
    }

    /**
     * 左边节次布局，设定每天最多12节课
     */
    private void initSectionView() {
        for (int i = 1; i <= maxSection; i++) {
            TextView tvSection = new TextView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.sectionHeight));
            lp.gravity = Gravity.CENTER;
            tvSection.setGravity(Gravity.CENTER);
            tvSection.setText(String.valueOf(i));
            tvSection.setLayoutParams(lp);
            sections.addView(tvSection);
        }
    }

    /**
     * 当前星期
     */
    public int getWeekDay() {
        int w = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (w <= 0) {
            w = 7;
        }
        return w;
    }

    /**
     * 当前月份
     */
    public int getMonth() {
        int w = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return w;
    }

    /**
     * 每次刷新前清除每个LinearLayout上的课程view
     */
    private void clearChildView() {
        for (int i = 0; i < mWeekViews.size(); i++) {
            if (mWeekViews.get(i) != null)
                if (mWeekViews.get(i).getChildCount() > 0)
                    mWeekViews.get(i).removeAllViews();
        }
    }

    private void addCourse(){
        if (Utils.getCurrUserType() == Constant.STUDENT_TYPE) {
            Log.d("wang","  STUDENT_TYPE  ");
            Intent intent = new Intent(getActivity(),AddCourseActivity.class);
            startActivityForResult(intent,Constant.ACTIVITY_REQUEST_CODE);

        } else if (Utils.getCurrUserType() == Constant.TEACHER_TYPE){
            Log.d("wang","  TEACHER_TYPE  ");
            ToastUtil.showToast("该功能仅限学生开放！");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == mResultCode){
            Log.d(TAG,"  onActivityResult  ");
//            BmobQuery<Course> query = new BmobQuery<Course>();
//            query.setLimit(50);
//            final Observable observable = query.findObjectsObservable(Course.class);
//            toSubscribe(observable,new ProgressSubscriber<List<Course>>(new SubscriberOnNextListener<List<Course>>() {
//                @Override
//                public void onNext(List<Course> list) {
//                    appData.setCourseList(null);
//                    appData.setCourseList(list);
//                    clearChildView();
//                    initWeekCourseView(filtData(list));
//                }
//            },mContext));
            loadCourse();

        }

    }

    public void initWeekPanel(LinearLayout ll, List<Course> data) {

        if (ll == null || data == null || data.size() < 1)
            return;
        Course firstCourse = data.get(0);
        for (int i = 0; i < data.size(); i++) {
            final Course courseModel = data.get(i);

            if (courseModel.getSection() == 0 || courseModel.getSectionSpan() == 0)
                return;
            FrameLayout frameLayout = new FrameLayout(mContext);

            CornerTextView tv = new CornerTextView(mContext,
                   Utils.getCourseBgColor(courseModel.getCourseFlag()),
                    Utils.dip2px(mContext, 3));
            LinearLayout.LayoutParams frameLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    itemHeight * courseModel.getSectionSpan());
            LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            if (i == 0) {
                frameLp.setMargins(0, (courseModel.getSection() - 1) * itemHeight, 0, 0);
            } else {
                frameLp.setMargins(0, (courseModel.getSection() - (firstCourse.getSection() + firstCourse.getSectionSpan())) * itemHeight, 0, 0);
            }
            tv.setLayoutParams(tvLp);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(12);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            tv.setText(courseModel.getCourseName() + "\n @" + courseModel.getClassRoom());

            frameLayout.setLayoutParams(frameLp);
            frameLayout.addView(tv);
            frameLayout.setPadding(2, 2, 2, 2);
            ll.addView(frameLayout);
            firstCourse = courseModel;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isWeek(courseModel)){
                        ToastUtil.showToast("不是当天的课程不能签到(⊙o⊙)哦");
                        return;
                    }
                    if (!Utils.hasCheckPermission(courseModel.getSection()) ){
                        showToast(courseModel.getCourseName());
                        Intent intent = new Intent(mContext, SelectSeatActivity.class);
                        intent.putExtra(Constant.VALUE,courseModel);
                        startActivity(intent);
                    } else {
                        ToastUtil.showToast("还没有到签到开放的时间(⊙o⊙)哦");
                    }

/*                    final User user = BmobUser.getCurrentUser(User.class);
                    BmobRelation relation = new BmobRelation();
                    relation.add(courseModel);
                    user.setCourses(relation);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                ToastUtil.showToast(user.getName()+" 关联 "+ courseModel.getCourseName() + "成功");
                            } else {
                                ToastUtil.showToast("关联失败！");
                                Log.d("wang",e.toString());
                            }
                        }
                    });*/
                }
            });
        }
    }

    private boolean isWeek(Course course){
        int w = getWeekDay();
        int c = course.getWeek().intValue();
        if (w == c){
            Log.d("wang"," 星期 " + getWeekDay() + " 课程星期 " + course.getWeek());
            return true;
        }else {
            Log.d("wang"," 星期 " + getWeekDay() + " 课程星期 " + course.getWeek());
            return false;
        }
    }


    /**
     * Toast
     */
    private void showToast(String msg) {

    }
}
