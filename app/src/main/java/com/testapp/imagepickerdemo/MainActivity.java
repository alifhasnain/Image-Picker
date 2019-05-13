package com.testapp.imagepickerdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //Define a request code constant
    private final int REQUEST_STORAGE_PERMISSION = 65132;

    private final int REQUEST_IMAGE_SELECT = 4646;

    Button mPickImage;

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPickImage = findViewById(R.id.btn_pick_image);

        mImageView = findViewById(R.id.image);

        mPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });
    }

    private void pickPhoto()    {
        if(!checkStoragePermission())   {
            askStoragePermission();
        }
        if(!checkStoragePermission())   {
            return;
        }

        final String[] items = {"Camera","Gallery"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose an option ");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = new Intent(MainActivity.this,ImagePicker.class);
                if(items[i].equals("Camera"))   {
                    intent.putExtra("choice",0);
                }
                else    {
                    intent.putExtra("choice",1);
                }
                startActivityForResult(intent, REQUEST_IMAGE_SELECT);
            }
        });
        dialog.create().show();

    }

    private boolean checkStoragePermission()    {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please provide storage permission first!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void askStoragePermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(Build.VERSION.SDK_INT >= 23) {
            requestPermissions(perms,REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhoto();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode)
        {
            case REQUEST_IMAGE_SELECT:
                if(resultCode == RESULT_OK)    {
                    Uri selectedPhotoUri = data.getData();

                    try {
                        Bitmap selectedPhotoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedPhotoUri);
                        mImageView.setImageBitmap(selectedPhotoBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
