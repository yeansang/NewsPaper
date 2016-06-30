package com.example.nemus.newspaper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by nemus on 2016-06-30.
 */
public class ConnectContentProvider extends ContentProvider{

    DBConnect dbConnect;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    @Override
    public boolean onCreate() {
        dbConnect = new DBConnect(getContext(), "news.db",null,1);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables("FAV");

        Cursor c = queryBuilder.query(dbConnect.getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        Log.d("query", queryBuilder.toString());
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
