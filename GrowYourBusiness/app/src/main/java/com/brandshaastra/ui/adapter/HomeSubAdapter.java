package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.brandshaastra.DTO.SubCategoryDTO;
import com.brandshaastra.R;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.ui.activity.ProductActivity;
import com.brandshaastra.utils.CustomTextViewBold;

import java.util.List;

public class HomeSubAdapter extends RecyclerView.Adapter<HomeSubAdapter.HomeSubViewholder> {

    Context context;
    List<SubCategoryDTO> arraylist;
    boolean type = false;

    public HomeSubAdapter(Context context, List<SubCategoryDTO> arraylist, boolean type) {
        this.context = context;
        this.arraylist = arraylist;
        this.type = type;

    }

    @NonNull
    @Override
    public HomeSubViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_sub_recycler, parent, false);
        return new HomeSubViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSubViewholder holder, int position) {

        holder.name.setSelected(true);
        holder.name.setText(arraylist.get(position).getSubcatgory_name());
        Glide.with(context).load(arraylist.get(position).getImage()).placeholder(R.drawable.image_gallery).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  context.startActivity(new Intent(context, SubCategoryActivity.class).putExtra("cat_name",arraylist.get(position).getCat_name()).putExtra("cat_id",arraylist.get(position).getCatid()));
                context.startActivity(new Intent(context, ProductActivity.class).putExtra(Consts.CATEGORY_NAME, arraylist.get(position).getCat_name()).putExtra(Consts.SUBCAT_ID, arraylist.get(position).getSubcatgory_id()).putExtra(Consts.SUBCAT_NAME, arraylist.get(position).getSubcatgory_name()).putExtra(Consts.MEDIA_TYPE_VIDEO, type));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    class HomeSubViewholder extends RecyclerView.ViewHolder {

        CustomTextViewBold name;
        ImageView imageView;
        CardView cardView;

        public HomeSubViewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.home_sub_recycler_img);
            name = itemView.findViewById(R.id.home_sub_recycler_name);
            cardView = itemView.findViewById(R.id.clicking);

        }
    }
}
