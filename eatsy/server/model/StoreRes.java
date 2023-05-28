package com.famous.eatsy.server.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoreRes {

    @SerializedName("suc")
    private boolean isSuc;

    /*@SerializedName("tables")
    private List<TableModel> tableList;*/

    @SerializedName("store")
    private List<StoreModel> stores;

    //public List<TableModel> getTableList(){return tableList;}

    public boolean isSuc() {
        return isSuc;
    }

    public List<StoreModel> getStore() {
        return stores;
    }
}
