package com.czm.cloudocr.model;

public class HistoryResult {
    private String id;
    private String imgText;
    private String date;
    private String imgPath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgText() {
        return imgText;
    }

    public void setImgText(String imgText) {
        this.imgText = imgText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public String toString() {
        return "HistoryResult{" +
                "id=" + id +
                ", imgText='" + imgText + '\'' +
                ", date='" + date + '\'' +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }
}
