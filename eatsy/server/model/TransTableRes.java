package com.famous.eatsy.server.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransTableRes {

    // 서버로 보낸 요청이 성공했는지 여부
    @SerializedName("suc")
    private boolean suc;

    public boolean isSuc() {
        return suc;
    }
}
