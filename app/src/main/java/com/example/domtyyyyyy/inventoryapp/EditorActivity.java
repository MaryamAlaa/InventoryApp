package com.example.domtyyyyyy.inventoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.domtyyyyyy.inventoryapp.data.ProductContract.ProductEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;



public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;

    private EditText productName;
    private EditText productPrice;
    private EditText productQuantity;
    private EditText productSupplier;
    private Uri mCurrentProductUri;
    private Button mail;
    private ImageView productImage;
    private boolean productChanged = false;
    private boolean setAnImage = false;
    private static final int PICK_IMAGE_REQUEST = 0;


    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
        productName = (EditText) findViewById(R.id.name);
        productPrice = (EditText) findViewById(R.id.price);
        productQuantity = (EditText) findViewById(R.id.quantity);
        productSupplier = (EditText) findViewById(R.id.supplier);
        productImage = (ImageView) findViewById(R.id.image);
        mail = (Button) findViewById(R.id.mail);

        productName.setOnTouchListener(onTouchListener);
        productPrice.setOnTouchListener(onTouchListener);
        productQuantity.setOnTouchListener(onTouchListener);
        productSupplier.setOnTouchListener(onTouchListener);
        productImage.setOnTouchListener(onTouchListener);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeAnOrder();
            }
        });
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAnImage();
            }
        });
    }

    private boolean validation() {
        String name = productName.getText().toString().trim();
        String price = productPrice.getText().toString();
        String quantity = productPrice.getText().toString();
        String supplier = productSupplier.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Enter Price", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(quantity)) {
            Toast.makeText(this, "Enter Quantity", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(supplier)) {
            Toast.makeText(this, "Enter Supplier Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void saveProduct() {
        String nameString = productName.getText().toString().trim();
        String priceString = productPrice.getText().toString().trim();
        String quantityString = productQuantity.getText().toString().trim();
        String supplierString = productSupplier.getText().toString().trim();
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString)) {
            return;
        }


        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLOUM_NAME, nameString);
        values.put(ProductEntry.COLOUM_PRICE, priceString);
        values.put(ProductEntry.COLOUM_QUANTITY, quantityString);
        values.put(ProductEntry.COLOUM_SUPPLIER, supplierString);

        if (!setAnImage) {
            values.putNull(ProductEntry.COLOUM_IMAGE);
        } else {
            Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
            values.put(ProductEntry.COLOUM_IMAGE, imageArray(bitmap));
        }

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insertion_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insertion_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_product_succeed),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (validation()) {
                    saveProduct();
                }
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!productChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeAnOrder() {
        String supplierString = productSupplier.getText().toString().trim();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + supplierString));
        intent.putExtra(Intent.EXTRA_SUBJECT, "make an order");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (!productChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLOUM_NAME,
                ProductEntry.COLOUM_PRICE,
                ProductEntry.COLOUM_SUPPLIER,
                ProductEntry.COLOUM_IMAGE,
                ProductEntry.COLOUM_QUANTITY
        };
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(ProductEntry.COLOUM_NAME);
            int priceIndex = cursor.getColumnIndex(ProductEntry.COLOUM_PRICE);
            int supplierIndex = cursor.getColumnIndex(ProductEntry.COLOUM_SUPPLIER);
            int quantityIndex = cursor.getColumnIndex(ProductEntry.COLOUM_QUANTITY);
            int imageIndex = cursor.getColumnIndex(ProductEntry.COLOUM_IMAGE);

            String name = cursor.getString(nameIndex);
            int price = cursor.getInt(priceIndex);
            String supplier = cursor.getString(supplierIndex);
            int quantity = cursor.getInt(quantityIndex);

            productName.setText(name);
            productPrice.setText(Integer.toOctalString(price));
            productQuantity.setText(Integer.toOctalString(quantity));
            productSupplier.setText(supplier);
            byte[] image = cursor.getBlob(imageIndex);
            if (image != null) {
                productImage.setImageBitmap(getImage(image));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productName.setText("");
        productPrice.setText("");
        productQuantity.setText("");
        productSupplier.setText("");
        productImage.setImageDrawable(null);
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_left);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_product_falied),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeStream(new ByteArrayInputStream(image));
    }


    public static byte[] imageArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    public void selectAnImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "upload image"), PICK_IMAGE_REQUEST);
    }

    public Bitmap getBitmap(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        int width = productImage.getWidth();
        int hieght = productImage.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int imageWidth = bmOptions.outWidth;
            int imageHieght = bmOptions.outHeight;
            int scaleFactor = Math.min(imageWidth / width, imageHieght / hieght);
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(EditorActivity.class.getSimpleName(), "Can't Load Image", fne);
            return null;
        } catch (Exception e) {
            Log.e(EditorActivity.class.getSimpleName(), "Can't Load Image", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                Uri uri = resultData.getData();
                Log.i(EditorActivity.class.getSimpleName(), "Uri: " + uri.toString());
                productImage.setImageBitmap(getBitmap(uri));
                setAnImage = true;
            }
        }
    }


}
