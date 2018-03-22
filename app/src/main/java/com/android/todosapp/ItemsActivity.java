package com.android.todosapp;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.todosapp.R;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity {

    FloatingActionButton addItem;
    String item = "",img;
    ListView listView;
    ArrayList<CategoryModel> list;
    ItemAdapter adapter;
    Bitmap byteBmp;

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.items_list);

        item = getIntent().getStringExtra("categoryFromMain");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(ItemsActivity.this,EditItemsActivity.class).putExtra("categoryFromItems",list.get(position).get_category_title()));
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + item + "</font>"));

        addItem = (FloatingActionButton) findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ItemsActivity.this, AddItemActivity.class).putExtra("categoryFromItems", item));
            }
        });
        updateUI();
    }
    public void updateUI(){
        DBHelper mDBHelper = new DBHelper(getApplicationContext());

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c = db.rawQuery("select * from " + DBContract.FeedEntry.TABLE_NAME + " where " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY + " =? ", new String[]{item}, null);

        if (c.moveToNext()) {
            do {
                int categoryID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_CATEGORY);
                int titleID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_TITLE);
                int descriptionID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION);
                int imageID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_IMAGE);
                int statusID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_STATUS);

                img = c.getString(imageID);
                if(img!=null){
                    byte[] strbyte  = Base64.decode(img, Base64.DEFAULT);
                    byteBmp = BitmapFactory.decodeByteArray(strbyte,0,strbyte.length);
                }

                if (c.getString(titleID) != null || c.getString(imageID) != null || c.getString(statusID) != null) {
                    list.add(new CategoryModel(c.getString(categoryID), c.getString(titleID), c.getString(descriptionID), byteBmp, c.getString(statusID)));
                    adapter = new ItemAdapter(ItemsActivity.this, list);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }
            } while (c.moveToNext());
        }
    }
}