package com.test.busanit.messenger_alpha.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.test.busanit.messenger_alpha.R;

import static com.test.busanit.messenger_alpha.Constants.IMAGE_DATA;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH;


public class PhotoViewActivity extends FragmentActivity {

    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        photoView = (ImageView) findViewById(R.id.imageView_photo);

        Bitmap bitmapImage = getBitmapImage();

        setBitmapImage(bitmapImage);

        if (bitmapImage != null) {
            bitmapImage = null;
        }
    }

    private Bitmap getBitmapImage() {
        byte[] bitmapData = getIntent().getByteArrayExtra(IMAGE_DATA);
        Bitmap bitmapImage = getBitmap(bitmapData);
        bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH, IMAGE_HEIGHT, false);

        return bitmapImage;
    }

    private void setBitmapImage(Bitmap bitmapImage) {
        photoView.setImageBitmap(bitmapImage);
    }


    private Bitmap getBitmap(byte[] bitmapData) {
        if (bitmapData == null)
            return null;
        return BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }
}