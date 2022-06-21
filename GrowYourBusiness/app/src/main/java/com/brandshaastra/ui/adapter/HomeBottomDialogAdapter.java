package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.brandshaastra.DTO.BusinessDataDto;
import com.brandshaastra.R;
import com.brandshaastra.interfaces.OnBusinessDataItemSelected;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.utils.CustomTextViewBold;

import java.util.List;

public class HomeBottomDialogAdapter extends RecyclerView.Adapter<HomeBottomDialogAdapter.BottomDialogViewholder> {

    Context context;
    SharedPrefrence prefrence;
    List<BusinessDataDto> arrayList;
    OnBusinessDataItemSelected itemSelected;
    public HomeBottomDialogAdapter(Context context, List<BusinessDataDto> arrayList,OnBusinessDataItemSelected itemSelected) {
        this.context = context;
        this.arrayList = arrayList;
        this.itemSelected = itemSelected;
    }

    @NonNull
    @Override
    public BottomDialogViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_dialog_rv_layout,parent,false);
        return new BottomDialogViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomDialogViewholder holder, int position) {
        holder.b_name.setText(arrayList.get(position).getName());
        Glide.with(context).load(arrayList.get(position).getImage()).placeholder(R.drawable.image_gallery).into(holder.b_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSelected.onItemSelected(position,arrayList.get(position).getBussiness_profile_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class BottomDialogViewholder extends RecyclerView.ViewHolder {

        CustomTextViewBold b_name;
        ImageView b_image;
        public BottomDialogViewholder(@NonNull View itemView) {
            super(itemView);

            b_image = itemView.findViewById(R.id.dialog_rv_image);
            b_name = itemView.findViewById(R.id.dialog_rv_name);
        }
    }
}
