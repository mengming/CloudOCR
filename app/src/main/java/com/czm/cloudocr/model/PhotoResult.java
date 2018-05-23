package com.czm.cloudocr.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class PhotoResult extends DataSupport implements Serializable{
    private int id;
    @SerializedName("id")
    private String remoteId;
    private String uri;
    private String rootUri;
    private String text;
    private String date;

    private Boolean isCloud;

    public PhotoResult() {
    }

    public PhotoResult(String uri, String rootUri, String text) {
        this.uri = uri;
        this.rootUri = rootUri;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRootUri() {
        return rootUri;
    }

    public void setRootUri(String rootUri) {
        this.rootUri = rootUri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getCloud() {
        return isCloud;
    }

    public void setCloud(Boolean cloud) {
        isCloud = cloud;
    }

    @Override
    public String toString() {
        return "PhotoResult{" +
                "id=" + id +
                ", remoteId='" + remoteId + '\'' +
                ", uri='" + uri + '\'' +
                ", rootUri='" + rootUri + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", isCloud=" + isCloud +
                '}';
    }
}
