package com.lokeshsoni.imagefilter.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokeshsoni.imagefilter.Interface.FilterListFragmentListner;
import com.lokeshsoni.imagefilter.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.MyViewHolder> {

    private List<ThumbnailItem> thumbnailItemList;
    private FilterListFragmentListner filterListFragmentListner;
    private Context context;
    private  int selectedIndex = 0;

    public ThumbnailAdapter(List<ThumbnailItem> thumbnailItemList, FilterListFragmentListner filterListFragmentListner, Context context) {
        this.thumbnailItemList = thumbnailItemList;
        this.filterListFragmentListner = filterListFragmentListner;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.thumbnail_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);
        holder.thumbnail.setImageBitmap(thumbnailItem.image);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterListFragmentListner.onFilterSelected(thumbnailItem.filter);
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });

        holder.filter_name.setText(thumbnailItem.filterName);

        if(selectedIndex == position)
            holder.filter_name.setTextColor(ContextCompat.getColor(context,R.color.selected_filter));
        else
            holder.filter_name.setTextColor(ContextCompat.getColor(context,R.color.normal_filter));

    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView filter_name;
        public MyViewHolder(View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            filter_name = itemView.findViewById(R.id.filter_name);

        }
    }
}
