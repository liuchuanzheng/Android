package com.tencent.liteav.demo.trtc;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 刘传政
 * @date 2019/2/20 0020 17:01
 * QQ:1052374416
 * 电话:18501231486
 * 作用:
 * 注意事项:
 */
public class Utils {

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLiveUrl(String bizid,String roomId,String userName){
        //拼接直播地址的规则
        //http://[bizid].liveplay.myqcloud.com/live/[bizid]_[streamid].flv
        /*
        * 假如，bizid = 8888， 房间号 = 12345，用户名 = userA， 用户当前使用了摄像头。

那么，streamid = MD5(12345_userA_main) = 8d0261436c375bb0dea901d86d7d70e8

所以，rexchang 这一路的腾讯云 CDN 观看地址（推荐 http-flv）是：

http://8888.liveplay.myqcloud.com/live/8888_8d0261436c375bb0dea901d86d7d70e8.flv*/
        String a = roomId+"_"+userName+"_"+"main";
        String streamid = md5(a);
        String url = "http://video.51vision.com/live/"+bizid+"_"+streamid+".flv";
//        String url = "http://video.51vision.com/live/"+streamid+".flv";
//        String url = "http://video.51vision.com/live/42155_7acce056e81b0dd1df073795c811e0ed.flv";

        return url;

    }
}
