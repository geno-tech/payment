
package com.GalleryAuction.Artist.AuctionList;
/*** Created by GOD on 2017-03-28.*/
public class ArtistAuctionItem {
    private String artisttitle ;
    private String nowbiddingStr;
    private String timeStr ;
    private String auctionStr;
    private String auckeyStr;

    public ArtistAuctionItem() {

    }

    public void setTitle(String title) {
        artisttitle = title ;
    }
    public void setNowbidding(String nowbidding) {
        nowbiddingStr = nowbidding ;
    }
    public void setTime(String time) {
        timeStr = time ;
    }
    public void setAuction(String auction) {
        auctionStr = auction ;
    }
    public void setAuckey(String auckey) {
        auckeyStr = auckey;
    }

    public String getTitle() {
        return this.artisttitle ;
    }
    public String getNowbidding() {
        return this.nowbiddingStr ;
    }
    public String getTime() {
        return this.timeStr ;
    }
    public String getAuction() {
        return this.auctionStr ;
    }
    public String getAuckey() {
        return this.auckeyStr;
    }
}