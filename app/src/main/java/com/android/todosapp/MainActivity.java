package com.android.todosapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todosapp.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addCategory;
    EditText category_edit_text;
    Button cancel, add;
    TextView error1;
    ListView categoryListView;
    ArrayList<CategoryModel> list;
    CategoryAdapter adapter;
    DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Categories" + "</font>"));

        mDBHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        list = new ArrayList<>();
        categoryListView = (ListView) findViewById(R.id.category_list);

        Cursor c1 = db.rawQuery("select " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY + ", Count(*) as cnt " +
                "from " + DBContract.FeedEntry.TABLE_NAME + " group by " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY, null);

        while (c1.moveToNext()) {
            Global.itemsCount = c1.getString(c1.getColumnIndex("cnt"));
            list.add(new CategoryModel(c1.getString(c1.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_CATEGORY)), Global.itemsCount, null, null, null));
            adapter = new CategoryAdapter(MainActivity.this, list);
            adapter.notifyDataSetChanged();
            categoryListView.setAdapter(adapter);
        }

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivityForResult(new Intent(MainActivity.this, ItemsActivity.class).putExtra("categoryFromMain", list.get(position).get_category()),1);
            }
        });
        categoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.notifyDataSetChanged();
                SQLiteDatabase db = mDBHelper.getWritableDatabase();

                db.delete(DBContract.FeedEntry.TABLE_NAME,
                        DBContract.FeedEntry.COLUMN_NAME_CATEGORY + " = ?",
                        new String[]{list.get(position).get_category()});
                db.close();
                list.remove(position);
                return true;
            }
        });


        addCategory = (FloatingActionButton) findViewById(R.id.addCategory);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder createCategory = new AlertDialog.Builder(MainActivity.this);
                final View categoryView = getLayoutInflater().inflate(R.layout.create_category_dialog, null);
                category_edit_text = (EditText) categoryView.findViewById(R.id.create_category_editText);
                error1 = (TextView) categoryView.findViewById(R.id.create_category_error);
                cancel = (Button) categoryView.findViewById(R.id.cancel);
                add = (Button) categoryView.findViewById(R.id.add);
                createCategory.setView(categoryView);
                createCategory.setCancelable(false);

                final AlertDialog tempDialog = createCategory.create();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tempDialog.cancel();
                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String temp = category_edit_text.getText().toString().trim();
                        boolean alreadyExists = false;
                        for (CategoryModel a : list) {
                            if (temp.compareTo(a.get_category()) == 0) {
                                alreadyExists = true;
                                error1.setVisibility(View.VISIBLE);
                            }
                        }
                        if (temp.length() > 0 && !alreadyExists) {
                            SQLiteDatabase db = mDBHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(DBContract.FeedEntry.COLUMN_NAME_CATEGORY, temp);
                            long checked = db.insert(DBContract.FeedEntry.TABLE_NAME, null, values);
                            Log.v("inserted?", String.valueOf(checked));
                            db.close();
                            if (checked > 0) {
                                Toast.makeText(MainActivity.this, "added", Toast.LENGTH_SHORT).show();
                                list.add(new CategoryModel(temp, null, null, null, null));
                                adapter = new CategoryAdapter(MainActivity.this, list);
                                adapter.notifyDataSetChanged();
                                categoryListView.setAdapter(adapter);
                            }
                            tempDialog.dismiss();
                        } else if (temp.trim().length() <= 0) {
                            Toast.makeText(MainActivity.this, "Please add Category or press cancel", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                tempDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UpdateUI();
    }

    private void UpdateUI() {
        list.clear();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor c1 = db.rawQuery("select " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY + ", Count(*) as cnt " +
                "from " + DBContract.FeedEntry.TABLE_NAME + " group by " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY, null);

        while (c1.moveToNext()) {
            Global.itemsCount = c1.getString(c1.getColumnIndex("cnt"));
            list.add(new CategoryModel(c1.getString(c1.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_CATEGORY)), Global.itemsCount, null, null, null));
            adapter = new CategoryAdapter(MainActivity.this, list);
            adapter.notifyDataSetChanged();
            categoryListView.setAdapter(adapter);
        }
    }
}