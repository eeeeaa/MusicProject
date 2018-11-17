package com.tiger.user.musicproject.YoutubeLinkService;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainData{
    @SerializedName("info")
    public Info info;
    @SerializedName("url")
    public String URL;
    public class Info{
        @SerializedName("formats")
        private List<Format> format;

        public List<Format> getFormat() {
            return format;
        }
    }
    public class Format{
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }
    }
}