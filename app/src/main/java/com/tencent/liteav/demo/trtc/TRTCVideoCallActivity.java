package com.tencent.liteav.demo.trtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.liteav.demo.R;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
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
    List<ImUserBean> userBeanList;
    int userIndex = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        //获取前一个页面得到的进房参数
        Intent intent       = getIntent();
        int sdkAppId        = intent.getIntExtra("sdkAppId", 0);
        int roomId          = intent.getIntExtra("roomId", 0);
        String selfUserId   = intent.getStringExtra("userId");
        String userSig      = intent.getStringExtra("userSig");

        initTencentIM();


        userBeanList = new ArrayList<>();
        userBeanList.add(new ImUserBean("user1","eJxlz11PgzAUgOF7fkXD7Yy2hQoz2cVgOFm2Kbg53E3TQJG6yUcp07H4342oEeO5fd6Tk3PSAAD6an5-zuK4aHJF1bHkOrgCOtTPfrEsRUKZooZM-iF-K4XklKWKyw4RIQRD2G9EwnMlUvFdNDWXqMd1sqPdja99E0JkX1rmn0Q8dbjwAtf3Zm01bx6Ub2e42hZVHm1ESjbVTRaEbI-DVwTb9jDzvb07Fs762robZukEsgvbedzubsMojQexk-NndzFeTqdqMFm50TFZB6NR76QSL-znIWgjy8RGTw9c1qLIuwBDRBA24Ofo2rv2ASUvXOk_"));
        userBeanList.add(new ImUserBean("user2","eJxlz1FPgzAQwPF3PkXTV4xry8qIiQ*IbC7RqEFJ5IWw0s0bGxTaMabxuxtRYxPv9fe-XO7dQQjhp9vkvBCiOdQmNyclMbpAmOCzP1QKyrwwudeV-1AOCjqZF2sjuxEp55wRYjdQytrAGn6Kg5Yds1iXVT7e*N6fEkIDfzaldgKbEe-i52i5YHWaXkEFk2M808dXYzaVf52kdD68zQfIXK5DwdzFQ6tCiENW7Sbi8bSKwM2yYAn7XbLNVi1to5f7plc9Yy3lw-YmFfGlddLAXv4*RALicd*3tJedhqYeA0Yop8wjX4OdD*cTTS9dlA__"));
        userBeanList.add(new ImUserBean("user3","eJxlz11PwjAUgOH7-YqmtxrXtXQbJl4gjM3IiDgXjDfLwgocDKO0ZeMj-nfj1NjEc-u8Jyfn4iCE8MskuykXi92hNoU5SYHRLcIEX-*hlFAVpSmYqv6hOEpQoiiXRqgOPc45JcRuoBK1gSX8FActFLNYV*9Fd*N7v0eIF-pBz7MTWHWYRvnw4T7OH9OWx3w95ZspVMfMF0yvA5eex5z6MFfDtDHPUc6SAUSDfRb2Y7GR56vgtBrPX11JG7edRUlSbpOntwwmM61Ho3Df6jvrpIGt*H2IhITxvm9pI5SGXd0FlHjco4x8DXY*nE*lsVxo"));
        userBeanList.add(new ImUserBean("user4","eJxlj01Pg0AURff8iglbjJmBgRYTF21l0RTTaikEN5MJM7WvWBiHoR80-ncjaiTxbc*5ufddLYSQncTrW14UdVsZZi5K2ugO2di**YNKgWDcME*Lf1CeFWjJ*NZI3UPi*76L8dABISsDW-gx2kZqOsCNKFnf8Z2nGJNxMKJkqMBrDx*jzWw*DUWUhXmio3k*IzHdKQfSNrnU5UP27oUdPapTkzjVYdlNIJqclvlLu0jTwNnvnczXci27uFqsuAPFbjXqnjmZbson*nam94NKAwf5*xAeYy9wh4OOUjdQV73gYuIT18NfZ1sf1icqTF1T"));

        // identifier为用户名，userSig 为用户登录凭证
        TIMManager.getInstance().login(userBeanList.get(userIndex).userName, userBeanList.get(userIndex).userSig, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.d("liuchuanzheng", "login failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                Log.d("liuchuanzheng", "login succ");
                Toast.makeText(TRTCVideoCallActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
            }
        });

        Button btn_call = findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //构造一条消息并添加一个文本内容
                TIMMessage msg = new TIMMessage();
                TIMTextElem elem = new TIMTextElem();
                elem.setText("请求视频呼叫");
                msg.addElement(elem);
                //获取单聊会话
                String peer = "user1";  //获取与用户 "test_user" 的会话
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


        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {//消息监听器
            @Override
            public boolean onNewMessages(List<TIMMessage> msgs) {//收到新消息
                //消息的内容解析请参考消息收发文档中的消息解析说明
                Log.i("liuchuanzheng", "收到新消息");
                Toast.makeText(TRTCVideoCallActivity.this,"收到新消息"+msgs,Toast.LENGTH_SHORT).show();

                for (TIMMessage msg : msgs) {
                    for(int i = 0; i < msg.getElementCount(); ++i) {
                        TIMElem elem = msg.getElement(i);

                        //获取当前元素的类型
                        TIMElemType elemType = elem.getType();
                        if (elemType == TIMElemType.Text) {
                            //处理文本消息
                            TIMTextElem timTextElem = (TIMTextElem)elem;
                            String text = timTextElem.getText();
                            if("请求视频呼叫".equals(text)){
                                showCalledView();
                            }else if("我接听".equals(text)){
                                Toast.makeText(TRTCVideoCallActivity.this,"下一步进房间"+msgs,Toast.LENGTH_SHORT).show();
                                onJoinRoom();
                            }
                        } else if (elemType == TIMElemType.Image) {
                            //处理图片消息
                        }//...处理更多消息
                    }
                }
                return true; //返回true将终止回调链，不再调用下一个新消息监听器
            }
        });
    }
    private void onJoinRoom() {
        final Intent intent = new Intent(this, TRTCMainActivity.class);
        intent.putExtra("roomId", 999);
        intent.putExtra("userId", userBeanList.get(userIndex).userName);
        intent.putExtra("sdkAppId", 1400186741);
        intent.putExtra("userSig",userBeanList.get(userIndex).userSig );
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
                String peer = "user2";  //获取与用户 "test_user" 的会话
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
                    }
                });
            }
        });
        dialog.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initTencentIM() {
        //初始化 SDK 基本配置
        TIMSdkConfig config = new TIMSdkConfig(1400186741)
//                .setAccoutType(accountType)
                .enableLogPrint(true)              // 是否在控制台打印Log?
                .setLogLevel(TIMLogLevel.DEBUG)    // Log输出级别（debug级别会很多）
                .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/justfortest/");
        // Log文件存放在哪里？

        //初始化 SDK
        TIMManager.getInstance().init(getApplicationContext(), config);
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
}
