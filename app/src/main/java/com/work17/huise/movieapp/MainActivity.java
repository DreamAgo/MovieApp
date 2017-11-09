package com.work17.huise.movieapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private List<View> viewPages = new ArrayList<>();
    //包裹点点的LinearLayout
    private ViewGroup group;
    private ImageView imageView;
     //定义一个ImageVIew数组，来存放生成的小园点
     private ImageView[] imageViews;
    private ListViewForScrollView listView;
    private  String path="https://api.douban.com/v2/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPageAdapter();
        initPointer();
        initEvent();
        initList();

    }

    private void initList() {

      final   Handler handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                Bundle bu=msg.getData();
               String js= bu.getString("data");//获取数据
               getdata(js);//解析数据

            }

        };
        listView=(ListViewForScrollView)findViewById(R.id.list);
        Runnable r=new Runnable() {
            @Override
            public void run() {
                String js = loginByGet();
                Message message=new Message();
                Bundle bundle=new Bundle();
                bundle.putString("data",js);
                message.setData(bundle);
                handler.sendMessage(message);//发送数据
              //  SimpleAdapter sim=new SimpleAdapter();
            }
        };
        new Thread(r).start();
       // List<MoveData>move= JSON.parseArray()



    }
    private void getdata(String js)
    {
        MoveData movie = JSON.parseObject(js, MoveData.class);
        List<SubjectsBean> subs = movie.getSubjects();
        List<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
        for (SubjectsBean s : subs) {

            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("title","电影名："+s.getTitle());//电影名

            map.put("year","年份："+s.getYear());//电影年份

            String imgUrl = s.getImages().getLarge();

            //for test
            AsyncImageLoader loader = new AsyncImageLoader(getApplicationContext());

            //将图片缓存至外部文件中
            loader.setCache2File(true); //false
            //设置外部缓存文件夹
            loader.setCachedDir(this.getCacheDir().getAbsolutePath());
            Log.d("445","1");
            //下载图片，第二个参数是否缓存至内存中
            loader.downloadImage(imgUrl, true/*false*/, new AsyncImageLoader.ImageCallback() {
                @Override
                public void onImageLoaded(Bitmap bitmap, String imageUrl) {
                    if(bitmap != null){
                        Log.d("445","2");
                        map.put("image",bitmap);
                    }else{
                        //下载失败，设置默认图片
                    }
                }
            });
            Log.d("445","3");
            map.put("directors","导演："+s.getDirectors().get(0).getName());//电影导演
            String ca="";

            for(CastsBean c:s.getCasts())//电影主演
            {
                ca+=c.getName()+"/";
            }
            map.put("Casts","主演："+ca);
            maps.add(map);
        }
        SimpleAdapter sim=new SimpleAdapter(this,maps,R.layout.list_view,new String[]{"title","year","image","directors","Casts"},new int[]{R.id.title, R.id.year, R.id.image,R.id.directors,R.id.casts});
        listView.setAdapter(sim);
    }
    public static String loginByGet(){
        //get的方式提交就是url拼接的方式
        String path = "https://api.douban.com/v2/movie/in_theaters";
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

    private void initEvent() {
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new GuidePageChangeListener());
    }

    private void initPageAdapter() {
        /**
         51          * 对于这几个想要动态载入的page页面，使用LayoutInflater.inflate()来找到其布局文件，并实例化为View对象
         52          */
        LayoutInflater inflater = LayoutInflater.from(this);
        View page1 = inflater.inflate(R.layout.imageappr, null);
        View page2 = inflater.inflate(R.layout.imageappr, null);
        View page3 = inflater.inflate(R.layout.imageappr, null);

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
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        group = (ViewGroup) findViewById(R.id.viewGroup);
    }
    //初始化下面的小圆点的方法
    private void initPointer() {
        //有多少个界面就new多长的数组
        imageViews = new ImageView[viewPages.size()];
        for (int i = 0; i < imageViews.length; i++) {
            imageView = new ImageView(this);
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
