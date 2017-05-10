package com.lzp.classroomassistant.presonal.common;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.UserOrgan;
import com.lzp.classroomassistant.util.PicassoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WangChunhe on 2017/4/24.
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {
    private List<UserOrgan> mUserList;
    private List<UserOrgan> mCheckList = new ArrayList<>();
    private Listener mListener;

    public MemberListAdapter(List<UserOrgan> userList,Listener listener) {
        mUserList = userList;
        mListener = listener;
    }

    @Override
    public MemberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_member,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MemberListAdapter.ViewHolder holder, final int position) {
        final UserOrgan userOrgan = mUserList.get(position);
        holder.mNameTxt.setText(userOrgan.getUserName().getName());
        holder.mMemberIdTxt.setText(userOrgan.getUserName().getUsername());
        holder.mCheckBox.setTag(userOrgan);
        if (mCheckList.size()!= 0){
            holder.mCheckBox.setChecked(mCheckList.contains(userOrgan));
        } else {
            holder.mCheckBox.setChecked(false);
        }

        PicassoUtils.loadImage(userOrgan.getUserName().getAvatar(),holder.mAvatarImage,R.drawable.icon_head);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.d(" 确定 选择 ","  " + isChecked);
                    if (buttonView.getTag().equals(userOrgan)
                            && !mCheckList.contains(userOrgan)){
                        mCheckList.add(userOrgan);
                        Log.d("确定 选择 ",userOrgan.getUserName().getName());
                    }
                } else {
                    Log.d(" 取消 选择 ","  " + isChecked);
                    if (buttonView.getTag().equals(userOrgan)
                            && mCheckList.contains(userOrgan)){
                        mCheckList.remove(userOrgan);
                        Log.d("取消选择 ", userOrgan.getUserName().getName());
                    }
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onLongClick(v, position);
                return true;
            }
        });

    }

    public void clearCheckList(){
        mCheckList.clear();
        notifyDataSetChanged();
    }

    public List<UserOrgan> getCheckList() {
        return mCheckList;
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.nameTxtId)
        TextView mNameTxt;
        @InjectView(R.id.memberTxtId)
        TextView mMemberIdTxt;
        @InjectView(R.id.addMemberCheckBox)
        CheckBox mCheckBox;
        @InjectView(R.id.headerImageId)
        CircleImageView mAvatarImage;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    interface Listener{
        void onClick(View view, int pos);
        void onLongClick(View view, int pos);
    }
}
