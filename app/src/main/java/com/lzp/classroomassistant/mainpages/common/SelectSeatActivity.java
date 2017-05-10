package com.lzp.classroomassistant.mainpages.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Course;
import com.lzp.classroomassistant.data.Seat;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.net.HttpManager;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.util.AlertDialogUtil;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.ToastUtil;
import com.lzp.classroomassistant.util.Utils;
import com.lzp.classroomassistant.view.SeatTable;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import rx.Observable;

public class SelectSeatActivity extends BaseActivity {

    @InjectView(R.id.seatView)
    SeatTable mSeatTableView;
    @InjectView(R.id.courseNameTxtId)
    TextView mCourseNameTxt;
    @InjectView(R.id.roomTxtId)
    TextView mRoomTxt;
    @InjectView(R.id.sectionTxtId)
    TextView mSectionTxt;
    @InjectView(R.id.weekTimeId)
    TextView mWeekTimeTxt;
    @InjectView(R.id.teacherTxtId)
    TextView mTeacherTxt;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.sureBtnId)
    Button mSureBtn;


    @OnClick({R.id.sureBtnId})
    void onClick(View view){
        switch (view.getId()){
            case R.id.sureBtnId:
                if (Utils.getCurrUserType() == Constant.STUDENT_TYPE){
                    initAlertDialog();
                } else {
                    Intent intent = new Intent(this,CheckActivity.class);
                    intent.putExtra(Constant.VALUE,mCourse);
                    startActivity(intent);
                }
                break;
        }
    }


    private Course mCourse;
    private User mUser;
    private int mRow;
    private int mColumn;


    @Override
    protected int getLayoutView() {
        return R.layout.activity_select_seat;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initCourse();
        loadData();


    }

    private void initCourse(){
        mCourse = (Course) getIntent().getSerializableExtra(Constant.VALUE);
        mToolbar.setTitle(mCourse.getCourseName());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCourseNameTxt.setText(String.format(getString(R.string.course_name),mCourse.getCourseName()));
        mRoomTxt.setText(String.format(getString(R.string.classroom),mCourse.getClassRoom()));
        String time = String.format(getString(R.string.section),mCourse.getSection()+"");
        int end = mCourse.getSection() + mCourse.getSectionSpan() - 1;
        StringBuilder sb = new StringBuilder(time).append("-").append(end).append("节");
        mSectionTxt.setText(sb);
        mTeacherTxt.setText(String.format(getString(R.string.teacher),mCourse.getTeacher().getName()));
        mUser = BmobUser.getCurrentUser(User.class);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuSynchron:
                        loadData();
                        break;
                }
                return false;
            }
        });
    }




    private void initSeatView(){

        mSeatTableView.setScreenName(mUser.getName());//设置屏幕名称
        if (Utils.getCurrUserType() == Constant.STUDENT_TYPE){
            mSeatTableView.setMaxSelected(1);//设置最多选中
        } else if (Utils.getCurrUserType() == Constant.TEACHER_TYPE){
            mSeatTableView.setMaxSelected(0);
            mSureBtn.setText("查看签到详情");
        }
        mSeatTableView.setSeatChecker(new SeatTable.SeatChecker() {
            @Override
            public boolean isValidSeat(int row, int column) {
                if(column == 3 || column == 8) {
                    return false;
                }
                return true;
            }

            @Override
            public boolean isSold(int row, int column) {

                        int size = appData.getSeatList().size();
                        if (size >= 0 ){
                            for (int i = 0;i < size; i++){
                                Seat seat = appData.getSeatList().get(i);
                                if (row == seat.getRow() && column == seat.getColumn()){
                                    if (!seat.getStudent().getUsername().equals(mUser.getUsername())){
                                        mSeatTableView.setSoldName(seat.getStudent().getName());
                                        Log.d("wang"," id " + seat.getStudent().getUsername());
                                        return true;
                                    } else {
                                        Log.d("wang"," else id " + seat.toString());
                                        mSeatTableView.addChooseSeat(row,column);
                                        mSeatTableView.invalidate();
                                        setBtnEnable(row , column);
                                        return false;
                                    }
                                }
                            }
                        }
                return false;
            }

            @Override
            public void checked(int row, int column) {
                mRow  = row;
                mColumn = column;
            }

            @Override
            public void unCheck(int row, int column) {

            }

            @Override
            public String[] checkedSeatTxt(int row, int column) {
                return null;
            }

        });
        mSeatTableView.setData(10,12);
    }
    private void suerSeat(){

        Seat seat  = new Seat(mRow,mColumn,mUser,mCourse);
        seat.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    ToastUtil.showToast(" 确定位置成功！");
                } else {
                    ToastUtil.showToast("确定位置失败！" + e.getMessage());
                }
            }
        });
        setBtnEnable(mRow,mColumn);

    }

    private void setBtnEnable(int row, int column){
        StringBuilder sb = new StringBuilder("我坐在").append(row+1).append("排").append(Utils.num2Letter(column+1)).append("列");
        mSureBtn.setText(sb);
        mSureBtn.setEnabled(false);
    }
    private void loadData(){
        BmobQuery<Seat> query = new BmobQuery<>();
        query.setLimit(50);
        query.include("student");
        query.addWhereEqualTo("course",new BmobPointer(mCourse));
        Observable observable = query.findObjectsObservable(Seat.class);
        HttpManager.toSubscribe(observable,new ProgressSubscriber<List<Seat>>(new SubscriberOnNextListener<List<Seat>>() {
            @Override
            public void onNext(List<Seat> list) {
                appData.setSeatList(list);
                initSeatView();
            }
        },this));
    }

    private void initAlertDialog(){
        AlertDialogUtil.showDialog(this, "确定位置后无法再修改哦！", new AlertDialogUtil.AlertDialogListener() {
            @Override
            public void sureOnClick() {
                suerSeat();
                loadData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu,menu);
        return true;
    }



}
