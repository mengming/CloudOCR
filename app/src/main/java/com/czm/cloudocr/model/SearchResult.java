package com.czm.cloudocr.model;

public class SearchResult {
    private String ObjUrl;
    private String ThumbnailUrl;
    private String fromUrl;

    public String getObjUrl() {
        return ObjUrl;
    }

    public void setObjUrl(String objUrl) {
        ObjUrl = objUrl;
    }

    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        ThumbnailUrl = thumbnailUrl;
    }

    public String getFromUrl() {
        return fromUrl;
    }

    public void setFromUrl(String fromUrl) {
        this.fromUrl = fromUrl;
    }
}
