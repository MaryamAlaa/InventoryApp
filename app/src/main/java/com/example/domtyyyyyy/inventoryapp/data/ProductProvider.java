package com.example.domtyyyyyy.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.domtyyyyyy.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Domtyyyyyy on 7/12/2017.
 */

public class ProductProvider extends ContentProvider {
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT, PRODUCT);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    private ProductDbHelper productDbHelper;

    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = productDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {

            case PRODUCT:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertio not supportwith ur " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase database = productDbHelper.getWritableDatabase();

        String name = values.getAsString(ProductEntry.COLOUM_NAME);
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Product  requires a name");
        }

        Integer price = values.getAsInteger(ProductEntry.COLOUM_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("Product requires price");
        }

        Integer quantity = values.getAsInteger(ProductEntry.COLOUM_QUANTITY);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }
        String supplier = values.getAsString(ProductEntry.COLOUM_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Product  requires a supplier name");
        }

        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        SQLiteDatabase database = productDbHelper.getWritableDatabase();
        if (contentValues.containsKey(ProductEntry.COLOUM_NAME)) {
            String name = contentValues.getAsString(ProductEntry.COLOUM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLOUM_SUPPLIER)) {
            String supplier = contentValues.getAsString(ProductEntry.COLOUM_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Product requires supplier name");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLOUM_PRICE)) {
            Integer price = contentValues.getAsInteger(ProductEntry.COLOUM_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }
        if (contentValues.containsKey(ProductEntry.COLOUM_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(ProductEntry.COLOUM_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("need validate quantity");
            }
        }
        if (contentValues.size() == 0) {
            return 0;
        }
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = productDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
