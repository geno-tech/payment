package com.GalleryAuction.Item;

/**
 * Created by GOD on 2017-03-28.
 */

public class WinningBidInfoItem {
    private String MybestStr ;
    private String timeStr ;
    private String aucStr;
    private String bidStr;
    private String bidwin;

    public WinningBidInfoItem() {

    }


    public void setMybest(String mybest) {
        MybestStr = mybest; ;
    }
    public void setTime(String time) {
        timeStr = time ;
    }

    public String getMybest() {
        return this.MybestStr ;
    }
    public String getTime() {
        return this.timeStr ;
    }

}
