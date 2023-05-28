package com.famous.eatsy.server;

import com.famous.eatsy.server.model.StoreRes;
import com.famous.eatsy.server.model.TableModel;
import com.famous.eatsy.server.model.TransTableRes;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ServiceApi {

    // Server의 Api를 사용할 수 있도록 설정

    // Table 상태 변경
    @POST("/table/transTable")
    Call<TransTableRes> transTable(@Body TableModel marketModel);

    // 매장 정보 가져오기
    @GET("/store/getStore/{store_id}")
    Call<StoreRes> getStore(@Path("store_id") int storeId);

    // 매장 정보 가져오기
    @GET("/store/getStore")
    Call<StoreRes> getStore();

    @GET("/store/getStoreEmpty/{emptyTable}")
    Call<StoreRes> getStoreEmpty(@Path("emptyTable") int emptyTable);

    @Multipart
    @POST("/test/add")
    Call<ResponseBody> upload(@Part MultipartBody.Part file);

}