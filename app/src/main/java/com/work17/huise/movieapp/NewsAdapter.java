package com.work17.huise.movieapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/11/12/012.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
    private MoveData mList;
    private Context context;
    private LayoutInflater mInflater;
    //图片加载类
    private ImageLoader imageLoader;
    //listView开始下载和结束下载的位置
    private int mStart,mEnd;
    //所有的URL的数组
    public static String[] URLS;
    private boolean mFirstIn;//第一次启动
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public NewsAdapter(Context context,MoveData mList, ListView listView){
        mInflater = LayoutInflater.from(context);
        this.mList = mList;
        this.context=context;
        imageLoader = new ImageLoader(listView,context);
        //获取所有的URL并初始化数组
        URLS = new String[mList.getSubjects().size()];
        for(int i = 0;i<mList.getSubjects().size();i++){
            URLS[i] = mList.getSubjects().get(i).getImages().getMedium();
        }
        listView.setOnScrollListener(this);
        mFirstIn = true;
    }
    @Override
    public int getCount() {
        return mList.getSubjects().size();
    }

    @Override
    public Object getItem(int position) {
        return mList.getSubjects().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void Onclick(int Postion)
    {
        Intent intent=new Intent(context,DeTailActivity.class);
    intent.putExtra("id",mList.getSubjects().get(Postion).getId()) ;
        context.startActivity(intent);





    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_view, null);
            viewHolder.ivIcon = (ImageView)convertView.findViewById(R.id.image);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.title);
            viewHolder.tvDirectors = (TextView)convertView.findViewById(R.id.directors);
            viewHolder.tvYear = (TextView)convertView.findViewById(R.id.year);
            viewHolder.TvCasts = (TextView)convertView.findViewById(R.id.casts);


            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置默认的图片
//      viewHolder.ivIcon.setImageResource(R.drawable.ic_launcher);

        String url = mList.getSubjects().get(position).getImages().getMedium();
        /*因为Itme是重复利用的，ListView滑动到第2行会异步加载某个图片，但是加载很慢，加载过程中listView已经滑动到了第14行，
         * 且滑动过程中该图片加载结束，第2行已不在屏幕内，根据缓存原理，第2行的view可能被第14行复用，这样我
         * 们看到的就是第14行显示了本该属于第2行的图片，造成显示重复。如果14行的图片也加载结束则会造成闪烁，先显示前一张，再显示后一张
         * 为了防止图片加载时错位，这里加上tag，把imageView和url标识绑定，在异步显示的位置，判断当前任务的url和item设置的url是否
         * 相同，只有相同才去加载图片
         */
        viewHolder.ivIcon.setTag(url);
        //在滚动的时候加载图片，如果缓存中都没有，则使用默认的图片
        imageLoader.showImagesFromCache(viewHolder.ivIcon, url);
        viewHolder.tvTitle.setText("电影名："+mList.getSubjects().get(position).getTitle());
        Log.d("11",position+"");

        if(mList.getSubjects().get(position).getDirectors().size()!=0)viewHolder.tvDirectors.setText("导演："+mList.getSubjects().get(position).getDirectors().get(0).getName());
        viewHolder.tvYear.setText("年份："+mList.getSubjects().get(position).getYear());
        String ca="";
        if(mList.getSubjects().size()==1) ca =mList.getSubjects().get(position).getCasts().get(0).getName();
        for(int i=0;i<mList.getSubjects().get(position).getCasts().size();i++)//电影主演
        {
           ca += mList.getSubjects().get(position).getCasts().get(i).getName();
            if(i!=mList.getSubjects().get(position).getCasts().size()-1)ca+="/";
        }
        viewHolder.TvCasts.setText("主演："+ca);
        return convertView;
    }
    class ViewHolder{
        public TextView tvTitle,tvYear,tvDirectors,TvCasts;
        public ImageView ivIcon;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE){
            //当前状态处于停止状态，加载可见项
            imageLoader.loadImages(mStart, mEnd);
        }else{
            //停止任务
            imageLoader.cancelAllTasks();
        }
    }
    /**
     * 由于我们使用的是滚动状态改变时才去下载图片，但是第一次进入的时候要加载第一屏的图片
     * listview初始化后会调用onScroll方法，我们在这里去加载第一屏的图片并把第一次进入
     * 状态位置为false
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        //start 为第一个可见的item的位置
        mStart = firstVisibleItem;
        //end 为第一个可见的位置加上可见的item的数量
        mEnd = firstVisibleItem + visibleItemCount;
        if(mFirstIn && visibleItemCount > 0){
            //第一次显示的时候调用，加载图片
            imageLoader.loadImages(mStart, mEnd);
            mFirstIn = false;
        }
    }
}