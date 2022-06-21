package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.brandshaastra.R;
import com.brandshaastra.ui.model.SiderModel;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {
    private ArrayList<SiderModel> images;
    private LayoutInflater inflater;
    private Context context;
    private FragmentManager fragmentManager;

    public SliderAdapter(Context context, ArrayList<SiderModel> images) {
        this.context = context;
        this.images = images;
        this.fragmentManager = fragmentManager;
        inflater = LayoutInflater.from(context);

        Log.e("Sliderimagesize", "" + images.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        ImageView myImage = myImageLayout.findViewById(R.id.image);

        Glide.with(context)
                .load(images.get(position).getSlider_image())
                .into(myImage);

        view.addView(myImageLayout, 0);

        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}