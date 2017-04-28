package com.GalleryAuction.Client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by GOD on 2017-04-28.
 */

public class ImageViewItem {
    public static Bitmap getImageBitmap(final String imageURL){
        final Bitmap[] bitmapImage = new Bitmap[1];
        Thread mThread = new Thread(){
            @Override
            public void run() {

                try {
                    URL url = new URL(imageURL); // URL 주소를 이용해서 URL 객체 생성

                    //  아래 코드는 웹에서 이미지를 가져온 뒤
                    //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmapImage[0] = BitmapFactory.decodeStream(is);

                } catch(IOException ex) {
                    ex.getMessage();
                }
            }

        };
        mThread.start();
        try {
            mThread.join();

        }
        catch (InterruptedException e){
            e.getMessage();
        }
        return bitmapImage[0];
    }
}
