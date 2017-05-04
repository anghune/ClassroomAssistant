package com.lzp.classroomassistant.assistant.notice;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WangChunhe on 2017/5/1.
 */

public class ReceiverListAdapter extends RecyclerView.Adapter<ReceiverListAdapter.ViewHolder> {
    private ArrayList<User> mUserList;
    private List<User> mCheckList = new ArrayList<>();

    public ReceiverListAdapter(ArrayList<User> userList) {
        mUserList = userList;
    }

    @Override
    public ReceiverListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice_member,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReceiverListAdapter.ViewHolder holder, final int position) {

        holder.mNameTxt.setText(mUserList.get(position).getName());
        holder.mMemberIdTxt.setText(mUserList.get(position).getUsername());
        holder.mCheckBox.setTag(mUserList.get(position));
        if (mCheckList.size()!= 0){
            holder.mCheckBox.setChecked(mCheckList.contains(mUserList.get(position)));
        } else {
            holder.mCheckBox.setChecked(false);
        }

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (buttonView.getTag().equals(mUserList.get(position))
                            && !mCheckList.contains(mUserList.get(position))){
                        mCheckList.add(mUserList.get(position));
                    }
                } else {
                    if (buttonView.getTag().equals(mUserList.get(position))
                            && mCheckList.contains(mUserList.get(position))){
                        mCheckList.remove(mUserList.get(position));
                    }
                }
            }
        });
    }

    public void clearCheckList(){
        mCheckList.clear();
        notifyDataSetChanged();
    }
    public ArrayList<User> getCheckList() {
        return (ArrayList<User>) mCheckList;
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
        @InjectView(R.id.checkboxId)
        CheckBox mCheckBox;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }
}
