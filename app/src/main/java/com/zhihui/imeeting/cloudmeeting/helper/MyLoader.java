package com.zhihui.imeeting.cloudmeeting.helper;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

public class MyLoader extends ImageLoader {

    @Override

    public void displayImage(Context context, Object path, ImageView imageView) {

        Glide.with(context).load(path).into(imageView);      //传入路径,因为list为String格式,path为Object格式,所以强制类型转换.

    }

}