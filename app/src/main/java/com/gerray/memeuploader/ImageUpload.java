package com.gerray.memeuploader;

import android.net.Uri;

public class ImageUpload {
    private String imName;
    private String mImageUrl;

    public ImageUpload() {
        //Empty Constructor
    }

    public ImageUpload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        imName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return imName;
    }

    public void setName(String name) {
        imName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
