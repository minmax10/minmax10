package com.famous.eatsy.server.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StoreModel implements Serializable {
    @SerializedName("store_id")
    private int storeId;

    @SerializedName("store_name")
    private String storeName;

    @SerializedName("store_detail")
    private String storeIntroduce;

    @SerializedName("store_latitude")
    private float storeLatitude;

    @SerializedName("store_longitude")
    private float storeLongitude;

    @SerializedName("store_address")
    private String storeAddress;

    @SerializedName("store_time_set")
    private String storeTimeSet;

    @SerializedName("store_star_rate")
    private float storeStarRate;

    @SerializedName("store_wait_time")
    private String storeWaitTime;

    @SerializedName("store_tel")
    private String storeTel;

    @SerializedName("store_table_sum")
    private String storeTableCount;

    @SerializedName("store_img_url")
    private String storeImgUrl;

    @SerializedName("store_catagory")
    private String storeCategory;

    @SerializedName("tables")
    private List<TableModel> tables;

    @SerializedName("menus")
    private List<MenuModel> menu;

    public StoreModel() {
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreIntroduce() {
        return storeIntroduce;
    }

    public void setStoreIntroduce(String storeIntroduce) {
        this.storeIntroduce = storeIntroduce;
    }


    public String getStoreTel() {
        return storeTel;
    }

    public void setStoreTel(String storeTel) {
        this.storeTel = storeTel;
    }

    public String getStoreTableCount() {
        return storeTableCount;
    }

    public void setStoreTableCount(String storeTableCount) {
        this.storeTableCount = storeTableCount;
    }

    public String getStoreImgUrl() {
        return storeImgUrl;
    }

    public void setStoreImgUrl(String storeImgUrl) {
        this.storeImgUrl = storeImgUrl;
    }

    public float getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(float storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public float getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(float storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreWaitTime() {
        return storeWaitTime;
    }

    public void setStoreWaitTime(String storeWaitTime) {
        this.storeWaitTime = storeWaitTime;
    }

    public List<TableModel> getTables() {
        return tables;
    }

    public void setTables(List<TableModel> tables) {
        this.tables = tables;
    }

    public String getStoreTimeSet() {
        return storeTimeSet;
    }

    public void setStoreTimeSet(String storeTimeSet) {
        this.storeTimeSet = storeTimeSet;
    }

    public float getStoreStarRate() {
        return storeStarRate;
    }

    public void setStoreStarRate(float storeStarRate) {
        this.storeStarRate = storeStarRate;
    }

    public String getStoreCategory() {
        return storeCategory;
    }

    public void setStoreCategory(String storeCategory) {
        this.storeCategory = storeCategory;
    }

    public List<MenuModel> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuModel> menu) {
        this.menu = menu;
    }
}
