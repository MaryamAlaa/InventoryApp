package com.example.domtyyyyyy.inventoryapp.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.domtyyyyyy.inventoryapp.data.ProductContract.ProductEntry;
/**
 * Created by youssef alaa on 17/06/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCT_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLOUM_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLOUM_SUPPLIER + " TEXT NOT NULL, "
                + ProductEntry.COLOUM_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLOUM_IMAGE + " BLOB,"
                + ProductEntry.COLOUM_QUANTITY + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME + ";");
        onCreate(sqLiteDatabase);
    }
    }
