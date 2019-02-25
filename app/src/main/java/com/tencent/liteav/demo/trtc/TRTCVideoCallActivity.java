package com.tencent.liteav.demo.trtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.liteav.demo.R;
import com.tencent.liteav.demo.TRTCApplication;
import com.tencent.liteav.demo.trtc.adapter.MyAdapter;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 刘传政
 * @date 2019/2/20 0020 17:08
 * QQ:1052374416
 * 电话:18501231486
 * 作用:
 * 注意事项:
 */
public class TRTCVideoCallActivity extends Activity {
    TXLivePlayer mLivePlayer;
    TXCloudVideoView mView;
    int userIndex = 0;
    RecyclerView recyclerView;
    List<ImUserBean> imUserBeanUnloginList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_list_activity);
        //获取前一个页面得到的进房参数
        Intent intent       = getIntent();
        int sdkAppId        = intent.getIntExtra("sdkAppId", 0);
        int roomId          = intent.getIntExtra("roomId", 0);
        String selfUserId   = intent.getStringExtra("userId");
        String userSig      = intent.getStringExtra("userSig");
        initView();

    }

    private void initView() {
        recyclerView = findViewById(R.id.rv);
        //设置LayoutManager为LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        imUserBeanUnloginList = new ArrayList<>();
        imUserBeanUnloginList.addAll(TRTCApplication.imUserBeanList);
        Iterator<ImUserBean> iterator = imUserBeanUnloginList.iterator();
        while (iterator.hasNext()){
            ImUserBean imUserBean = iterator.next();
            if (TRTCApplication.loginImUserBean.userName.equals(imUserBean.userName)){
                iterator.remove();
            }
        }
        //设置Adapter
        MyAdapter myAdapter = new MyAdapter(this,imUserBeanUnloginList );
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                userIndex = position;
                //构造一条消息并添加一个文本内容
                TIMMessage msg = new TIMMessage();
                TIMTextElem elem = new TIMTextElem();
                elem.setText("请求视频呼叫");
                msg.addElement(elem);
                //获取单聊会话
                String peer = imUserBeanUnloginList.get(position).userName;  //获取与用户 "test_user" 的会话
                TIMConversation conversation = TIMManager.getInstance().getConversation(
                        TIMConversationType.C2C,    //会话类型：单聊
                        peer);                      //会话对方用户帐号//对方ID
                //发送消息
                conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int code, String desc) {//发送消息失败
                        //错误码 code 和错误描述 desc，可用于定位请求失败原因
                        Log.d("liuchuanzheng", "send message failed. code: " + code + " errmsg: " + desc);
                    }

                    @Override
                    public void onSuccess(TIMMessage msg) {//发送消息成功
                        Log.e("liuchuanzheng", "SendMsg ok");
                        Toast.makeText(TRTCVideoCallActivity.this,"发送消息成功"+msg,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void onJoinRoom() {
        final Intent intent = new Intent(this, TRTCCallVideoActivity.class);
        intent.putExtra("roomId", 999);
        intent.putExtra("userId", TRTCApplication.loginImUserBean.userName);
        intent.putExtra("sdkAppId", 1400186741);
        intent.putExtra("userSig",TRTCApplication.loginImUserBean.userSig );
        startActivity(intent);

    }
    private void showCalledView() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("提示");
        dialog.setMessage("您被邀请加入视频通话");
        dialog.setCancelable(false);
        dialog.setPositiveButton("接听", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //构造一条消息并添加一个文本内容
                TIMMessage msg = new TIMMessage();
                TIMTextElem elem = new TIMTextElem();
                elem.setText("我接听");
                msg.addElement(elem);
                //获取单聊会话
                String peer = imUserBeanUnloginList.get(userIndex).userName;  //获取与用户 "test_user" 的会话
                TIMConversation conversation = TIMManager.getInstance().getConversation(
                        TIMConversationType.C2C,    //会话类型：单聊
                        peer);                      //会话对方用户帐号//对方ID
                //发送消息
                conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int code, String desc) {//发送消息失败
                        //错误码 code 和错误描述 desc，可用于定位请求失败原因
                        Log.d("liuchuanzheng", "send message failed. code: " + code + " errmsg: " + desc);

                    }

                    @Override
                    public void onSuccess(TIMMessage msg) {//发送消息成功
                        Log.e("liuchuanzheng", "SendMsg ok");
                        Toast.makeText(TRTCVideoCallActivity.this,"发送消息成功"+msg,Toast.LENGTH_SHORT).show();
                        onJoinRoom();
                        RingtoneUtil2.stopPlay();
                    }
                });
            }
        });
        dialog.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                RingtoneUtil2.stopPlay();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        RingtoneUtil2.endRingAndVibrator();
                        return true;
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        RingtoneUtil2.endRingAndVibrator();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
        dialog.show();
        RingtoneUtil2.startRingAndVibrator(this);

    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                RingtoneUtil2.endRingAndVibrator();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                RingtoneUtil2.endRingAndVibrator();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusEvent(MessageEvent messageEvent) {
        for (TIMMessage msg : messageEvent.msgs) {
            for(int i = 0; i < msg.getElementCount(); ++i) {
                TIMElem elem = msg.getElement(i);

                //获取当前元素的类型
                TIMElemType elemType = elem.getType();
                if (elemType == TIMElemType.Text) {
                    //处理文本消息
                    TIMTextElem timTextElem = (TIMTextElem)elem;
                    String text = timTextElem.getText();
                    if("请求视频呼叫".equals(text)){
                        Intent intent = new Intent(TRTCVideoCallActivity.this, CallActivity.class);
                        startActivity(intent);
                    }
                } else if (elemType == TIMElemType.Image) {
                    //处理图片消息
                }//...处理更多消息
            }
        }
    }

}
