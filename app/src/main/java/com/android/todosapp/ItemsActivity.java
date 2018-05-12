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
    String item = "";
    byte[] img;
    ListView listView;
    ArrayList<CategoryModel> list;
    ItemAdapter adapter;
    Bitmap byteBmp = null;
    DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.items_list);
        mDBHelper = new DBHelper(getApplicationContext());

        item = getIntent().getStringExtra("categoryFromMain");

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c = db.rawQuery("select * from " + DBContract.FeedEntry.TABLE_NAME + " where " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY + " =? ", new String[]{item}, null);

        while (c.moveToNext()) {
            int titleID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_TITLE);
            int descriptionID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION);
            int imageID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_IMAGE);
            int statusID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_STATUS);

            img = c.getBlob(imageID);

            if (img != null) {
                //byte[] strbyte = Base64.decode(img, Base64.DEFAULT);
                byteBmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            }

            if (c.getString(titleID) != null || c.getString(imageID) != null || c.getString(statusID) != null) {
                if(!list.contains(c.getString(titleID))){
                    list.add(new CategoryModel(null, c.getString(titleID), c.getString(descriptionID), byteBmp, c.getString(statusID)));
                    adapter = new ItemAdapter(ItemsActivity.this, list);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivityForResult(new Intent(ItemsActivity.this, EditItemsActivity.class).putExtra("categoryFromItems", list.get(position).get_category_title()),1);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.notifyDataSetChanged();
                SQLiteDatabase db = mDBHelper.getWritableDatabase();

                db.delete(DBContract.FeedEntry.TABLE_NAME,
                        DBContract.FeedEntry.COLUMN_NAME_TITLE + " = ?",
                        new String[]{list.get(position).get_category_title()});
                db.close();
                list.remove(position);
                return true;
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + item + "</font>"));

        addItem = (FloatingActionButton) findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ItemsActivity.this, AddItemActivity.class).putExtra("categoryFromItems", item), Global.SAVE_ITEM_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUI();
    }

    public void updateUI() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c = db.rawQuery("select * from " + DBContract.FeedEntry.TABLE_NAME + " where " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY + " =? ", new String[]{item}, null);

        while (c.moveToNext()) {
            int titleID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_TITLE);
            int descriptionID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION);
            int imageID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_IMAGE);
            int statusID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_STATUS);

            img = c.getBlob(imageID);
            if (img != null) {
                byteBmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            }else{
                byteBmp = null;
            }

            if (c.getString(titleID) != null || c.getString(imageID) != null || c.getString(statusID) != null) {
                if(!list.contains(c.getString(titleID))){
                    list.add(new CategoryModel(null, c.getString(titleID), c.getString(descriptionID), byteBmp, c.getString(statusID)));
                    adapter = new ItemAdapter(ItemsActivity.this, list);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }
            }else{
                list.clear();
                adapter = new ItemAdapter(ItemsActivity.this, list);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
        }
    }
}