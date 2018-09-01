package com.qinkai.puzzlegame.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.qinkai.puzzlegame.R;
import com.qinkai.puzzlegame.util.ScreenUtil;

import java.util.List;

public class GridPicListAdapter extends BaseAdapter {
    private Context mContext;
    private final List<Bitmap> mPicList;

    public GridPicListAdapter(Context context, List<Bitmap> picList) {
        mContext = context;
        mPicList = picList;
    }

    @Override
    public int getCount() {
        return mPicList.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return mPicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            int wPixel =  mContext.getResources().getDimensionPixelSize(R.dimen.pic_w);
            int hPixel =  mContext.getResources().getDimensionPixelSize(R.dimen.pic_h);
            imageView.setLayoutParams(new GridView.LayoutParams(wPixel, hPixel));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setBackgroundColor(Color.BLACK);
        imageView.setImageBitmap(getItem(position));
        return imageView;
    }
}
