package com.czm.cloudocr.model;

public class PhotoResult{
    private String uri;
    private String text;

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
