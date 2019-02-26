package com.tencent.liteav.demo.trtc;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.liteav.demo.R;
import com.tencent.liteav.demo.TRTCApplication;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Module:   TRTCNewActivity
 *
 * Function: 该界面可以让用户输入一个【房间号】和一个【用户名】
 *
 * Notice:
 *
 *  （1）房间号为数字类型，用户名为字符串类型
 *
 *  （2）在真实的使用场景中，房间号大多不是用户手动输入的，而是系统分配的，
 *       比如视频会议中的会议号是会控系统提前预定好的，客服系统中的房间号也是根据客服员工的工号决定的。
 */
public class TRTCNewActivity extends Activity {
    private final static int REQ_PERMISSION_CODE = 0x1000;

    private TRTCGetUserIDAndUserSig mUserInfoLoader;
     EditText etRoomId;
     EditText etUserId;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity);


        etRoomId = (EditText)findViewById(R.id.et_room_name);
        etRoomId.setText("999");


        etUserId = (EditText)findViewById(R.id.et_user_name);
        etUserId.setText(String.valueOf(System.currentTimeMillis() % 1000000));

        TextView tvEnterRoom = (TextView)findViewById(R.id.tv_enter);
        tvEnterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int roomId = 123;
                try{
                    roomId = Integer.valueOf(etRoomId.getText().toString());
                }catch (Exception e){
                    Toast.makeText(getContext(), "请输入有效的房间号", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String userId = etUserId.getText().toString();
                if(TextUtils.isEmpty(userId)) {
                    Toast.makeText(getContext(), "请输入有效的用户名", Toast.LENGTH_SHORT).show();
                    return;
                }

                onJoinRoom(roomId, userId);
            }
        });
        videoCall();
        live();
        watchLive();


        // 如果配置有config文件，则从config文件中选择userId
        mUserInfoLoader = new TRTCGetUserIDAndUserSig(this);
        final ArrayList<String> userIds = mUserInfoLoader.getUserIdFromConfig();
        if (userIds != null && userIds.size() > 0) {
            TRTCUserSelectDialog dialog = new TRTCUserSelectDialog(getContext(), mUserInfoLoader.getUserIdFromConfig());
            dialog.setTitle("请选择登录的用户:");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnItemClickListener(new TRTCUserSelectDialog.onItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    final EditText etUserId = (EditText)findViewById(R.id.et_user_name);
                    etUserId.setText(userIds.get(position));
                    etUserId.setEnabled(false);
                    TRTCApplication.loginImUserBean = TRTCApplication.imUserBeanList.get(position);
                    initTencentIM();
                }
            });
            dialog.show();
        }
        else {
            showAlertDialog();
        }

        // 申请动态权限
        checkPermission();
    }
    private void videoCall() {
        TextView tv_videoCall = (TextView)findViewById(R.id.tv_videoCall);
        tv_videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int roomId = 123;
                try{
                    roomId = Integer.valueOf(etRoomId.getText().toString());
                }catch (Exception e){
                    Toast.makeText(getContext(), "请输入有效的房间号", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String userId = etUserId.getText().toString();
                if(TextUtils.isEmpty(userId)) {
                    Toast.makeText(getContext(), "请输入有效的用户名", Toast.LENGTH_SHORT).show();
                    return;
                }

                onVideoCall(roomId, userId);
            }
        });
    }

    private void watchLive() {
        TextView tv_watchLive = (TextView)findViewById(R.id.tv_watchLive);
        tv_watchLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int roomId = 123;
                try{
                    roomId = Integer.valueOf(etRoomId.getText().toString());
                }catch (Exception e){
                    Toast.makeText(getContext(), "请输入有效的房间号", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String userId = etUserId.getText().toString();
                if(TextUtils.isEmpty(userId)) {
                    Toast.makeText(getContext(), "请输入有效的用户名", Toast.LENGTH_SHORT).show();
                    return;
                }

//                onWatchLive(roomId, userId);

                final Intent intent = new Intent(getContext(), LivingUsersActivity.class);
                intent.putExtra("roomId", roomId);
                intent.putExtra("userId", userId);
                final int sdkAppId = mUserInfoLoader.getSdkAppIdFromConfig();
                if (sdkAppId > 0) {
                    //（1） 从控制台获取的 json 文件中，简单获取几组已经提前计算好的 userid 和 usersig
                    ArrayList<String> userIdList = mUserInfoLoader.getUserIdFromConfig();
                    ArrayList<String> userSigList = mUserInfoLoader.getUserSigFromConfig();
                    int position = userIdList.indexOf(userId);
                    String userSig = "";
                    if (userSigList != null && userSigList.size() > position) {
                        userSig = userSigList.get(position);
                    }
                    intent.putExtra("sdkAppId", sdkAppId);
                    intent.putExtra("userSig", userSig);
                    startActivity(intent);
                }
            }
        });
    }

    private void live() {
        TextView tv_live = (TextView)findViewById(R.id.tv_live);
        tv_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int roomId = 123;
                try{
                    roomId = Integer.valueOf(etRoomId.getText().toString());
                }catch (Exception e){
                    Toast.makeText(getContext(), "请输入有效的房间号", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String userId = etUserId.getText().toString();
                if(TextUtils.isEmpty(userId)) {
                    Toast.makeText(getContext(), "请输入有效的用户名", Toast.LENGTH_SHORT).show();
                    return;
                }

                onLive(roomId, userId);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     *  Function: 读取用户输入，并创建（或加入）音视频房间
     *
     *  此段示例代码最主要的作用是组装 TRTC SDK 进房所需的 TRTCParams
     *
     *  TRTCParams.sdkAppId => 可以在腾讯云实时音视频控制台（https://console.cloud.tencent.com/rav）获取
     *  TRTCParams.userId   => 此处即用户输入的用户名，它是一个字符串
     *  TRTCParams.roomId   => 此处即用户输入的音视频房间号，比如 125
     *  TRTCParams.userSig  => 此处示例代码展示了两种获取 usersig 的方式，一种是从【控制台】获取，一种是从【服务器】获取
     *
     * （1）控制台获取：可以获得几组已经生成好的 userid 和 usersig，他们会被放在一个 json 格式的配置文件中，仅适合调试使用
     * （2）服务器获取：直接在服务器端用我们提供的源代码，根据 userid 实时计算 usersig，这种方式安全可靠，适合线上使用
     *
     *  参考文档：https://cloud.tencent.com/document/product/647/17275
     */
    private void onJoinRoom(int roomId, final String userId) {
        final Intent intent = new Intent(getContext(), TRTCMainActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("userId", userId);
        final int sdkAppId = mUserInfoLoader.getSdkAppIdFromConfig();
        if (sdkAppId > 0) {
            //（1） 从控制台获取的 json 文件中，简单获取几组已经提前计算好的 userid 和 usersig
            ArrayList<String> userIdList = mUserInfoLoader.getUserIdFromConfig();
            ArrayList<String> userSigList = mUserInfoLoader.getUserSigFromConfig();
            int position = userIdList.indexOf(userId);
            String userSig = "";
            if (userSigList != null && userSigList.size() > position) {
                userSig = userSigList.get(position);
            }
            intent.putExtra("sdkAppId", sdkAppId);
            intent.putExtra("userSig", userSig);
            startActivity(intent);
        } else {
            //（2） 通过 http 协议向一台服务器获取 userid 对应的 usersig
            mUserInfoLoader.getUserSigFromServer(1400037025, roomId, userId, "12345678", new TRTCGetUserIDAndUserSig.IGetUserSigListener() {
                @Override
                public void onComplete(String userSig, String errMsg) {
                    if (!TextUtils.isEmpty(userSig)) {
                        intent.putExtra("sdkAppId", 1400037025);
                        intent.putExtra("userSig", userSig);
                        startActivity(intent);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "从服务器获取userSig失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
    private void onLive(int roomId, final String userId) {
        final Intent intent = new Intent(getContext(), TRTCLiveActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("userId", userId);
        final int sdkAppId = mUserInfoLoader.getSdkAppIdFromConfig();
        if (sdkAppId > 0) {
            //（1） 从控制台获取的 json 文件中，简单获取几组已经提前计算好的 userid 和 usersig
            ArrayList<String> userIdList = mUserInfoLoader.getUserIdFromConfig();
            ArrayList<String> userSigList = mUserInfoLoader.getUserSigFromConfig();
            int position = userIdList.indexOf(userId);
            String userSig = "";
            if (userSigList != null && userSigList.size() > position) {
                userSig = userSigList.get(position);
            }
            intent.putExtra("sdkAppId", sdkAppId);
            intent.putExtra("userSig", userSig);
            startActivity(intent);
        }
    }

    private void onVideoCall(int roomId, final String userId) {
        final Intent intent = new Intent(getContext(), TRTCVideoCallActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("userId", userId);
        final int sdkAppId = mUserInfoLoader.getSdkAppIdFromConfig();
        if (sdkAppId > 0) {
            //（1） 从控制台获取的 json 文件中，简单获取几组已经提前计算好的 userid 和 usersig
            ArrayList<String> userIdList = mUserInfoLoader.getUserIdFromConfig();
            ArrayList<String> userSigList = mUserInfoLoader.getUserSigFromConfig();
            int position = userIdList.indexOf(userId);
            String userSig = "";
            if (userSigList != null && userSigList.size() > position) {
                userSig = userSigList.get(position);
            }
            intent.putExtra("sdkAppId", sdkAppId);
            intent.putExtra("userSig", userSig);
            startActivity(intent);
        }
    }

    private void onWatchLive(int roomId, final String userId) {
        final Intent intent = new Intent(getContext(), TRTCWatchLiveActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("userId", userId);
        final int sdkAppId = mUserInfoLoader.getSdkAppIdFromConfig();
        if (sdkAppId > 0) {
            //（1） 从控制台获取的 json 文件中，简单获取几组已经提前计算好的 userid 和 usersig
            ArrayList<String> userIdList = mUserInfoLoader.getUserIdFromConfig();
            ArrayList<String> userSigList = mUserInfoLoader.getUserSigFromConfig();
            int position = userIdList.indexOf(userId);
            String userSig = "";
            if (userSigList != null && userSigList.size() > position) {
                userSig = userSigList.get(position);
            }
            intent.putExtra("sdkAppId", sdkAppId);
            intent.putExtra("userSig", userSig);
            startActivity(intent);
        }
    }

    private Context getContext(){
        return this;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("注意")
                .setMessage("读取配置文件失败，请在【控制台】->【快速上手】中生成配置内容复制到config.json文件");
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }
    //////////////////////////////////    动态权限申请   ////////////////////////////////////////

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.VIBRATE)) {
                permissions.add(Manifest.permission.VIBRATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(TRTCNewActivity.this,
                        (String[]) permissions.toArray(new String[0]),
                        REQ_PERMISSION_CODE);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                for (int ret : grantResults) {
                    if (PackageManager.PERMISSION_GRANTED != ret) {
                        Toast.makeText(getContext(), "用户没有允许需要的权限，使用可能会受到限制！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
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

        TIMManager.getInstance().login(TRTCApplication.loginImUserBean.userName, TRTCApplication.loginImUserBean.userSig, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.d("liuchuanzheng", "login failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                Log.d("liuchuanzheng", "login succ");
                Toast.makeText(TRTCNewActivity.this,"im登录成功",Toast.LENGTH_SHORT).show();
            }
        });

        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {//消息监听器
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public boolean onNewMessages(List<TIMMessage> msgs) {//收到新消息

                    //当前界面 不用发消息了,就是自己用
                    for (TIMMessage msg : msgs) {
                        for (int i = 0; i < msg.getElementCount(); ++i) {
                            TIMElem elem = msg.getElement(i);

                            //获取当前元素的类型
                            TIMElemType elemType = elem.getType();
                            if (elemType == TIMElemType.Text) {
                                //处理文本消息
                                TIMTextElem timTextElem = (TIMTextElem) elem;
                                String text = timTextElem.getText();
                                if ("请求视频呼叫".equals(text)) {
                                    Intent intent = new Intent(TRTCNewActivity.this, CallActivity.class);
                                    intent.putExtra("type", "called");
                                    intent.putExtra("username", msg.getSender());
                                    startActivity(intent);
                                }else if("我接听".equals(text)){
                                    if (CallActivity.instance != null) {
                                        if ( !CallActivity.instance.isDestroyed() && !CallActivity.instance.isFinishing()) {
                                            CallActivity.instance.finish();
                                        }
                                    }

                                    final Intent intent = new Intent(TRTCNewActivity.this, TRTCCallVideoActivity.class);
                                    intent.putExtra("roomId", 999);
                                    intent.putExtra("userId", TRTCApplication.loginImUserBean.userName);
                                    intent.putExtra("sdkAppId", 1400186741);
                                    intent.putExtra("userSig",TRTCApplication.loginImUserBean.userSig );
                                    intent.putExtra("toUserName",msg.getSender());
                                    startActivity(intent);
                                }else if("挂断".equals(text)){
                                    //消息的内容解析请参考消息收发文档中的消息解析说明
                                    MessageEvent messageEvent = new MessageEvent();
                                    messageEvent.msgs = msgs;
                                    EventBus.getDefault().post(messageEvent);
                                }else if("呼叫界面挂断".equals(text)){
                                    if (CallActivity.instance != null) {
                                        if ( !CallActivity.instance.isDestroyed() && !CallActivity.instance.isFinishing()) {
                                            CallActivity.instance.finish();
                                            Toast.makeText(TRTCNewActivity.this,"对方已挂断",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }else if("拒绝".equals(text)){
                                    if (CallActivity.instance != null) {
                                        if ( !CallActivity.instance.isDestroyed() && !CallActivity.instance.isFinishing()) {
                                            CallActivity.instance.finish();
                                            Toast.makeText(TRTCNewActivity.this,"对方已拒绝",Toast.LENGTH_SHORT).show();
                                        }
                                    }
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

}
