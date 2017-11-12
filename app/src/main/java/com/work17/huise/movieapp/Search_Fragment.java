package com.work17.huise.movieapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class Search_Fragment extends Fragment {
   private ListViewForScrollView listView;
    private EditText editText;
    private TextView textView;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragmen_search,container,false);
        listView= view.findViewById(R.id.list);
        editText= view.findViewById(R.id.search_edit);
        textView= view.findViewById(R.id.text);

        view.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                String text=editText.getText().toString();
                if(TextUtils.isEmpty(text))
                {
                    Toast.makeText(getContext(),"内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                new NewsAsyncTask(getContext(),listView).execute("https://api.douban.com/v2/movie/search?q="+text);
                textView.setText("你正在搜索>"+text);
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });



        return view;
    }
}
