package com.tencent.liteav.demo.trtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.liteav.demo.R;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * @author 刘传政
 * @date 2019/2/20 0020 17:08
 * QQ:1052374416
 * 电话:18501231486
 * 作用:
 * 注意事项:
 */
public class TRTCWatchLiveActivity  extends Activity {
    TXLivePlayer mLivePlayer;
    TXCloudVideoView mView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //应用运行时，保持屏幕高亮，不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.watchlive_activity);
        //获取前一个页面得到的进房参数
        Intent intent       = getIntent();
        int sdkAppId        = intent.getIntExtra("sdkAppId", 0);
        int roomId          = intent.getIntExtra("roomId", 0);
        String selfUserId   = intent.getStringExtra("userId");
        String userSig      = intent.getStringExtra("userSig");


        //mPlayerView 即 step1 中添加的界面 view

        mView = (TXCloudVideoView) findViewById(R.id.video_view);

        //创建 player 对象

        mLivePlayer = new TXLivePlayer(this);
        //关键 player 对象与界面 view
        mLivePlayer.setPlayerView(mView);
        String flvUrl = Utils.getLiveUrl("42155", roomId+"", selfUserId);
//        String flvUrl = "http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4_900.flv";
        mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 继续
        mLivePlayer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停
        mLivePlayer.pause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
        mView.onDestroy();
    }
}
