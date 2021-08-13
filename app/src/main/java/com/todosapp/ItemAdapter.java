package com.todosapp;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.build.todosapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G V RAVI KUMAR on 3/10/2018.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    Context _c;
    List<CategoryModel> categoryModels = new ArrayList<>();
    ItemsActivity.RecyclerOnItemClickInterface _recyclerOnItemClickInterface;

    public ItemAdapter(Context c, List<CategoryModel> modelList, ItemsActivity.RecyclerOnItemClickInterface recyclerOnItemClickInterface) {
        this._c = c;
        this.categoryModels = modelList;
        this._recyclerOnItemClickInterface = recyclerOnItemClickInterface;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_items_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        CategoryModel temp = categoryModels.get(position);
        holder.title.setText(temp.get_category_title());
        holder.status.setText(categoryModels.get(position).get_status());
        if(categoryModels.get(position).get_image() != null){
            holder.selectedImage.setImageBitmap(categoryModels.get(position).get_image());
        }
        if (Integer.parseInt(categoryModels.get(position).get_status()) == 1) {
            holder.status.setText("Done");
        } else holder.status.setText("Pending");
        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _recyclerOnItemClickInterface.RecyclerOnItemClickMethod(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, status;
        public ImageView selectedImage;
        public RelativeLayout viewBackground;
        public LinearLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView)view.findViewById(R.id.title);
            status = (TextView)view.findViewById(R.id.status);
            selectedImage = (ImageView)view.findViewById(R.id.selectedImage);
            viewBackground = (RelativeLayout)view.findViewById(R.id.view_background);
            viewForeground = (LinearLayout) view.findViewById(R.id.view_foreground);
        }
    }

    public void removeItem(int position) {
        categoryModels.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(CategoryModel item, int position) {
        categoryModels.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
