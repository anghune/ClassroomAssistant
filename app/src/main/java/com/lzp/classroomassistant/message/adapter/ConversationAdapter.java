package com.lzp.classroomassistant.message.adapter;

import android.content.Context;
import android.view.View;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Conversation;
import com.lzp.classroomassistant.message.adapter.base.BaseRecyclerAdapter;
import com.lzp.classroomassistant.message.adapter.base.BaseRecyclerHolder;
import com.lzp.classroomassistant.message.adapter.base.IMutlipleItem;
import com.lzp.classroomassistant.util.TimeUtil;

import java.util.Collection;



/**
 * 使用进一步封装的Conversation,教大家怎么自定义会话列表
 * @author smile
 */
public class ConversationAdapter extends BaseRecyclerAdapter<Conversation> {

    public ConversationAdapter(Context context, IMutlipleItem<Conversation> items, Collection<Conversation> datas) {
        super(context,items,datas);
    }

    @Override
    public void bindView(BaseRecyclerHolder holder, Conversation conversation, int position) {
        holder.setText(R.id.tv_recent_msg,conversation.getLastMessageContent());
        holder.setText(R.id.tv_recent_time, TimeUtil.getChatTime(false,conversation.getLastMessageTime()));
        //会话图标
        Object obj = conversation.getAvatar();
        if(obj instanceof String){
            String avatar=(String)obj;
            holder.setImageView(avatar, R.drawable.head, R.id.iv_recent_avatar);
        }else{
            int defaultRes = (int)obj;
            holder.setImageView(null, defaultRes, R.id.iv_recent_avatar);
        }
        //会话标题
        holder.setText(R.id.tv_recent_name, conversation.getcName());
        //查询指定未读消息数
        long unread = conversation.getUnReadCount();
        if(unread>0){
            holder.setVisible(R.id.tv_recent_unread, View.VISIBLE);
            holder.setText(R.id.tv_recent_unread, String.valueOf(unread));
        }else{
            holder.setVisible(R.id.tv_recent_unread, View.GONE);
        }
    }
}