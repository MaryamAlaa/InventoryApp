package com.example.domtyyyyyy.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.domtyyyyyy.inventoryapp.data.ProductContract.ProductEntry;

import static android.R.attr.id;

/**
 * Created by Domtyyyyyy on 7/16/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView productName = (TextView) view.findViewById(R.id.name);
        final TextView productQuantity = (TextView) view.findViewById(R.id.quantity);
        TextView productPrice = (TextView) view.findViewById(R.id.price);
        Button productSale = (Button) view.findViewById(R.id.sale);
        int idIndex = cursor.getColumnIndex(ProductEntry._ID);
        final long id = cursor.getLong(idIndex);

        int nameIndex = cursor.getColumnIndex(ProductEntry.COLOUM_NAME);
        String name = cursor.getString(nameIndex);

        int priceIndex = cursor.getColumnIndex(ProductEntry.COLOUM_PRICE);
        int price = cursor.getInt(priceIndex);

        int quantityIndex = cursor.getColumnIndex(ProductEntry.COLOUM_QUANTITY);
        final int quantity = cursor.getInt(quantityIndex);

        productSale.setFocusable(false);
        if (quantity >= 0) {
            productSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLOUM_QUANTITY, quantity - 1);
                    Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                    context.getContentResolver().update(
                            uri,
                            values,
                            ProductEntry._ID + "=?",
                            new String[]{String.valueOf(ContentUris.parseId(uri))});

                }
            });
            productName.setText(name);
            productQuantity.setText(String.valueOf(cursor.getInt(quantityIndex)));
            productPrice.setText(String.valueOf(price));
        }

    }
}
