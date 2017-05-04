package com.lzp.classroomassistant.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.User;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WangChunhe on 2017/4/26.
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {

    private ArrayList<User> mUserList;
    private Listener mListener;

    public MemberListAdapter(ArrayList<User> userList, Listener listener) {
        mUserList = userList;
        mListener = listener;
    }

    @Override
    public MemberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_child,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberListAdapter.ViewHolder holder, final int position) {
        holder.mMemberIdTxt.setText(mUserList.get(position).getUsername());
        holder.mNameTxt.setText(mUserList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.itemOnClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.headImage)
        CircleImageView mHeadImage;
        @InjectView(R.id.nameTxtId)
        TextView mNameTxt;
        @InjectView(R.id.memberTxtId)
        TextView mMemberIdTxt;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    interface Listener{
        void itemOnClick(View view, int pos);
    }
}
