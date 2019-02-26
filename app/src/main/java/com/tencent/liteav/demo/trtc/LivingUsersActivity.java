package com.tencent.liteav.demo.trtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tencent.liteav.demo.R;
import com.tencent.liteav.demo.TRTCApplication;
import com.tencent.liteav.demo.trtc.adapter.LivingUsersAdapter;

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
public class LivingUsersActivity extends Activity {
    int roomId;
    int sdkAppId;
    RecyclerView recyclerView;
    List<ImUserBean> imUserBeanUnloginList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.living_users_activity);
        //获取前一个页面得到的进房参数
        Intent intent       = getIntent();

        sdkAppId = intent.getIntExtra("sdkAppId", 0);

        roomId = intent.getIntExtra("roomId", 0);
        String selfUserId   = intent.getStringExtra("userId");
        String userSig      = intent.getStringExtra("userSig");
        initView();

    }

    private void initView() {
        recyclerView = findViewById(R.id.rv);
        //设置LayoutManager为LinearLayoutManager
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));

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
        LivingUsersAdapter myAdapter = new LivingUsersAdapter(this,imUserBeanUnloginList );
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new LivingUsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                onWatchLive(position);
            }
        });
    }

    private void onWatchLive(int position) {
        final Intent intent = new Intent(this, TRTCWatchLiveActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("userId", imUserBeanUnloginList.get(position).userName);
        intent.putExtra("sdkAppId", sdkAppId);
        intent.putExtra("userSig", imUserBeanUnloginList.get(position).userSig);
        startActivity(intent);

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
