package com.work17.huise.movieapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

public class ImageMemoryCache {
    /**
     * 从内存读取数据速度是最快的，为了更大限度使用内存，这里使用了两层缓存。
     * 强引用缓存不会轻易被回收，用来保存常用数据
     * 不常用的数据转入软引用缓存，不会影响GC的回收。
     */
    private static final int SOFT_CACHE_SIZE = 15;  //软引用缓存容量
    private static LruCache<String, Bitmap> mLruCache;  //硬引用缓存
    private static LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache;  //软引用缓存

    public ImageMemoryCache(Context context) {
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        Log.i("mengli","maxMemory = "+ maxMemory);
        //强引用缓存容量，为系统可用内存的1/4
        int cacheSize = maxMemory/4;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (value != null)
                    //每次加入缓存时会调用
                    return value.getByteCount();
                else
                    return 0;
            }
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue != null)
                    //LRU算法会把最近使用的元素压入栈顶，所以栈底就是被移除的元素
                    // 强引用缓存容量满的时候，会根据LRU算法把最近最久没有被使用的图片转入此软引用缓存
                    mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
            }
        };
        mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_SIZE, 0.75f, true) {
            //            private static final long serialVersionUID = 6040103833179403725L;
            @Override
            protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
                if (size() > SOFT_CACHE_SIZE){
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 从缓存中获取图片
     */
    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap;
        //先从强引用缓存中获取
        synchronized (mLruCache) {
            bitmap = mLruCache.get(url);
            if (bitmap != null) {
                //如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中最后被删除
                mLruCache.remove(url);
                mLruCache.put(url, bitmap);
                return bitmap;
            }
        }
        //如果强引用缓存中找不到，到软引用缓存中找
        synchronized (mSoftCache) {
            SoftReference<Bitmap> bitmapReference = mSoftCache.get(url);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();
                if (bitmap != null) {
                    //将图片移回硬缓存
                    mLruCache.put(url, bitmap);
                    mSoftCache.remove(url);
                    return bitmap;
                } else {
                    //没找到，可能改bigmap已经被回收了，删除url
                    mSoftCache.remove(url);
                }
            }
        }
        return null;
    }

    /**
     * 添加图片到缓存
     */
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mLruCache) {
                mLruCache.put(url, bitmap);
            }
        }
    }

    public void clearCache() {
        mSoftCache.clear();
    }
}