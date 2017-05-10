package com.lzp.classroomassistant.assistant.courseware;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Courseware;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WangChunhe on 2017/5/4.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private ArrayList<Courseware> mCoursewares;
    private Listener mListener;

    public FileListAdapter(ArrayList<Courseware> coursewares, Listener listener) {
        mCoursewares = coursewares;
        mListener = listener;
    }

    public ArrayList<Courseware> getCoursewares() {
        return mCoursewares;
    }

    public void setCoursewares(ArrayList<Courseware> coursewares) {
        mCoursewares = coursewares;
    }

    public void replaceData(ArrayList<Courseware> coursewares){
        setCoursewares(coursewares);
        notifyDataSetChanged();
    }
    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_recycler,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileListAdapter.ViewHolder holder, final int position) {
        Courseware courseware = mCoursewares.get(position);
        String filName = courseware.getName().trim().toString();
        Log.d("wang" , " 名称  " + filName);
        if (filName.endsWith(".zip")){
            holder.mIconImage.setBackgroundResource(R.drawable.zip);
        } else if (filName.endsWith(".doc") || filName.endsWith(".docx") ){
            holder.mIconImage.setBackgroundResource(R.drawable.doc);
        } else if (filName.endsWith(".ppt") ||filName.endsWith(".pptx")){
            holder.mIconImage.setBackgroundResource(R.drawable.ppt);
        } else if (filName.endsWith(".xls") || filName.endsWith(".xlsx") ){
            holder.mIconImage.setBackgroundResource(R.drawable.xls);
        } else if (filName.endsWith(".pdf")){
            holder.mIconImage.setBackgroundResource(R.drawable.pdf);
        }
        holder.mNameTxt.setText(courseware.getName());
        holder.mSizeTxt.setText(courseware.getSize());
        holder.mManageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickMore(v,position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onLongClick(v,position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCoursewares.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.iconImage)
        ImageView mIconImage;
        @InjectView(R.id.nameTxtId)
        TextView mNameTxt;
        @InjectView(R.id.sizeTxtId)
        TextView mSizeTxt;
        @InjectView(R.id.manageImageId)
        ImageView mManageView;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    interface Listener{
        void onClickMore(View view, int pos);
        void onLongClick(View view, int pos);
    }

}
