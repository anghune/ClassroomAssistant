package com.lzp.classroomassistant.message.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.message.adapter.base.BaseViewHolder;
import com.lzp.classroomassistant.util.PicassoUtils;

import java.text.SimpleDateFormat;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * 接收到的文本类型
 */
public class ReceiveTextHolder extends BaseViewHolder {

  @InjectView(R.id.iv_avatar)
  protected ImageView iv_avatar;

  @InjectView(R.id.tv_time)
  protected TextView tv_time;

  @InjectView(R.id.tv_message)
  protected TextView tv_message;

  public ReceiveTextHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.item_chat_received_message,onRecyclerViewListener);
  }

  @OnClick({R.id.iv_avatar})
  public void onAvatarClick(View view) {

  }

  @Override
  public void bindData(Object o) {
    final BmobIMMessage message = (BmobIMMessage)o;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    String time = dateFormat.format(message.getCreateTime());
    tv_time.setText(time);
    final BmobIMUserInfo info = message.getBmobIMUserInfo();
//    ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.drawable.head);
    PicassoUtils.loadImage(info.getAvatar(),iv_avatar,R.drawable.icon_head);
    String content =  message.getContent();
    tv_message.setText(content);
    iv_avatar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toast("点击" + info.getName() + "的头像");
      }
    });
    tv_message.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          toast("点击"+message.getContent());
          if(onRecyclerViewListener!=null){
            onRecyclerViewListener.onItemClick(getAdapterPosition());
          }
        }
    });

    tv_message.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          if (onRecyclerViewListener != null) {
            onRecyclerViewListener.onItemLongClick(getAdapterPosition());
          }
          return true;
        }
    });
  }

  public void showTime(boolean isShow) {
    tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }
}