package com.qinkai.puzzlegame.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class GridItemsAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<Bitmap> mBlockList;

    public GridItemsAdapter(Context context, List<Bitmap> picList) {
        mContext = context;
        mBlockList = picList;
    }

    @Override
    public int getCount() {
        return mBlockList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBlockList.get(position);
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
            imageView.setLayoutParams(new GridView.LayoutParams(
                    mBlockList.get(position).getWidth(),
                    mBlockList.get(position).getHeight()
            ));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setImageBitmap(mBlockList.get(position));

        return imageView;
    }
}
