package com.example.nameconfirmeddemo.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Admin on 2017/6/26.
 *
 */

public class VolleyApi {

    private static VolleyApi mInstance;
    private Context mContext;
    private static RequestQueue mRequestQueue;
    private static final String host="http://auditphone.market.alicloudapi.com/phoneAudit";
    private static final String appcode="515d61e519d94aecac79ea1726fc97cd";


    private VolleyApi(Context context) {
        mContext=context.getApplicationContext();
        mRequestQueue= Volley.newRequestQueue(mContext);

    }

    public static VolleyApi getmInstance(Context context){
        if(mInstance==null){
            synchronized (VolleyApi.class){
                if (mInstance==null){
                    mInstance=new VolleyApi(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获得请求队列
     * @return
     */
    public static RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }

    /**
     * 身份证实名认证
     * @param cardNo 身份证号码
     * @param name 姓名
     * @param phone 手机号，
     * @param tag 请求标志
     * @param listener 响应成功监听回调
     * @param errorListener 响应失败监听回调
     * @return
     */
    public boolean getConfirmRequest(
            final String cardNo,
            final String name,
            final String phone,
            String tag,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener){

//        String url = host+"?idCard="+cardNo+"&name="+name+"&needBelongArea=true&phone="+phone;
        String url = MakeURL(host,new LinkedHashMap<String, Object>(){{
            put("idCard",cardNo);
            put("name",name);
            put("needBelongArea","true");
            put("phone",phone);
        }});
        Timber.d("============url==========="+url);
      //如果请求是https请求那么就信任所有SSL
        if(url.contains("https://")){
            HTTPSTrustManager.allowAllSSL();
        }
        if(isNetworkAvaliable()){
            StringRequest stringRequest= new StringRequest(
                    Request.Method.GET,
                    url,
                    listener,
                    errorListener
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers=new HashMap<>();
                    headers.put("Authorization","APPCODE " + appcode);
                    return headers;
                }
            };
            if(!TextUtils.isEmpty(tag)){
                stringRequest.setTag(tag);
            }
            mRequestQueue.add(stringRequest);
            return true;
        }
        return false;
    }


    /**
     * 取消请求
     * @param tag
     */
    public void cancelRequestQueue(Object tag){
        if(tag!=null){
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * URL拼接
     * @param p_url
     * @param params
     * @return
     */
    private String MakeURL(String p_url, LinkedHashMap<String, Object> params) {
        StringBuilder url = new StringBuilder(p_url);
        if(url.indexOf("?")<0){
            url.append('?');
        }
        for(String name : params.keySet()){
            url.append('&');
            url.append(name);
            url.append('=');
            url.append(String.valueOf(params.get(name)));
        }
        return url.toString().replace("?&", "?");
    }
    /**
     * 判断网络是否可用
     * @return
     */
    private boolean isNetworkAvaliable() {
        if(isNetworkAvaliable(mContext)){
            return true;
        }
        return false;
    }

    private boolean isNetworkAvaliable(Context mContext) {
        ConnectivityManager connectivityManager = (android.net.ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(mNetworkInfo!=null&&mNetworkInfo.isAvailable()
                &&mNetworkInfo.isConnected()){
            return true;
        }
        return false;
    }
}
