package com.rongxianren.android_fix_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {


    private static int count = 0;
    private ImageView imageView;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view_icon);
        mImageView = (ImageView) findViewById(R.id.image_animal);
    }

    public void action(View view) {
        Toast.makeText(this, CalcuUtils.calculate()+"", Toast.LENGTH_SHORT).show();
    }

    public void fixBug(View view) {
         fixBug();
    }


    private void fixBug() {
        File fileDir = getDir(MyConstant.DEX_DIR, Context.MODE_PRIVATE);
        String dexName = "classes.dex";
        String filePath = fileDir + File.separator + dexName;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + dexName);
            OutputStream os = new FileOutputStream(file);
            int count = 0;
            byte[] buffer = new byte[1024];
            while ((count = is.read(buffer)) != -1) {
                os.write(buffer, 0, count);
            }

            if (file.exists()) {
                Toast.makeText(this, "dex 移动成功", Toast.LENGTH_SHORT).show();
            }
            is.close();
            os.close();

            DexFixUtils.loadFixedDex(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void printBitmapSize(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            System.out.println(" width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());
        } else {
            System.out.println( "Drawable is null !");
        }
    }


}

