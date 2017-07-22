package com.example.domtyyyyyy.inventoryapp.data;

/**
 * Created by Domtyyyyyy on 7/12/2017.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class ProductContract {

    public ProductContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.domtyyyyyy.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "product";

    public static final class ProductEntry implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        public static String COLOUM_ID = BaseColumns._ID;
        public static String TABLE_NAME = "product";
        public static String COLOUM_NAME = "name";
        public static String COLOUM_QUANTITY = "quantity";
        public static String COLOUM_PRICE = "price";
        public static String COLOUM_SUPPLIER = "supplier";
        public static  String COLOUM_IMAGE= "image";
    }
}