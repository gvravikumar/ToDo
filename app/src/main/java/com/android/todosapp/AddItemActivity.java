package com.android.todosapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.todosapp.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddItemActivity extends AppCompatActivity {

    ImageView img;
    Button addImage;
    EditText title, description;
    Switch status;
    String category ="",selectedImg;
    int resultImage = 2;
    byte[] imageBytes;
    DBHelper db = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Add Item" + "</font>"));

        category = getIntent().getStringExtra("categoryFromItems");

        img = (ImageView) findViewById(R.id.image);
        addImage = (Button) findViewById(R.id.addPhoto);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        status =(Switch) findViewById(R.id.status);

        addImage.setOnClickListener(new View.OnClickListener()   {
            @Override
            public void onClick(View v)  {
                Intent getImage = new Intent(Intent.ACTION_PICK);
                getImage.setType("image/*");
                startActivityForResult(getImage,resultImage);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                img.setImageBitmap(selectedImage);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG,0, stream);
                imageBytes = stream.toByteArray();
                selectedImg = Base64.encodeToString(imageBytes, Base64.DEFAULT).replaceAll("[\n\r]", "");

            }catch(FileNotFoundException fnf){
                fnf.printStackTrace();
            }
        }else{
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
        getMenuInflater().inflate(R.menu.save,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save :
                String temp_title = title.getText().toString();
                String temp_description = description.getText().toString();
                boolean temp_status = status.isChecked();

                SQLiteDatabase sqLiteOpenHelper = db.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(DBContract.FeedEntry.COLUMN_NAME_CATEGORY,category);
                cv.put(DBContract.FeedEntry.COLUMN_NAME_TITLE,temp_title);
                cv.put(DBContract.FeedEntry.COLUMN_NAME_IMAGE,selectedImg);
                cv.put(DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION,temp_description);
                if (!temp_status){
                    cv.put(DBContract.FeedEntry.COLUMN_NAME_STATUS,1);
                }else{
                    cv.put(DBContract.FeedEntry.COLUMN_NAME_STATUS,0);
                }
                long saveResult = sqLiteOpenHelper.insert(DBContract.FeedEntry.TABLE_NAME,null,cv);
                if(saveResult != -1) Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                db.close();
                getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}