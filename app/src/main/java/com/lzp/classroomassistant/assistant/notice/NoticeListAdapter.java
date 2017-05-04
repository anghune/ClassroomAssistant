package com.lzp.classroomassistant.assistant.notice;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.NoticeMember;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WangChunhe on 2017/5/1.
 */

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.ViewHolder> {
    private List<NoticeMember> mNoticeList;
    private Listener mListener;

    public NoticeListAdapter(List<NoticeMember> noticeList,Listener listener) {
        mNoticeList = noticeList;
        mListener = listener;
    }

    @Override
    public NoticeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice_recycler,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoticeListAdapter.ViewHolder holder, final int position) {
        NoticeMember member = mNoticeList.get(position);
        if (member.getMember() != null){
            holder.mNameTxt.setText(member.getNotice().getAuthor().getName());
            if (member.isConfirm()){
                holder.mStatusTxt.setText("已确认");
                holder.mStatusRelative.setBackgroundResource(R.color.white);
            } else {
                holder.mStatusTxt.setText("立即确认");
                holder.mStatusRelative.setBackgroundResource(R.color.colorPrimary);
            }
        } else {
            holder.mNameTxt.setText("我");
            if (member.isConfirm()){
                holder.mStatusTxt.setText("全部确认");
                holder.mStatusRelative.setBackgroundResource(R.color.white);
            }else {

                StringBuilder stringBuilder = new StringBuilder(String.valueOf(member.getCount())).append("人未确认");
                holder.mStatusTxt.setText(stringBuilder.toString());
                holder.mStatusRelative.setBackgroundResource(R.color.white);
            }
        }
        holder.mDateTxt.setText(pareDateString(member.getNotice().getNoticeId()));
        holder.mContentTxt.setText(member.getNotice().getContent());
        holder.mStatusRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.itemOnClick(v,position);
            }
        });
    }

    private String pareDateString(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long lt = new Long(s);
        java.util.Date date = new java.util.Date(lt);
        res = simpleDateFormat.format(date).replace("-","/");
        return res;
    }

    @Override
    public int getItemCount() {
        return mNoticeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.avatarImage)
        CircleImageView mAvatarImage;
        @InjectView(R.id.nameTxtId)
        TextView mNameTxt;
        @InjectView(R.id.contentTxtId)
        TextView mContentTxt;
        @InjectView(R.id.dateTxtId)
        TextView mDateTxt;
        @InjectView(R.id.statusRelativeId)
        RelativeLayout mStatusRelative;
        @InjectView(R.id.statusTxtId)
        TextView mStatusTxt;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    interface Listener{
        void itemOnClick(View view, int pos);
    }
}
