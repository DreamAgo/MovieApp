package com.work17.huise.movieapp;

/**
 * Created by Administrator on 2017/11/12/012.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class NewsAsyncTask extends AsyncTask<String , Void, MoveData> {
    private Context context;
    private ListView listView;
    public NewsAsyncTask(Context context,ListView listView)
    {
        this.context=context;
        this.listView=listView;

    }
    @Override
    protected MoveData doInBackground(String... params) {
        return getdata(params[0]);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onPostExecute(MoveData result) {
        super.onPostExecute(result);
        //创建并给listView设置适配器
        final NewsAdapter adapter = new NewsAdapter(context, result,listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.Onclick(position);
            }

        });
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