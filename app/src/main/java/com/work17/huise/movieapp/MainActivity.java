package com.work17.huise.movieapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private LinearLayout mLin_hot;
 private LinearLayout mLin_new;
private LinearLayout mLin_search;
private ImageButton mImg_hot;
 private ImageButton mImg_new;
private ImageButton mImg_search;
    private ImageButton mImg_menu;
    private TextView mTex_hot;
    private TextView mTex_new;
    private TextView mTex_search;
    private Fragment mHotfragment;
    private Fragment mNewfragment;
    private Fragment mSearchfragment;




    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.lin_hot:
               Clickchange(mImg_hot,mTex_hot);
        StandChange(mImg_new,mTex_new,mImg_search,mTex_search);
                InitFragment(0);
                break;
            case  R.id.lin_new:
                Clickchange(mImg_new,mTex_new);
                StandChange(mImg_hot,mTex_hot,mImg_search,mTex_search);
                InitFragment(1);
                break;
            case  R.id.lin_search:
                Clickchange(mImg_search,mTex_search);
                StandChange(mImg_hot,mTex_hot,mImg_new,mTex_new);
                InitFragment(2);
                break;
            case R.id.Img_menu:
                ShowPopMenu(mImg_menu);
                break;



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化view
        InitView();
        //初始化导航栏颜色
        Clickchange(mImg_hot,mTex_hot);
        //设置默认的fragment
        InitFragment(0);

    }
    //初始化数据
    public void InitView()
    {
        mLin_hot=(LinearLayout)findViewById(R.id.lin_hot);
        mLin_hot.setOnClickListener(this);
        mLin_new=(LinearLayout)findViewById(R.id.lin_new);
        mLin_new.setOnClickListener(this);
        mLin_search=(LinearLayout)findViewById(R.id.lin_search);
        mLin_search.setOnClickListener(this);
        mImg_hot=(ImageButton)findViewById(R.id.img_hot);
        mImg_new=(ImageButton)findViewById(R.id.img_new);
        mImg_search=(ImageButton)findViewById(R.id.img_search);
        mTex_hot=(TextView)findViewById(R.id.text_hot);
        mTex_new=(TextView)findViewById(R.id.text_new);
        mTex_search=(TextView)findViewById(R.id.text_search);
        mImg_menu=(ImageButton)findViewById(R.id.Img_menu);
        mImg_menu.setOnClickListener(this);

    }
    public  void Clickchange(ImageButton img,TextView textView)
    {
        img.setImageResource(R.drawable.green);
        textView.setTextColor(getResources().getColor(R.color.green));
    }
    public void StandChange(ImageButton img,TextView textView,ImageButton img1,TextView textView1)
    {
        img.setImageResource(R.drawable.red);
        textView.setTextColor(getResources().getColor(R.color.hui));
        img1.setImageResource(R.drawable.red);
        textView1.setTextColor(getResources().getColor(R.color.hui));

    }
    //设置frgment
    public void InitFragment(int index)
    {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction tr=fm.beginTransaction();
        HideFragment(tr);
        switch (index)
        {
            case 0:
                if (mHotfragment==null)
                {
                    mHotfragment=new HotFragement();
                    tr.add(R.id.frg_content,mHotfragment);
                }
                else {
                    tr.show(mHotfragment);
                }
                break;
            case 1:
                if (mNewfragment==null)
                {
                    mNewfragment=new NewFragment();
                    tr.add(R.id.frg_content,mNewfragment);
                }
                else {
                    tr.show(mNewfragment);
                }
                break;
            case 2:
                if (mSearchfragment==null)
                {
                    mSearchfragment=new Search_Fragment();
                    tr.add(R.id.frg_content,mSearchfragment);

                }
                else {
                    tr.show(mSearchfragment);
                }
                break;


        }
        tr.commit();
    }
    //隐藏fragment
    public void HideFragment(FragmentTransaction tr)
    {
        if (mHotfragment!=null)
        {
           // mHotfragment=new HotFragement();
            tr.hide(mHotfragment);
        }

        if (mNewfragment!=null)
        {
            //mNewfragment=new HotFragement();
            tr.hide(mNewfragment);
        }
        if (mSearchfragment!=null)
        {
           // mSearchfragment=new HotFragement();
            tr.hide(mSearchfragment);
        }

    }
    public void ShowPopMenu(View v)
    {
        PopupMenu popupMenu=new PopupMenu(this,v);
        popupMenu.getMenuInflater().inflate(R.menu.main,popupMenu.getMenu());
        popupMenu.show();
    }
}

