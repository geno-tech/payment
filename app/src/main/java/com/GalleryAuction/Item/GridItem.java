
package com.GalleryAuction.Item;

import android.graphics.Bitmap;

/*** Created by GOD on 2017-03-28.*/
public class GridItem {
    private String artisttitle ;
    private Bitmap image ;
    public GridItem() {

    }

    public void setTitle(String title) {
        artisttitle = title ;
    }

    public void setImage(Bitmap image) { this.image = image; }

    public String getTitle() {
        return this.artisttitle ;
    }

    public Bitmap getImage() { return image;  }


}