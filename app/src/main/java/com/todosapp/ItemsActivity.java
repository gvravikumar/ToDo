package com.todosapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.View;

import com.build.todosapp.R;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity implements com.todosapp.RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    FloatingActionButton addItem;
    String item = "";
    byte[] img;
    RecyclerView listView;
    List<CategoryModel> list;
    ItemAdapter adapter;
    Bitmap byteBmp = null;
    DBHelper mDBHelper;
    ConstraintLayout constraintLayout;
    int titleID, descriptionID, imageID, statusID;
    RecyclerOnItemClickInterface recyclerOnItemClickInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        listView = (RecyclerView) findViewById(R.id.items_list);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        list = new ArrayList<>();
        list.clear();

        mDBHelper = new DBHelper(getApplicationContext());

        recyclerOnItemClickInterface = new RecyclerOnItemClickInterface() {
            @Override
            public void RecyclerOnItemClickMethod(int position) {
                startActivityForResult(new Intent(ItemsActivity.this, EditItemsActivity.class).putExtra("categoryFromItems", list.get(position).get_category_title()), 1);
            }
        };

        item = getIntent().getStringExtra("categoryFromMain");

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c = db.rawQuery("select * from " + DBContract.FeedEntry.TABLE_NAME + " where " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY + " =? ", new String[]{item}, null);

        titleID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_TITLE);
        descriptionID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION);
        imageID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_IMAGE);
        statusID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_STATUS);

        while (c.moveToNext()) {
            img = c.getBlob(imageID);
            if (img != null) {
                byteBmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            }else{
                byteBmp = null;
            }
            if (c.getString(titleID) != null || c.getString(imageID) != null || c.getString(statusID) != null) {
                if (!list.contains(c.getString(titleID))) {
                    list.add(new CategoryModel(null, c.getString(titleID), c.getString(descriptionID), byteBmp, c.getString(statusID)));
                }
            }
        }
        if (list.size() > 0) {
            adapter = new ItemAdapter(ItemsActivity.this, list, recyclerOnItemClickInterface);
            adapter.notifyDataSetChanged();
        }

        RecyclerView.LayoutManager mLM = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(mLM);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listView);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(listView);

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
        list.clear();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c = db.rawQuery("select * from " + DBContract.FeedEntry.TABLE_NAME + " where " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY + " =? ", new String[]{item}, null);
        int titleID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_TITLE);
        int descriptionID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION);
        int imageID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_IMAGE);
        int statusID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_STATUS);
        while (c.moveToNext()) {
            img = c.getBlob(imageID);
            if (img != null) {
                byteBmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            } else {
                byteBmp = null;
            }
            String b = c.getString(statusID);

            if (c.getString(titleID) != null || c.getString(imageID) != null || c.getString(statusID) != null) {
                if (!list.contains(c.getString(titleID))) {
                    list.add(new CategoryModel(null, c.getString(titleID), c.getString(descriptionID), byteBmp, c.getString(statusID)));
                }
            }
        }
        if (list.size() > 0) {
            adapter = new ItemAdapter(ItemsActivity.this, list, recyclerOnItemClickInterface);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ItemAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = list.get(viewHolder.getAdapterPosition()).get_category_title();

            // backup of removed item for undo purpose
            final CategoryModel deletedItem = list.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            SQLiteDatabase db = mDBHelper.getWritableDatabase();

            db.delete(DBContract.FeedEntry.TABLE_NAME,
                    DBContract.FeedEntry.COLUMN_NAME_TITLE + " = ?",
                    new String[]{name});
            db.close();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(constraintLayout, name + " Task has been removed", Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    // undo is selected, restore the deleted item
//                    adapter.restoreItem(deletedItem, deletedIndex);
//                }
//            });
//            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public interface RecyclerOnItemClickInterface {
        void RecyclerOnItemClickMethod(int position);
    }
}