package com.slate.vit.vitslate;

/**
 * Created by Aayush Karwatkar on 30-Sep-15.
 * This class is used to store the data for each row in ListView.
 */
public class RowItemDownloaded {
    private int imageId;
    private String desc;

    public RowItemDownloaded(int imageId , String desc) {
        this.imageId = imageId;
        this.desc = desc;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public int getImageId() {
        return imageId;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}