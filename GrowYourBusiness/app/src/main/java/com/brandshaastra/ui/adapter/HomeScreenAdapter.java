package com.brandshaastra.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.brandshaastra.utils.CircleImageView;
import com.bumptech.glide.Glide;
import com.brandshaastra.DTO.CategoryDTO;
import com.brandshaastra.R;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.ui.activity.SubCategoryActivity;
import com.brandshaastra.utils.CustomTextViewBold;

import java.util.List;


public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.Homeviewholder> {

    Context context;
    List<CategoryDTO> arraylist;
    HomeSubAdapter homeSubAdapter;
    boolean type = false;
    public HomeScreenAdapter(Context context, List<CategoryDTO> arraylist,boolean type) {
        this.context = context;
        this.arraylist = arraylist;
        this.type = type;
        Log.e("CATALOG_CATEGORY", " adapter " + arraylist.toString());

    }

    @NonNull
    @Override
    public Homeviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_sub_recycler,parent,false);
        return new Homeviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Homeviewholder holder, @SuppressLint("RecyclerView") int position) {

        holder.home_sub_recycler_name.setText(arraylist.get(position).getCategory_name());

        Log.e("CATALOG_CATEGORY", " adapter 2" + arraylist.get(position).getCategory_name());


        /*holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,true));
        homeSubAdapter = new HomeSubAdapter(context,arraylist.get(position).getAllsub(),type);
        holder.recyclerView.setAdapter(homeSubAdapter);*/


        Glide.with(context).load(arraylist.get(position).getAllsub().get(0).getImage()).into(holder.home_sub_recycler_img);

        holder.clicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("cat_name"," homescreen adapter "+arraylist.get(position).getCategory_name());
                context.startActivity(new Intent(context, SubCategoryActivity.class).putExtra(Consts.TRACKER,arraylist.get(position).getTracker()).putExtra(Consts.CATEGORY_NAME,arraylist.get(position).getCategory_name()).putExtra("cat_id",arraylist.get(position).getCategory_id()).putExtra(Consts.MEDIA_TYPE_VIDEO,type));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    class Homeviewholder extends RecyclerView.ViewHolder {

        CustomTextViewBold home_sub_recycler_name,home_recycle_view_all;
        CircleImageView home_sub_recycler_img;
        CardView clicking;
        public Homeviewholder(@NonNull View itemView) {
            super(itemView);
            //home_recycle_view_all = itemView.findViewById(R.id.home_recycle_view_all);
            /*name = itemView.findViewById(R.id.home_recycle_business_name);
            recyclerView = itemView.findViewById(R.id.home_recycle_rv);*/
            home_sub_recycler_name = itemView.findViewById(R.id.home_sub_recycler_name);
            home_sub_recycler_img = itemView.findViewById(R.id.home_sub_recycler_img);
            clicking = itemView.findViewById(R.id.clicking);
        }
    }
}
