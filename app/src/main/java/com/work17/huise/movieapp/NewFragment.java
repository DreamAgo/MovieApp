package com.work17.huise.movieapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class NewFragment extends Fragment {
    private ListView listView;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      view=inflater.inflate(R.layout.fragment_new,container,false);
        initView();
        initList();
        return view;
    }

    private void initView() {
        listView=(ListViewForScrollView)view.findViewById(R.id.list);
    }
    private void initList() {
        new NewsAsyncTask(getContext(),listView).execute("https://api.douban.com/v2/movie/coming_soon");//加载列表  列表加载内容只需要一句  点击详细在适配器内添加
    }

}
