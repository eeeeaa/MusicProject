package com.tiger.user.musicproject.FavoriteService;

import com.google.gson.annotations.SerializedName;

public class FavoriteModel {
    @SerializedName("videoName")
    public String videoName;
    @SerializedName("URLthumbnail")
    public String thumbnail;
    @SerializedName("videoId")
    public String videoId;
}
