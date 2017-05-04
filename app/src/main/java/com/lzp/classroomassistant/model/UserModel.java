package com.lzp.classroomassistant.model;

import android.util.Log;

import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.model.i.QueryUserListener;
import com.lzp.classroomassistant.model.i.UpdateCacheListener;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * @author :smile
 * @project:UserModel
 * @date :2016-01-22-18:09
 */
public class UserModel extends BaseModel {

    private final static String TAG = "UserModel";
    private static UserModel ourInstance = new UserModel();

    public static UserModel getInstance() {
        return ourInstance;
    }

    private UserModel() {}


    /**更新用户资料和会话资料
     * @param event
     * @param listener
     */
    public void updateUserInfo(MessageEvent event, final UpdateCacheListener listener){
        final BmobIMConversation conversation=event.getConversation();
        final BmobIMUserInfo info =event.getFromUserInfo();
        final BmobIMMessage msg =event.getMessage();
        String username =info.getName();
        String title =conversation.getConversationTitle();
        String name = conversation.getDraft();
        Log.i(TAG,"username " + username + "  title " + title + "   draft "+ name);
        //sdk内部，将新会话的会话标题用objectId表示，因此需要比对用户名和会话标题--单聊，后续会根据会话类型进行判断
//        if(!username.equals(title)) {
            UserModel.getInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                @Override
                public void done(User s, BmobException e) {
                    if(e==null){
                        String name =s.getUsername();
                        String avatar = s.getAvatar();
                        Log.i(TAG,"query success："+name+","+avatar);
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        conversation.setDraft(s.getName());
                        info.setName(name);
                        info.setAvatar(avatar);
                        //更新用户资料
                        BmobIM.getInstance().updateUserInfo(info);
                        //更新会话资料-如果消息是暂态消息，则不更新会话资料
                        if(!msg.isTransient()){
                            BmobIM.getInstance().updateConversation(conversation);
                        }
                    }else{
                        Log.e(TAG,e.getMessage());
                    }
                    listener.done(null);
                }
            });
//        }else{
//            listener.done(null);
//        }
    }

    /**查询用户信息
     * @param objectId
     * @param listener
     */
    public void queryUserInfo(String objectId, final QueryUserListener listener){
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(objectId, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null){
                    listener.done(user,e);
                } else {
                    listener.done(null,new BmobException(000, e.getMessage()));
                }

            }
        });
//        query.findObjects(new FindListener<User>() {
//            @Override
//            public void done(List<User> list, BmobException e) {
//                if (e != null){
//                    listener.done(list.get(0),null);
//                } else {
//                    listener.done(null,new BmobException(000, "查无此人"));
//                }
//            }
//        });
//        query.findObjects(getContext(), new FindListener<User>() {
//            @Override
//            public void onSuccess(List<User> list) {
//                if(list!=null && list.size()>0){
//                    listener.internalDone(list.get(0), null);
//                }else{
//                    listener.internalDone(new BmobException(000, "查无此人"));
//                }
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                listener.internalDone(new BmobException(i, s));
//            }
//        });
    }
}
