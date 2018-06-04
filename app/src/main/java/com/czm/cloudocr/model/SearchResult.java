package com.czm.cloudocr.model;

import java.io.Serializable;

public class SearchResult implements Serializable{
    private String objURL;
    private String thumbnailURL;
    private String fromURL;

    public String getObjURL() {
        return objURL;
    }

    public void setObjURL(String objURL) {
        this.objURL = objURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getFromURL() {
        return fromURL;
    }

    public void setFromURL(String fromURL) {
        this.fromURL = fromURL;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "objURL='" + objURL + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                ", fromURL='" + fromURL + '\'' +
                '}';
    }
}
