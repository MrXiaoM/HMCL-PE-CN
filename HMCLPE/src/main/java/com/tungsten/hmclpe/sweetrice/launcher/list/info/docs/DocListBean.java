package com.tungsten.hmclpe.sweetrice.launcher.list.info.docs;

import java.util.ArrayList;

public class DocListBean {

    private final String category;
    private final ArrayList<Doc> item;

    public DocListBean() {
        this("", new ArrayList<>());
    }

    public DocListBean(String category, ArrayList<Doc> item) {
        this.category = category;
        this.item = item;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<Doc> getItem() {
        return item;
    }
}
