package com.tencent.liteav.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.tencent.liteav.demo.trtc.ImUserBean;
import com.tencent.liteav.demo.trtc.TRTCGetUserIDAndUserSig;

import java.util.ArrayList;
import java.util.List;

public class TRTCApplication extends MultiDexApplication {
    public static List<ImUserBean> imUserBeanList;
    public static ImUserBean loginImUserBean;
    private static TRTCApplication mInstance;
    private Activity app_activity = null;

    @Override
    public void onCreate() {

        super.onCreate();
        readUsers();
        mInstance = this;
        initGlobeActivity();
    }

    private void readUsers() {
        // 如果配置有config文件，则从config文件中选择userId
        TRTCGetUserIDAndUserSig mUserInfoLoader = new TRTCGetUserIDAndUserSig(this);
        final ArrayList<String> userIds = mUserInfoLoader.getUserIdFromConfig();
        final ArrayList<String> userSigs = mUserInfoLoader.getUserSigFromConfig();

        imUserBeanList = new ArrayList<>();
        if (userIds != null && userIds.size() > 0) {
            for (String userId : userIds) {
                ImUserBean imUserBean = new ImUserBean("", "");
                imUserBean.userName = userId;
                imUserBeanList.add(imUserBean);
            }
            for (int i = 0; i < imUserBeanList.size(); i++) {
                imUserBeanList.get(i).userSig = userSigs.get(i);
            }
        }
    }

    private void initGlobeActivity() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                app_activity = activity;
                Log.e("onActivityCreated===", app_activity + "");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e("onActivityDestroyed===", app_activity + "");
            }

            /** Unused implementation **/
            @Override
            public void onActivityStarted(Activity activity) {
                app_activity = activity;
                Log.e("onActivityStarted===", app_activity + "");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                app_activity = activity;
                Log.e("onActivityResumed===", app_activity + "");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.e("onActivityPaused===", app_activity + "");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.e("onActivityStopped===", app_activity + "");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
        });
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static TRTCApplication getInstance() {
        return mInstance;
    }

    /**
     * 公开方法，外部可通过 MyApplication.getInstance().getCurrentActivity() 获取到当前最上层的activity
     */
    public Activity getCurrentActivity() {
        return app_activity;
    }

}
