package com.famous.eatsy.server.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TableModel implements Serializable {

    @SerializedName("store_id")
    private int storeId;

    @SerializedName("table_id")
    private int tableId;

    @SerializedName("table_pos_x")
    private int tableX;

    @SerializedName("table_pos_y")
    private int tableY;

    //@SerializedName("table_set_id")
    //private int storeId;

    @SerializedName("table_state")
    private String tabelState;

    @SerializedName("table_waiting_time")
    private String tableWaitingTime;


    public TableModel() {
    }

    public TableModel(int storeId, int tableId) {
        this.storeId = storeId;
        this.tableId = tableId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getTableX() {
        return tableX;
    }

    public void setTableX(int tableX) {
        this.tableX = tableX;
    }

    public int getTableY() {
        return tableY;
    }

    public void setTableY(int tableY) {
        this.tableY = tableY;
    }

    public String getTabelState() {
        return tabelState;
    }

    public void setTabelState(String tabelState) {
        this.tabelState = tabelState;
    }

    public String getTableWaitingTime() {
        return tableWaitingTime;
    }

    public void setTableWaitingTime(String tableWaitingTime) {
        this.tableWaitingTime = tableWaitingTime;
    }
}
