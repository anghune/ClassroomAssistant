package com.lzp.classroomassistant.mainpages.common;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.User;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WangChunhe on 2017/4/18.
 */

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {
    private List<User> mUserList;

    public CheckListAdapter(List<User> userList) {
        mUserList = userList;
    }

    @Override
    public CheckListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_recycler,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckListAdapter.ViewHolder holder, int position) {

        holder.mNameTxt.setText(mUserList.get(position).getName());
        holder.mMunberTxt.setText(mUserList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.nameTxtId)
        TextView mNameTxt;
        @InjectView(R.id.munberTxtId)
        TextView mMunberTxt;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }
}
