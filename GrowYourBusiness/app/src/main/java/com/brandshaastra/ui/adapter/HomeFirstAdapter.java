package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.brandshaastra.R;
import com.brandshaastra.ui.model.HomeFirstModel;
import com.brandshaastra.utils.CustomTextViewBold;

import java.util.List;

public class HomeFirstAdapter extends RecyclerView.Adapter<HomeFirstAdapter.FirstViewHolder> {

    Context context;
    List<HomeFirstModel> arrayList;

    public HomeFirstAdapter(Context context, List<HomeFirstModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public FirstViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_sub_recycler,parent, false);
        return new FirstViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FirstViewHolder holder, int position) {

        holder.textViewBold.setText(arrayList.get(position).getName());
        Glide.with(context).load(arrayList.get(position).getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class FirstViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewBold textViewBold;
        ImageView imageView;
        public FirstViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewBold = itemView.findViewById(R.id.home_sub_recycler_name);
            imageView = itemView.findViewById(R.id.home_sub_recycler_img);
        }
    }
}
