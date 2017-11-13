package com.work17.huise.movieapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

public class DetailAsyncTask extends AsyncTask<String,Void,DetailedMove> {
    private String doubanuri="https://api.douban.com/v2/movie/subject/";
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
    private  Context context;
    private  ImageView[] imgs;
    public DetailAsyncTask(Context con,ImageView big, TextView moviename,TextView director,TextView year,TextView actor,ImageView[] imgs,TextView remark)
    {
        this.mImg_big=big;
        this.context=con;
        this.mTex_name=moviename;
        this.mTex_director=director;
        this.mTex_year=year;
        this.mTex_actor=actor;
        this.mText_remark=remark;
        this.imgs=imgs;



    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onPostExecute(DetailedMove moveData) {
        super.onPostExecute(moveData);

if (moveData==null)
{
    return;
}
        ImageLoader loader = new ImageLoader(mImg_big, context);
        loader.loadImages(moveData.getImages().getLarge());
if (moveData.getDirectors().size()>0)
        {
            mTex_director.setText("导演："+moveData.getDirectors().get(0).getName().toString());
        }
        else {
            mTex_director.setText("导演："+"无");
        }
        if (moveData.getCasts().size()>0)
        {  String actor="主演：";
            for (int i=0;i<moveData.getCasts().size();i++)
            {

                actor+=moveData.getCasts().get(i).getName().toString()+"/";
            }
            mTex_actor.setText(actor);
        }
        if (moveData.getCasts().size()>0)
        {
            for (int i=0;i<moveData.getCasts().size();i++)
            {
                 loader = new ImageLoader(imgs[i], context);
                loader.loadImages(moveData.getCasts().get(i).getAvatars().getMedium());
            }

        }

        mTex_year.setText("年份:"+moveData.getYear().toString());

        mTex_name.setText("电影名："+moveData.getTitle().toString());
mText_remark.setText(moveData.getSummary().toString());




    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected DetailedMove doInBackground(String... params) {
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private DetailedMove getdata(String url)
    {
        String js = loginByGet(url);
        return JSON.parseObject(js, DetailedMove.class);
    }
}
