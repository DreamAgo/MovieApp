package com.work17.huise.movieapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/11/13 0013.
 */

public class DetailAsyncTask extends AsyncTask<String,Void,MoveData> {
    private String doubanuri="/v2/movie/subject/:";
    private ImageView mImg_big;
    private TextView mTex_name;
    private TextView  mTex_director;
    private TextView  mTex_year;
    private TextView  mTex_actor;
    private ImageView mImg_1;
    private ImageView mImg_2;
    private ImageView mImg_3;
    private ImageView mImg_4;
    private TextView mText_remark;
    public DetailAsyncTask(ImageView big, TextView moviename,TextView director,TextView year,TextView actor,ImageView mImg_1,ImageView mImg_2,ImageView mImg_3,ImageView mImg_4,TextView remark)
    {
        this.mImg_1=mImg_1;
        this.mImg_2=mImg_2;
        this.mImg_3=mImg_3;
        this.mImg_4=mImg_4;
        this.mTex_name=moviename;
        this.mTex_director=director;
        this.mTex_year=year;
        this.mTex_actor=actor;
        this.mText_remark=remark;



    }
    @Override
    protected void onPostExecute(MoveData moveData) {
        super.onPostExecute(moveData);

    }

    @Override
    protected MoveData doInBackground(String... params) {
      return getdata(doubanuri+params[0]);
    }
    private static String loginByGet(String path){
        //get的方式提交就是url拼接的方式

        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            //获得结果码
            int responseCode = connection.getResponseCode();
            if(responseCode ==200){
                //请求成功 获得返回的流
                InputStream is = connection.getInputStream();
                BufferedReader bf=new BufferedReader(new InputStreamReader(is,"UTF-8"));
                //最好在将字节流转换为字符流的时候 进行转码
                StringBuffer buffer=new StringBuffer();
                String line="";
                while((line=bf.readLine())!=null){
                    buffer.append(line);
                }

                return buffer.toString();
                // return IOSUtil.inputStream2String(is);
            }else {
                //请求失败
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private MoveData getdata(String url)
    {
        String js = loginByGet(url);
        return JSON.parseObject(js, MoveData.class);
    }
}
