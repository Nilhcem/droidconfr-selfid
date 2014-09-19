package com.nilhcem.selfid.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nilhcem.selfid.R;

public class FramesPagerAdapter extends PagerAdapter {

    private Context mContext;

    private static final int[] FRAMES = new int[]{
            R.drawable.frame_01,
            R.drawable.frame_02,
            R.drawable.frame_03,
            R.drawable.frame_04,
            R.drawable.frame_05,
            R.drawable.frame_06,
            R.drawable.frame_07,
            R.drawable.frame_08
    };

    FramesPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return FRAMES.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(FRAMES[position]);
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    public int getDrawableIdAt(int position) {
        return FRAMES[position];
    }
}
