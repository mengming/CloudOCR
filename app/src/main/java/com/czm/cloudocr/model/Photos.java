package com.czm.cloudocr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Phelps on 2018/3/26.
 */

public class Photos implements Serializable{
    private HashMap<String, List<String>> mGruopMap;
    private ArrayList<String> urls;

    public Photos(HashMap<String, List<String>> gruopMap, ArrayList<String> urls) {
        mGruopMap = gruopMap;
        this.urls = urls;
    }

    public HashMap<String, List<String>> getGruopMap() {
        return mGruopMap;
    }

    public void setGruopMap(HashMap<String, List<String>> gruopMap) {
        mGruopMap = gruopMap;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }
}
