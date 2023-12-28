package com.catapi.c4.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddFavouritesData {

    @SerializedName("image_id")
    @Expose
    private String imageId;
    @SerializedName("sub_id")
    @Expose
    private String subId;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }
}
