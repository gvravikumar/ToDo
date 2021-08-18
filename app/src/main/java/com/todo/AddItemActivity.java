package com.todo;

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

import com.todo.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    ImageView imgageView;
    Button addImage,alertMe;
    EditText title, description;
    Switch status;
    String category = "";
    int resultImage = 2;
    byte[] imageBytes;
    DBHelper mDBHelper;
    List<String> tempList = new ArrayList<>();
    Bitmap selectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Add Item" + "</font>"));

        category = getIntent().getStringExtra("categoryFromItems");

        mDBHelper = new DBHelper(getApplicationContext());

        imgageView = (ImageView) findViewById(R.id.image);
        addImage = (Button) findViewById(R.id.addPhoto);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        status = (Switch) findViewById(R.id.status);

        clearImage();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage = null;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, resultImage);
            }
        });
        ((Button)findViewById(R.id.alertMe)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = Bitmap.createScaledBitmap(selectedImage, 320, 320, true);
                imgageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException fnf) {
                fnf.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Did not Pick the image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        switch (item.getItemId()) {
            case R.id.save:
                String temp_title = title.getText().toString().trim();
                String temp_description = description.getText().toString().trim();
                boolean temp_status = status.isChecked();

                if (temp_title.length() <= 0) {
                    throwAlert(1);
                } else {

                    Cursor c = db.rawQuery("select * from " + DBContract.FeedEntry.TABLE_NAME + " where " + DBContract.FeedEntry.COLUMN_NAME_CATEGORY + " =? ", new String[]{category}, null);

                    int titleID = c.getColumnIndex(DBContract.FeedEntry.COLUMN_NAME_TITLE);

                    while (c.moveToNext()) {
                        if (c.getString(titleID) != null) {
                            tempList.add(c.getString(titleID));
                        }
                    }
                    if (!tempList.contains(temp_title)) {
                        if (selectedImage != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            selectedImage.compress(Bitmap.CompressFormat.PNG, 90, stream);
                            imageBytes = stream.toByteArray();
                        }
                        ContentValues cv = new ContentValues();
                        cv.put(DBContract.FeedEntry.COLUMN_NAME_CATEGORY, category);
                        cv.put(DBContract.FeedEntry.COLUMN_NAME_TITLE, temp_title);
                        cv.put(DBContract.FeedEntry.COLUMN_NAME_IMAGE, imageBytes);
                        cv.put(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION, temp_description);
                        cv.put(DBContract.FeedEntry.COLUMN_NAME_STATUS, temp_status);
                        long saveResult = db.insert(DBContract.FeedEntry.TABLE_NAME, null, cv);
                        if (saveResult > 0) {
                            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                        }
                        clearImage();
                        db.close();
                        finish();
                    } else {
                        throwAlert(2);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void throwAlert(int code) {
        switch (code) {
            case 1:
                Toast.makeText(this, "Cannot save with empty Title field", Toast.LENGTH_SHORT).show();
                return;
            case 2:
                Toast.makeText(this, "Task ALready Exists", Toast.LENGTH_SHORT).show();
                return;
        }
    }

    private void clearImage() {
        selectedImage = null;
        imageBytes = null;
    }
}