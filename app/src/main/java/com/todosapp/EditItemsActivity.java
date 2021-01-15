package com.todosapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.build.todosapp.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditItemsActivity extends AppCompatActivity {

    ImageView img;
    Button changePhoto;
    EditText title, description;
    Switch status;
    String header = "", updateTitle, updateDescription, temp_desc, temp_title, statusFromDB;
    byte[] imgStr;
    Boolean updateStatus;
    Bitmap byteBmp, selectedImage;
    int CHANGE_IMAGE_REQUEST_CODE = 4;
    byte[] imageBytes;
    DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_items);
        mDBHelper = new DBHelper(getApplicationContext());

        header = getIntent().getStringExtra("categoryFromItems");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + header + "</font>"));

        img = (ImageView) findViewById(R.id.edit_image);
        changePhoto = (Button) findViewById(R.id.edit_addPhoto);
        title = (EditText) findViewById(R.id.edit_title);
        description = (EditText) findViewById(R.id.edit_description);
        status = (Switch) findViewById(R.id.edit_status);

        SQLiteDatabase sqLiteDatabase = mDBHelper.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("select * from " + DBContract.FeedEntry.TABLE_NAME + " where " + DBContract.FeedEntry.COLUMN_NAME_TITLE + " =? ", new String[]{header}, null);

        while (c.moveToNext()) {
            imgStr = c.getBlob(c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_IMAGE));
            if (imgStr != null) {
                byteBmp = BitmapFactory.decodeByteArray(imgStr, 0, imgStr.length);
                selectedImage = byteBmp;
                img.setImageBitmap(byteBmp);
            }
            temp_title = c.getString(c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_TITLE)).trim();
            temp_desc = c.getString(c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION)).trim();
            if (temp_title.length() > 0) {
                title.setText(c.getString(c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_TITLE)));
            } else {
                title.setHint("Title");
            }
            if (temp_desc.length() > 0) {
                description.setText(c.getString(c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION)));
            } else {
                description.setHint("Description(Optional)");
            }
            statusFromDB = c.getString(c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_STATUS));
            if(Integer.parseInt(statusFromDB)==1) {
                status.setChecked(true);
            }else{
                status.setChecked(false);
            }
        }

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImage = new Intent(Intent.ACTION_PICK);
                getImage.setType("image/*");
                startActivityForResult(getImage, CHANGE_IMAGE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = Bitmap.createScaledBitmap(selectedImage, 160, 160, true);
                img.setImageBitmap(selectedImage);
            } catch (FileNotFoundException fnf) {
                fnf.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Did not Pick the image", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.del_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        switch (item.getItemId()) {
            case R.id.save:
                if (selectedImage != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 90, stream);
                    imageBytes = stream.toByteArray();
                }
                updateTitle = title.getText().toString();
                updateDescription = description.getText().toString();
                updateStatus = status.isChecked();

                ContentValues cv = new ContentValues();
                cv.put(DBContract.FeedEntry.COLUMN_NAME_TITLE, updateTitle);
                cv.put(DBContract.FeedEntry.COLUMN_NAME_IMAGE, imageBytes);
                cv.put(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION, updateDescription);
                cv.put(DBContract.FeedEntry.COLUMN_NAME_STATUS, updateStatus);
                long saveResult = db.update(DBContract.FeedEntry.TABLE_NAME, cv, DBContract.FeedEntry.COLUMN_NAME_TITLE + " =? ", new String[]{header});
                if (saveResult != -1) {
                    setResult(1);
                    Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                }
                db.close();
                finish();
                return true;
            case R.id.delete:
                long deleteResult = db.delete(DBContract.FeedEntry.TABLE_NAME,
                        DBContract.FeedEntry.COLUMN_NAME_TITLE + " = ?",
                        new String[]{header});
                if (deleteResult != -1) {
                    Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    setResult(1, new Intent());
                }
                db.close();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}