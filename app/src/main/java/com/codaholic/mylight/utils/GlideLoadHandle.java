package com.codaholic.mylight.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.codaholic.mylight.R;

import java.io.File;



/**
 * Created by vim on 01/03/18.
 */

public class GlideLoadHandle {
    private Context context;

    public GlideLoadHandle(Context context) {
        this.context = context;
    }

    public void intoCacheNews(String url, ImageView source) {
        Glide.with(context)
                .load(url)
                .thumbnail(0.1f)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .override(500, 700)
                .into(source);
    }

}
