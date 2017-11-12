package com.work17.huise.movieapp;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class HotFragement extends Fragment {
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private List<View> viewPages = new ArrayList<>();
    //包裹点点的LinearLayout
    private ViewGroup group;
    private ImageView imageView;
    //定义一个ImageVIew数组，来存放生成的小园点
    private ImageView[] imageViews;
    private ListViewForScrollView listView;
    private View view;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     view=inflater.inflate(R.layout.fragment_hot,container,false);
        initView();
        initPageAdapter();
        initPointer();
        initEvent();
        initList();
        return view;
    }
    private void initList() {
        new NewsAsyncTask(getContext(),listView).execute("https://api.douban.com/v2/movie/in_theaters");//加载列表
    }


    private void initEvent() {
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new GuidePageChangeListener());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initPageAdapter() {
        /**
         51          * 对于这几个想要动态载入的page页面，使用LayoutInflater.inflate()来找到其布局文件，并实例化为View对象
         52          */
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View page1 = inflater.inflate(R.layout.imageappr, null);
        ImageLoader loader=new ImageLoader(((ImageView)page1.findViewById(R.id.im)),getContext());
        loader.loadImages("https://img3.doubanio.com/view/photo/s_ratio_poster/public/p1955027201.webp");
        View page2 = inflater.inflate(R.layout.imageappr, null);
        loader=new ImageLoader(((ImageView)page2.findViewById(R.id.im)),getContext());
        loader.loadImages("https://img3.doubanio.com/view/photo/s_ratio_poster/public/p725871004.webp");
        View page3 = inflater.inflate(R.layout.imageappr, null);
       loader=new ImageLoader(((ImageView)page3.findViewById(R.id.im)),getContext());
        loader.loadImages("https://img1.doubanio.com/view/photo/s_ratio_poster/public/p449706837.webp");
        //添加到集合中
        viewPages.add(page1);

        viewPages.add(page2);

        viewPages.add(page3);
        adapter = new PagerAdapter() {
            //获取当前界面个数
            //
            @Override
            public int getCount() {
                return viewPages.size();
            }
            //判断是否由对象生成页面
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewPages.get(position));
            }
            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = viewPages.get(position);
                container.addView(view);
                return view;
            }
        };
    }
    private void initView() {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        group = (ViewGroup)view. findViewById(R.id.viewGroup);
        listView=(ListViewForScrollView)view.findViewById(R.id.list);

    }
    //初始化下面的小圆点的方法
    private void initPointer() {
        //有多少个界面就new多长的数组
        imageViews = new ImageView[viewPages.size()];
        for (int i = 0; i < imageViews.length; i++) {
            imageView = new ImageView(getContext());
            //设置控件的宽高
            imageView.setLayoutParams(new ViewGroup.LayoutParams(40, 40));
            //控件的padding属性
            imageView.setPadding(20, 0, 20, 0);
            imageViews[i] = imageView;
            //初始化第一个page页面的图片的原点为选中状态
            if (i == 0) {
                //表示当前图片
                imageViews[i].setBackgroundResource(R.drawable.in);/**
                 * 在java代码中动态生成ImageView的时候
                 * 要设置其BackgroundResource属性才有效
                 * 设置ImageResource属性无效
                 */

            } else {
                imageViews[i].setBackgroundResource(R.drawable.un);
            }
            group.addView(imageViews[i]);
        }
    }
    //ViewPager的onPageChangeListener监听事件，当ViewPager的page页发生变化的时候调用
    public class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        //页面滑动完成后执行
        @Override
        public void onPageSelected(int position) {
            //判断当前是在那个page，就把对应下标的ImageView原点设置为选中状态的图片
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[position].setBackgroundResource(R.drawable.in);
                if (position != i) {
                    imageViews[i].setBackgroundResource(R.drawable.un);
                }
            }
        }
        //监听页面的状态，0--静止  1--滑动   2--滑动完成
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }



}
