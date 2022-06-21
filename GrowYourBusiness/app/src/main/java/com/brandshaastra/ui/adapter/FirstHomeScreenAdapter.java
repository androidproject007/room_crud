package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.brandshaastra.utils.CircleImageView;
import com.bumptech.glide.Glide;
import com.brandshaastra.R;
import com.brandshaastra.interfaces.OnVideoSelect;
import com.brandshaastra.ui.activity.ImageCanvasActivity;
import com.brandshaastra.ui.model.FirstAdapterImageModel;
import com.brandshaastra.utils.CameraUtils;
import com.brandshaastra.utils.CustomTextViewBold;

import java.util.List;


public class FirstHomeScreenAdapter extends RecyclerView.Adapter<FirstHomeScreenAdapter.FirstViewHolder> {

    Context context;
    private CameraUtils cameraUtils;
    List<FirstAdapterImageModel> arrayList;
    OnVideoSelect onVideoSelect;

    public FirstHomeScreenAdapter(Context context, List<FirstAdapterImageModel> arrayList, OnVideoSelect onVideoSelect) {
        this.context = context;
        this.arrayList = arrayList;
        this.onVideoSelect = onVideoSelect;
    }

    @NonNull
    @Override
    public FirstViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_sub_recycler, parent, false);
        return new FirstViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FirstViewHolder holder, int position) {
        holder.name.setSelected(true);

        holder.name.setText(arrayList.get(position).getText());
        Glide.with(context).load(arrayList.get(position).getImage()).placeholder(R.drawable.image_gallery).into(holder.imageView);
/*
        if (position == 0) {
            holder.name.setText("Text");
        } else if (position == 1) {
            holder.name.setText("Image & Text");
        }
*/

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    context.startActivity(new Intent(context, ImageCanvasActivity.class).putExtra("text", true));
                } else if (position == 1) {
                    context.startActivity(new Intent(context, ImageCanvasActivity.class).putExtra("image", true));
                } else if (position == 2) {

                    onVideoSelect.selectVideo();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class FirstViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewBold name;
        CircleImageView imageView;
        CardView cardView;

        public FirstViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.home_sub_recycler_img);
            name = itemView.findViewById(R.id.home_sub_recycler_name);
            cardView = itemView.findViewById(R.id.clicking);

        }
    }
}
