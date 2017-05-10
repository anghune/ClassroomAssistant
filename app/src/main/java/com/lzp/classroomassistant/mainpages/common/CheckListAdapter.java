package com.lzp.classroomassistant.mainpages.common;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.PicassoUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WangChunhe on 2017/4/18.
 */

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {
    private List<User> mUserList;
    private Listener mListener;
    private static final String TAG = "CheckListAdapter";
    public CheckListAdapter(List<User> userList, Listener listener) {
        mUserList = userList;
        mListener = listener;
    }

    @Override
    public CheckListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_recycler,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckListAdapter.ViewHolder holder, final int position) {
        User user =  mUserList.get(position);
        holder.mNameTxt.setText(user.getName());
        holder.mMunberTxt.setText(user.getUsername());
        PicassoUtils.loadImage(user.getAvatar(),holder.mAvatarImage,R.drawable.icon_head);
        if (user.getOrgan().equals(Constant.UNCHECK_CLASS)){
            holder.mMoreImage.setVisibility(View.INVISIBLE);
        } else if (user.getOrgan().equals(Constant.CHECK_CLASS)){
            holder.mMoreImage.setVisibility(View.VISIBLE);
        }
        if (user.getResult() == 1 ){
            holder.mResultImage.setBackgroundResource(R.drawable.icon_uncheck);
            holder.mResultImage.setVisibility(View.VISIBLE);
        } else if (user.getResult() == 2){
            holder.mResultImage.setBackgroundResource(R.drawable.icon_sure);
            holder.mResultImage.setVisibility(View.VISIBLE);
        } else {
            holder.mResultImage.setVisibility(View.INVISIBLE);
        }
        holder.mMoreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickMore(v,position);
            }
        });
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
        @InjectView(R.id.moreImageId)
        ImageView mMoreImage;
        @InjectView(R.id.avatarImageId)
        CircleImageView mAvatarImage;
        @InjectView(R.id.resultImageId)
        ImageView mResultImage;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    interface Listener{
        void onClickMore(View view, int pos);
    }
}
