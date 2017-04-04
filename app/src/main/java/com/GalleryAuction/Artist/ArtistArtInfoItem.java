
package com.GalleryAuction.Artist;
/*** Created by GOD on 2017-03-28.*/
public class ArtistArtInfoItem {
    private String artisttitle ;
    private String timeStr ;
    public ArtistArtInfoItem() {

    }

    public void setTitle(String title) {
        artisttitle = title ;
    }

    public void setTime(String time) {
        timeStr = time ;
    }

    public String getTitle() {
        return this.artisttitle ;
    }

    public String getTime() {
        return this.timeStr ;
    }
}