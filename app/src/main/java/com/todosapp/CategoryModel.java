package com.todosapp;

import android.graphics.Bitmap;

/**
 * Created by G V RAVI KUMAR on 3/8/2018.
 */

public class CategoryModel {
    private String _category, _category_title, _description, _status;
    Bitmap _image;

    public CategoryModel(String category, String title, String description, Bitmap blob, String status) {
        this._category = category;
        this._category_title = title;
        this._description = description;
        this._image = blob;
        this._status = status;
    }

    public String get_category() {
        return _category;
    }

    public String get_category_title() {
        return _category_title;
    }

    public String get_description() {
        return _description;
    }

    public String get_status() {
        return _status;
    }

    public Bitmap get_image() {
        return _image;
    }
}
