
package com.GalleryAuction.Item;

import android.graphics.Bitmap;

/*** Created by GOD on 2017-03-28.*/
public class BidderGoingItem {
    private String name ;
    private String title ;
    private String time  ;
    public BidderGoingItem() {

    }

    public void setName(String strname) {
        name = strname ;
    }

    public void setTitle(String strtitle) {
        title = strtitle ;
    }
    public void setTime(String strtime) {
        time = strtime;
    }

    public String getName() {
        return this.name ;
    }

    public String getTitle() {
        return this.title ;
    }

    public String getTime() {
        return this.time;
    }


}