package com.todo;

import android.provider.BaseColumns;

import java.sql.Blob;

/**
 * Created by G V RAVI KUMAR on 3/8/2018.
 */

public class DBContract {

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "CategoryTable";

        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_STATUS = "status";
    }
}
