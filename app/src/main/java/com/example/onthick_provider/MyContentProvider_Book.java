package com.example.onthick_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyContentProvider_Book extends ContentProvider {

    static final String AUTHORITY = "com.example.onthick_provider.MyContentProvider_Book";
    static final String CONTENT_PATH = "bookPath";
    static final String URL = "content://" + AUTHORITY + "/" + CONTENT_PATH;
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final String TABLE_NAME = "Books";
    private SQLiteDatabase db;

    private static HashMap<String, String> BOOKS_PROJECTION_MAP;

    static final int ALLITEMS = 1;
    static final int ONEITEM = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, ALLITEMS);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", ONEITEM);
    }

    public MyContentProvider_Book() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case ALLITEMS:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case ONEITEM:
                String id = uri.getPathSegments().get(1);
                count = db.delete(TABLE_NAME, "id_book" + " = " + id + (!TextUtils.isEmpty(selection)
                        ? "AND (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long number_row = db.insert(TABLE_NAME, "", values);
        if (number_row > 0) {
            Uri uri1 = ContentUris.withAppendedId(CONTENT_URI, number_row);
            getContext().getContentResolver().notifyChange(uri1, null);
            return uri1;
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        Context context = getContext();
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
        if (db == null)
            return false;
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case ALLITEMS:
                sqLiteQueryBuilder.setProjectionMap(BOOKS_PROJECTION_MAP);
                break;
            case ONEITEM:
                sqLiteQueryBuilder.appendWhere("id_book" + "=" + uri.getPathSegments().get(1));
                break;
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = "id_book";
        }
        Cursor cursor = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case ALLITEMS:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case ONEITEM:
                count = db.update(TABLE_NAME, values, "id_book" + " = " + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context) {
            super(context, "BookDatabase", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String author = "create table Authors(id_author integer primary key, name text, address text, email text)";
            db.execSQL(author);
            String book = "create table Books(id_book integer primary key, title text, id_author integer " +
                    "constraint id_author references Authors(id_author) on delete cascade on update cascade)";
            db.execSQL(book);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String author = "drop table if exists Authors";
            String book = "drop table if exists Books";
            db.execSQL(author);
            db.execSQL(book);
            onCreate(db);
        }
    }
}
