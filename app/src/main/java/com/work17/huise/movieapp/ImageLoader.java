package com.work17.huise.movieapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/11/12/012.
 */

public class ImageLoader {
    /**
     * 使用多线程的方式去加载图片
     */
    private ImageView imageView;
    private String mUrl;
    //内存缓存
    private ImageMemoryCache memoryCache;
    //文件缓存
    private ImageFileCache fileCache;
    private ImageView mimageView;
    private ListView mListView;
    //任务集合，用来处理多个下载线程
    private Set<NewsAsyncTask> mTasks;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public ImageLoader(ListView listView, Context context){
        mListView = listView;
        mTasks = new HashSet<NewsAsyncTask>();
        memoryCache = new ImageMemoryCache(context);
        fileCache = new ImageFileCache();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public ImageLoader(ImageView textView, Context context){
        mimageView = textView;
        mTasks = new HashSet<NewsAsyncTask>();
        memoryCache = new ImageMemoryCache(context);
        fileCache = new ImageFileCache();
    }
    /**
     * 用于从一个url获取bitmap
     */
    public Bitmap getBitmapFromURL(String urlString){
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (Exception e) {

        }finally{
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    //在滚动的时候显示缓存的图片，如果没有缓存图片则显示默认的图片
    public void showImagesFromCache(ImageView imageView,String url){
        //从缓存取出图片
        Bitmap result = memoryCache.getBitmapFromCache(url);
        if (result == null) {
            // 文件缓存中获取
            result = fileCache.getImage(url);
        }
        if(result == null){
            imageView.setImageResource(R.drawable.w);
        }else{
            imageView.setImageBitmap(result);
        }
    }
    //取消加载图片
    public void cancelAllTasks(){
        if(mTasks != null){
            for(NewsAsyncTask task: mTasks){
                task.cancel(false);
            }
        }
    }
    public void loadImages(int start,int end){
        //加载从start到end的图片
        for(int i = start;i<end;i++){
            String url = NewsAdapter.URLS[i];
            //从内存缓存取出图片
            Bitmap bitmap = memoryCache.getBitmapFromCache(url);
            //如果缓存没有，则从文件中读取
            if(bitmap == null){
                //从文件获取图片
                bitmap = fileCache.getImage(url);
                //文件中也为空，则必须从网络下载图片
                if(bitmap == null){
                    //使用AsyncTask下载图片，这里会耗费流量
                    NewsAsyncTask task = new NewsAsyncTask(url);
                    task.execute(url);
                    //添加一个任务
                    mTasks.add(task);
                }else{
                    //文件中获取到了图片，则把图片加入到内存中
                    memoryCache.addBitmapToCache(url, bitmap);
                }
            }
            if(bitmap != null){
                //根据url去获取 对应的imageView对象，防止显示混乱
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    public void loadImages(String url){
        //加载从start到end的图片

            //从内存缓存取出图片
            Bitmap bitmap = memoryCache.getBitmapFromCache(url);
            //如果缓存没有，则从文件中读取
            if(bitmap == null){
                //从文件获取图片
                bitmap = fileCache.getImage(url);
                //文件中也为空，则必须从网络下载图片
                if(bitmap == null){
                    //使用AsyncTask下载图片，这里会耗费流量
                    NewsAsyncTask task = new NewsAsyncTask(url);
                    task.execute(url);
                    //添加一个任务
                    mTasks.add(task);
                }else{
                    //文件中获取到了图片，则把图片加入到内存中
                    memoryCache.addBitmapToCache(url, bitmap);
                }
            }
            if(bitmap != null){
                //根据url去获取 对应的imageView对象，防止显示混乱
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }

    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private String mUrl;
        public NewsAsyncTask(String url){
            mUrl = url;
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        protected Bitmap doInBackground(String... params) {
            //从网络获取图片
            Bitmap bitmap = getBitmapFromURL(params[0]);
            if(bitmap != null){
                //把bitmap加入到缓存
                memoryCache.addBitmapToCache(params[0], bitmap);
                //把bitmap 加入到文件
                fileCache.saveBitmap(bitmap,params[0]);
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            ImageView imageView;
            if(mListView!=null)
           imageView = (ImageView) mListView.findViewWithTag(mUrl);
         else  imageView=mimageView;
            if(imageView != null && result != null){
                imageView.setImageBitmap(result);
            }
            //下载任务完成则移除这个任务
            mTasks.remove(this);
        }
    }
}