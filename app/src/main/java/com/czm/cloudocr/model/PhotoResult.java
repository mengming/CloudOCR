package com.czm.cloudocr.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class PhotoResult extends DataSupport implements Serializable{
    private int id;
    private String uri;
    private String rootUri;
    private String text;

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
}
