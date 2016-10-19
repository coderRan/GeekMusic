package com.zdr.geekmusic.netUtils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 歌词网络请求
 * Created by zdr on 16-9-18.
 */
public class VolleyManager {
    private static RequestQueue requestQueue;

    /**
     * 初始化vollye请求队列
     */
    public static void initVolleyManager(Context context){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }
    }

    /**
     * 创建请求
     * @param url 请求地址
     * @return 返回创建轻轻的便捷类
     */
    public static LrcRequest.Builder get(String url){

        return new LrcRequest.Builder()
                .setMethod(Request.Method.GET)
                .setUrl(url);
    }
    /**
     * 返回一个post请求
     * 用于创建post请求
     * @param url 请求的url
     * @return 创建该请求的便捷类
     */
    public static LrcRequest.Builder post(String url){
        return new LrcRequest.Builder()
                .setMethod(Request.Method.POST)
                .setUrl(url);
    }
    /**
     * 启动一个请求，将该请求加入到请求队列
     * @param request 待加入的请求
     */
    public static void start(LrcRequest request){
        if(request!=null)
            requestQueue.add(request);
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

}
