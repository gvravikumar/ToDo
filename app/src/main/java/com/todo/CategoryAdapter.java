package com.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.todo.R;

import java.util.ArrayList;

/**
 * Created by G V RAVI KUMAR on 3/8/2018.
 */

public class CategoryAdapter extends ArrayAdapter<CategoryModel> {

    Context _c;
    ArrayList<CategoryModel> categoryModels;

    public CategoryAdapter(Context c, ArrayList<CategoryModel> modelList) {
        super(c, R.layout.category_list_items, modelList);
        this._c = c;
        this.categoryModels = modelList;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this._c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DBHelper mDBHelper = new DBHelper(_c);
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        View view = inflater.inflate(R.layout.category_list_items, null);
        TextView categoryTitle = (TextView) view.findViewById(R.id.category_title);
        TextView itemsCount = (TextView) view.findViewById(R.id.category_item_count);
        categoryTitle.setText(categoryModels.get(position).get_category());
        if(categoryModels.get(position).get_category_title()  !=null){
            itemsCount.setText(Integer.parseInt(categoryModels.get(position).get_category_title())-1 + "items");
        }else{
            itemsCount.setText("0 Items");
        }
        return view;
    }
}