package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brandshaastra.R;
import com.brandshaastra.interfaces.ThemeItemClick;
import com.bumptech.glide.Glide;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {

    Context context;
    List<String> arrayList;
    ThemeItemClick themeItemClick;

    public ThemeAdapter(Context context, List<String> arrayList, ThemeItemClick themeItemClick) {
        this.context = context;
        this.arrayList = arrayList;
        this.themeItemClick = themeItemClick;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.theme_recycler_layout, parent, false);
        return new ThemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {

//        holder.theme_img.setBackground(arrayList.get(position));
        Glide.with(context).load(arrayList.get(position)).into(holder.theme_img);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeItemClick.getThemeItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ThemeViewHolder extends RecyclerView.ViewHolder {
        ImageView theme_img;

        public ThemeViewHolder(@NonNull View itemView) {
            super(itemView);
            theme_img = itemView.findViewById(R.id.theme_img);
        }
    }
}
