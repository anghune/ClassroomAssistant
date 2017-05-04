package com.lzp.classroomassistant.presonal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Organization;
import com.lzp.classroomassistant.data.User;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WangChunhe on 2017/4/25.
 */

public class GroupExpandAdapter extends BaseExpandableListAdapter {

    private ArrayList<ArrayList<User>> mMemberList;
    private ArrayList<Organization> mOrganArrayList;
    private Listener mListener;

    public GroupExpandAdapter(ArrayList<ArrayList<User>> memberList, ArrayList<Organization> organArrayList,Listener listener) {
        mMemberList = memberList;
        mOrganArrayList = organArrayList;
        mListener = listener;
    }

    @Override
    public int getGroupCount() {
        return mOrganArrayList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mMemberList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mOrganArrayList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mMemberList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView != null){
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_group,parent,false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        }
        //TODO 创建组织后，点击确定会Crash掉
        if (!mOrganArrayList.isEmpty()){
            groupViewHolder.mGroupNameTxt.setText(mOrganArrayList.get(groupPosition).getName());
        }
        groupViewHolder.mMamageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickMore(v, groupPosition);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder;
        if (convertView != null){
            viewHolder = (ChildViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_child,parent,false);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.mNameTxt.setText(mMemberList.get(groupPosition).get(childPosition).getName());
        viewHolder.mMemberId.setText(mMemberList.get(groupPosition).get(childPosition).getUsername());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

     class GroupViewHolder {
        @InjectView(R.id.label_expand_group)
        TextView mGroupNameTxt;
         @InjectView(R.id.manageImageId)
         ImageView mMamageImage;

        public GroupViewHolder(View view) {
            ButterKnife.inject(this,view);
        }
    }
     class ChildViewHolder {
        @InjectView(R.id.nameTxtId)
        TextView mNameTxt;
        @InjectView(R.id.memberTxtId)
        TextView mMemberId;
        @InjectView(R.id.headImage)
        CircleImageView mHeadImage;

        public ChildViewHolder(View view) {
            ButterKnife.inject(this,view);
        }
    }

    interface Listener{
        void onClickMore(View view, int groupPosition);
    }
}
