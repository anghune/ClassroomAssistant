package com.lzp.classroomassistant.mainpages.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Course;
import com.lzp.classroomassistant.util.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WangChunhe on 2017/4/12.
 */

public class CourseListAdapter extends BaseAdapter {
    private List<Course> mCourseList;
    private List<Course> mAllCourseList;
    private Context mContext;
    private AddCouresListener mListener;

    public CourseListAdapter(List<Course> courseList,List<Course> allCourseList,Context context,AddCouresListener listener) {
        mCourseList = courseList;
        mAllCourseList = allCourseList;
        mContext = context;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mAllCourseList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAllCourseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final HolderView holderView;
        if (convertView != null){
            holderView = (HolderView) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_infor_course,parent,false);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        }
        holderView.mCourseNameTxt.setText(mAllCourseList.get(position).getCourseName());
        String classroom = String.format(mContext.getString(R.string.classroom),mAllCourseList.get(position).getClassRoom());
        holderView.mClassRoomTxt.setText(classroom);
        String time =  String.format(mContext.getString(R.string.coursetiem), "周"+ Utils.intToZH(mAllCourseList.get(position).getWeek()));
        int end = mAllCourseList.get(position).getSection() + mAllCourseList.get(position).getSectionSpan() - 1;
        StringBuilder sb = new StringBuilder(time).append(mAllCourseList.get(position).getSection()).append("-").append(end).append("节");
        holderView.mCourseTimeTxt.setText(sb);
        holderView.mTeacherTxt.setText(String.format(mContext.getString(R.string.teacher),mAllCourseList.get(position).getTeacher().getName()));

        if (hasCourse(position)){
            holderView.mIsSelectTxt.setText(mContext.getString(R.string.remove_course));
            holderView.mIsSelectTxt.setBackgroundResource(R.drawable.unselect_course_bg);
        } else {
            holderView.mIsSelectTxt.setText(mContext.getString(R.string.add_course));
            holderView.mIsSelectTxt.setBackgroundResource(R.drawable.select_course_bg);
        }
        holderView.mIsSelectTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v,position);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onLongClick(v, position);
                return false;
            }
        });
        return convertView;
    }

    class HolderView{
        @InjectView(R.id.courseNameTxtId)
        TextView mCourseNameTxt;
        @InjectView(R.id.isSelectTxtId)
        TextView mIsSelectTxt;
        @InjectView(R.id.classroomTxtId)
        TextView mClassRoomTxt;
        @InjectView(R.id.courseTimeTxtId)
        TextView mCourseTimeTxt;
        @InjectView(R.id.teacherTxtId)
        TextView mTeacherTxt;

        public HolderView(View view) {
            ButterKnife.inject(this,view);
        }
    }

    private boolean hasCourse(int pos){
        Course course = mAllCourseList.get(pos);
        if (mCourseList.contains(course)){
            return true;
        } else {
            return false;
        }
    }

    interface AddCouresListener{
        void onClick(View view, int pos);
        void onLongClick(View view, int pos);
    }
}
