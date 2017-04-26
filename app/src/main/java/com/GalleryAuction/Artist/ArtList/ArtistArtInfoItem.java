
package com.GalleryAuction.Artist.ArtList;

import android.graphics.Bitmap;

/*** Created by GOD on 2017-03-28.*/
public class ArtistArtInfoItem {
    private String artisttitle ;
    private String timeStr ;
    private Bitmap image ;
    public ArtistArtInfoItem() {

    }

    public void setTitle(String title) {
        artisttitle = title ;
    }

    public void setTime(String time) {
        timeStr = time ;
    }
    public void setImage(Bitmap image) { this.image = image; }

    public String getTitle() {
        return this.artisttitle ;
    }

    public String getTime() {
        return this.timeStr ;
    }

    public Bitmap getImage() { return image;  }


}