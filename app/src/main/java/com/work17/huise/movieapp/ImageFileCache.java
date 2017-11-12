package com.work17.huise.movieapp;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Administrator on 2017/11/12/012.
 */
public class ImageFileCache {
    //缓存目录名
    private static final String CACHDIR = "ImgeCache";
    private static final String WHOLESALE_CONV = ".cach";

    private static final int MB = 1024*1024;
    //缓存大小
    private static final int CACHE_SIZE = 10;
    //剩余最小空间大小
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public ImageFileCache() {
        //清理文件缓存
        removeCache(getDirectory());
    }

    /** 从缓存中获取图片 **/
    public Bitmap getImage(final String url) {
        final String path = getDirectory() + "/" + getFileNameFromUrl(url);
        File file = new File(path);
        if (file.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            if (bmp == null) {
                file.delete();
            } else {
                //获取时图片时需要更新文件的最后修改时间
                updateFileTime(path);
                return bmp;
            }
        }
        return null;
    }

    /** 将图片存入文件缓存 **/

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void saveBitmap(Bitmap bm, String url) {
        if (bm == null) {
            return;
        }
        //判断sdcard上的空间 ，如果不足10M返回
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            //SD空间不足
            return;
        }
        String filename = getFileNameFromUrl(url);
        String dir = getDirectory();
        File dirFile = new File(dir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        //创建文件
        File file = new File(dir +"/" + filename);
        try {
            file.createNewFile();
            OutputStream outStream = new FileOutputStream(file);
            //将图片进行压缩并写入文件，100表示不压缩
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        }
    }

    /**
     * 计算存储目录下的文件大小，
     * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
     * 那么删除40%最近没有被使用的文件
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean removeCache(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return true;
        }
        //没有挂载外部存储设备
        if (!android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return false;
        }

        int dirSize = 0;
        for (int i = 0; i < files.length; i++) {
            //遍历目录下的所有文件，如果包含.cach 则累加size
            if (files[i].getName().contains(WHOLESALE_CONV)) {
                dirSize += files[i].length();
            }
        }
        //如果缓存目录的文件大小 大于规定的缓存大小或者剩余内存不足10M，则删除40%最久未使用的文件
        if (dirSize > CACHE_SIZE * MB || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            int removeFactor = (int) ((0.4 * files.length) + 1);
            //对文件按时间排序
            Arrays.sort(files, new FileLastModifSort());
            for (int i = 0; i < removeFactor; i++) {
                if (files[i].getName().contains(WHOLESALE_CONV)) {
                    files[i].delete();
                }
            }
        }

        if (freeSpaceOnSd() <= CACHE_SIZE) {
            return false;
        }

        return true;
    }

    /** 修改文件的最后修改时间 **/
    public void updateFileTime(String path) {
        File file = new File(path);
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
    }

    /** 计算sdcard上的剩余空间 **/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private int freeSpaceOnSd() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double)stat.getAvailableBlocksLong() * (double) stat.getBlockSizeLong()) / MB;
        return (int) sdFreeMB;
    }

    /**从url中获取文件名 **/
    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/")+1)+WHOLESALE_CONV;
    }

    /** 获得缓存目录 **/
    private String getDirectory() {
        String dir = getSDPath() + "/" + CACHDIR;
        return dir;
    }

    /** 取SD卡路径 **/
    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }
    /**
     * 根据文件的最后修改时间进行排序，Java中对对象进行排序要实现Comparator 接口，自己实现比较规则
     * 1 表示大于，0表示相等，-1表示小于
     */
    private class FileLastModifSort implements Comparator<File> {
        public int compare(File arg0, File arg1) {
            if (arg0.lastModified() > arg1.lastModified()) {
                return 1;
            } else if (arg0.lastModified() == arg1.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}