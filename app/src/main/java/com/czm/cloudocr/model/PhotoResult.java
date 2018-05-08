package com.czm.cloudocr.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class PhotoResult extends DataSupport implements Serializable{
    private int id;
    private String uri;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    /**
     *
     * @param uri
     * @param text
     */
    public PhotoResult(String uri, String text) {
        this.uri = uri;
        this.text = text;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
