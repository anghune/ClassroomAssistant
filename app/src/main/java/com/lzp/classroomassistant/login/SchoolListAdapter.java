package com.lzp.classroomassistant.login;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.College;
import com.lzp.classroomassistant.data.School;
import com.lzp.classroomassistant.util.Constant;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WangChunhe on 2017/4/14.
 */

public class SchoolListAdapter extends RecyclerView.Adapter<SchoolListAdapter.ViewHolder> {
    private List<School> mSchoolList;
    private List<College> mCollegeList;
    private ItemOnClick mListener;
    private int mType;

    public SchoolListAdapter(List<School> schoolList,List<College> collegeList,ItemOnClick listener, int type) {
        mType = type;
        mCollegeList = collegeList;
        mSchoolList = schoolList;
        mListener = listener;
    }

    public List<School> getSchoolList() {
        return mSchoolList;
    }

    public void setSchoolList(List<School> schoolList) {
        mSchoolList = schoolList;
    }

    public List<College> getCollegeList() {
        return mCollegeList;
    }

    public void setCollegeList(List<College> collegeList) {
        mCollegeList = collegeList;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    @Override
    public SchoolListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_school_recycler,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SchoolListAdapter.ViewHolder holder, final int position) {
        String content = "";
        if (mType == Constant.TYPE_SCHOOL_REGISTER || mType == Constant.TYPE_SCHOOL_LOGIN){
            content = mSchoolList.get(position).getName().toString();
        }else if (mType == Constant.TYPE_COLLEGE_REGISTER){
            content = mCollegeList.get(position).getName().toString();
        }
        holder.mTextView.setText(content);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v, position);
            }
        });
        holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onLongClick(v,position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mType == Constant.TYPE_SCHOOL_REGISTER || mType == Constant.TYPE_SCHOOL_LOGIN){
            return mSchoolList.size();
        } else if (mType == Constant.TYPE_COLLEGE_REGISTER){
            return mCollegeList.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.itemSchoolTxtId)
        TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }


    }

    interface ItemOnClick{
        void onClick(View view, int pos);
        void onLongClick(View view,int pos);
    }
}
