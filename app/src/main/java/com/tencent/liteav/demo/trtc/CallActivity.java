package com.tencent.liteav.demo.trtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.liteav.demo.R;
import com.tencent.liteav.demo.TRTCApplication;

/**
 * @author 刘传政
 * @date 2019/2/25 0025 17:38
 * QQ:1052374416
 * 电话:18501231486
 * 作用:
 * 注意事项:
 */
public class CallActivity extends Activity {
    RelativeLayout rl_call;
    RelativeLayout rl_called;
    String type;
    String username;
    TextView tv_answer;
    TextView tv_deny;
    TextView tv_hangup;
    public static CallActivity instance = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        Intent intent       = getIntent();
        type = intent.getStringExtra("type");
        username = intent.getStringExtra("username");
        initView();
        instance = this;
    }

    private void initView() {
        rl_call = findViewById(R.id.rl_call);
        rl_called = findViewById(R.id.rl_called);
        if("call".equals(type)){
            rl_call.setVisibility(View.VISIBLE);
            rl_called.setVisibility(View.INVISIBLE);
        }else if("called".equals(type)){
            rl_call.setVisibility(View.INVISIBLE);
            rl_called.setVisibility(View.VISIBLE);
            RingtoneUtil2.startRingAndVibrator(this);
        }

        tv_answer = findViewById(R.id.tv_answer);
        tv_deny = findViewById(R.id.tv_deny);
        tv_hangup = findViewById(R.id.tv_hangup);
        tv_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer();
            }
        });
        tv_hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_hangup();
            }
        });
        tv_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_deny();
            }
        });
    }

    private void tv_deny() {
        //拒绝
        //发出挂断通知.
        //构造一条消息并添加一个文本内容
        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText("拒绝");
        msg.addElement(elem);
        //获取单聊会话
        String peer = username;  //获取与用户 "test_user" 的会话
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
            }
        });
        Toast.makeText(this,"拒绝",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void tv_hangup() {
        //挂断
        //发出挂断通知.
        //构造一条消息并添加一个文本内容
        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText("呼叫界面挂断");
        msg.addElement(elem);
        //获取单聊会话
        String peer = username;  //获取与用户 "test_user" 的会话
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
            }
        });
        Toast.makeText(this,"已挂断",Toast.LENGTH_SHORT).show();
        finish();

    }

    private void answer() {
        //构造一条消息并添加一个文本内容
        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText("我接听");
        msg.addElement(elem);
        //获取单聊会话
        String peer = username;  //获取与用户 "test_user" 的会话
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
                onJoinRoom();
                RingtoneUtil2.endRingAndVibrator();
            }
        });
    }
    private void onJoinRoom() {
        final Intent intent = new Intent(this, TRTCCallVideoActivity.class);
        intent.putExtra("roomId", 999);
        intent.putExtra("userId", TRTCApplication.loginImUserBean.userName);
        intent.putExtra("sdkAppId", 1400186741);
        intent.putExtra("userSig",TRTCApplication.loginImUserBean.userSig );
        intent.putExtra("toUserName",username );
        startActivity(intent);
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        RingtoneUtil2.endRingAndVibrator();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RingtoneUtil2.endRingAndVibrator();
        instance = null;
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
}
