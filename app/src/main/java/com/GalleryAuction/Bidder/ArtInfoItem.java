package com.GalleryAuction.Bidder;

import android.graphics.drawable.Drawable;
import android.webkit.WebView;

import java.net.URL;

/**
 * Created by GOD on 2017-03-28.
 */

public class ArtInfoItem {
    private String iconDrawable ;
    private String titleStr ;
    private String aucStr;
    private String bidStr;

    public ArtInfoItem() {

    }


    public void setIcon(String icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setAuckey(String auckey) {
        aucStr = auckey ;
    }

    public void setBidkey(String bidkey) {
        bidStr = bidkey ;
    }
    public String getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getAuckey() {
        return this.aucStr ;
    }
    public String getBidkey() {
        return this.bidStr ;
    }

}
