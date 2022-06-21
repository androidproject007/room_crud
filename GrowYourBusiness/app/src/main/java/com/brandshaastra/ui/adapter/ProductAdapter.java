package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.brandshaastra.R;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.ui.activity.ImageCanvasActivity;
import com.brandshaastra.ui.model.ImageModel;
import com.brandshaastra.utils.CustomTextViewBold;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    List<ImageModel> arrayList;
    List<String> stringImageList;
    String category_name = "";
    String cat_tracker = "";
    boolean media_type = false;

    public ProductAdapter(Context context, List<ImageModel> arrayList, String cat_tracker, String category_name, boolean media_type) {
        this.context = context;
        this.arrayList = arrayList;
        this.category_name = category_name;
        this.cat_tracker = cat_tracker;
        this.media_type = media_type;
        Log.e("CATEGORY_NAME", " product adapter " + category_name);
        stringImageList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subcat_recycler_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.name.setVisibility(View.GONE);

        if (media_type) {
            Glide.with(context).load(arrayList.get(position).getThumbnail()).placeholder(R.drawable.logo_with_white_bg).into(holder.imageView);
        } else {

            Glide.with(context).load(arrayList.get(position).getImage()).placeholder(R.drawable.logo_with_white_bg).into(holder.imageView);
        }

        stringImageList.add(arrayList.get(position).getImage());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("CATEGORY_NAME"," product adapter before intent"+category_name);
                if (arrayList.get(position).getComing_soon().equals("1")) {
                    Toast.makeText(context, arrayList.get(position).getComing_soon_text(), Toast.LENGTH_SHORT).show();
                } else {
                    if (media_type) {
                        context.startActivity(new Intent(context, ImageCanvasActivity.class).putExtra(Consts.TRACKER, cat_tracker).putExtra(Consts.CATEGORY_NAME, category_name).putExtra(Consts.SUBCAT_NAME, arrayList.get(position).getImage()).putExtra(Consts.POSITION, String.valueOf(position)).putExtra("video", true).putExtra("DATA", arrayList.get(position).getVideo_url()).putExtra("video_url2", arrayList.get(position).getVideo_url2()));
                    } else {
                        context.startActivity(new Intent(context, ImageCanvasActivity.class).putExtra(Consts.TRACKER, cat_tracker).putExtra(Consts.CATEGORY_NAME, category_name).putExtra(Consts.SUBCAT_NAME, arrayList.get(position).getImage()).putExtra(Consts.POSITION, String.valueOf(position)).putExtra(Consts.IMAGE_LIST, (Serializable) stringImageList));
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewBold name;
        ImageView imageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.subcat_name);
            imageView = itemView.findViewById(R.id.subcat_image);
        }
    }
}
