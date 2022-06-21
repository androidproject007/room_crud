package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brandshaastra.R;
import com.brandshaastra.ui.model.NotificationModel;
import com.brandshaastra.utils.CustomTextView;
import com.brandshaastra.utils.CustomTextViewBold;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    Context context;
    List<NotificationModel> arrayList;

    public NotificationAdapter(Context context, List<NotificationModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_recycler_layout, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.n_name.setText(arrayList.get(position).getTitle());
        holder.n_str.setText(arrayList.get(position).getMsg());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewBold n_name, n_status;
        CustomTextView n_str;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            n_name = itemView.findViewById(R.id.notification_name);
            n_status = itemView.findViewById(R.id.notification_status);
            n_str = itemView.findViewById(R.id.notification_str);
        }
    }
}

