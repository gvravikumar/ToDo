package com.android.todosapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todosapp.R;

import java.util.ArrayList;

/**
 * Created by G V RAVI KUMAR on 3/10/2018.
 */

public class ItemAdapter extends ArrayAdapter<CategoryModel> {
    Context _c;
    ArrayList<CategoryModel> categoryModels;

    public ItemAdapter(Context c, ArrayList<CategoryModel> modelList) {
        super(c, R.layout.custom_items_list, modelList);
        this._c = c;
        this.categoryModels = modelList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this._c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.custom_items_list, null);
        ImageView iv = (ImageView) v.findViewById(R.id.selectedImage);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView status = (TextView) v.findViewById(R.id.status);

        iv.setImageBitmap(categoryModels.get(position).get_image());

        title.setText(categoryModels.get(position).get_category_title());

        if (Integer.parseInt(categoryModels.get(position).get_status()) != 1) {
            status.setText("Done");
        } else status.setText("Pending");
        return v;
    }
}
