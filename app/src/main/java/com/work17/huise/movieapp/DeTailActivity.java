package com.work17.huise.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DeTailActivity extends AppCompatActivity implements  View.OnClickListener {
    private Button mBut_return;
    private ImageButton mBut_menu;
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
 public DetailAsyncTask asyncTask;
    private  ImageView[] imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_de_tail);
        InitView();
        Intent intent=this.getIntent();
 String id =intent.getStringExtra("id");
        Log.e("Id",id);
     asyncTask=new DetailAsyncTask(this,mImg_big,mTex_name,mTex_director,mTex_year,mTex_actor,imageViews,mText_remark);
        asyncTask.execute(id);




    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.but_return:
                finish();
                break;
            case R.id.Img_menu:
                ShowPopMenu(mBut_menu);
                break;
        }

    }

    public void InitView()
    {
        mBut_return=(Button)findViewById(R.id.but_return);
        mBut_return.setOnClickListener(this);
        mBut_menu=(ImageButton) findViewById(R.id.Img_menu);
        mBut_menu.setOnClickListener(this);
        mImg_big=(ImageView)findViewById(R.id.img_big);
        mImg_1=(ImageView)findViewById(R.id.img_1);
        mImg_2=(ImageView)findViewById(R.id.img_2);
        mImg_3=(ImageView)findViewById(R.id.img_3);
        mImg_4=(ImageView)findViewById(R.id.img_4);
        mTex_name=(TextView)findViewById(R.id.movie_name);
        mTex_year=(TextView)findViewById(R.id.movie_year);
        mTex_actor=(TextView)findViewById(R.id.actor);
        mTex_director=(TextView)findViewById(R.id.director_name);
        mText_remark=(TextView)findViewById(R.id.remark);
        imageViews=new ImageView[]{mImg_1,mImg_2,mImg_3,mImg_4};
    }
    public void ShowPopMenu(View v)
    {
        PopupMenu popupMenu=new PopupMenu(this,v);
        popupMenu.getMenuInflater().inflate(R.menu.main,popupMenu.getMenu());
        popupMenu.show();
    }

}
