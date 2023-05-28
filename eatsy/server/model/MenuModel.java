package com.famous.eatsy.server.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MenuModel implements Serializable {
    @SerializedName("menu_id")
    private int menuId;

    @SerializedName("menu_name")
    private String menuName;

    @SerializedName("menu_detail")
    private String menuDetail;

    @SerializedName("menu_catalog")
    private String menuCatalog;

    @SerializedName("menu_price")
    private int menuPrice;

    @SerializedName("menu_make_time")
    private int menuMakeTime;

    @SerializedName("store_id")
    private String storeId;

    public MenuModel() {
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuDetail() {
        return menuDetail;
    }

    public void setMenuDetail(String menuDetail) {
        this.menuDetail = menuDetail;
    }

    public String getMenuCatalog() {
        return menuCatalog;
    }

    public void setMenuCatalog(String menuCatalog) {
        this.menuCatalog = menuCatalog;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }

    public int getMenuMakeTime() {
        return menuMakeTime;
    }

    public void setMenuMakeTime(int menuMakeTime) {
        this.menuMakeTime = menuMakeTime;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
