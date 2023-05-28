package com.famous.eatsy.page;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.famous.eatsy.R;
import com.famous.eatsy.server.RetrofitConnector;
import com.famous.eatsy.server.ServiceApi;
import com.famous.eatsy.server.model.StoreModel;
import com.famous.eatsy.server.model.StoreRes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    /**
     * 검색 화면
     */
    ArrayList<StoreModel> stores = new ArrayList<>();

    ArrayList<CheckBox> peopleChbs = new ArrayList<>();
    ArrayList<CheckBox> categoryChbs = new ArrayList<>();

    private static SearchActivity searchActivity;

    public static SearchActivity getInstance() {
        return searchActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchActivity = this;

        stores = (ArrayList<StoreModel>) getIntent().getSerializableExtra("stores");

        initView();
    }

    private void initView(){
        categoryChbs.add((CheckBox) findViewById(R.id.search_chb_korean));
        categoryChbs.add((CheckBox) findViewById(R.id.search_chb_chinese));
        categoryChbs.add((CheckBox) findViewById(R.id.search_chb_japanese));
        categoryChbs.add((CheckBox) findViewById(R.id.search_chb_western));

        peopleChbs.add((CheckBox) findViewById(R.id.search_chb_one));
        peopleChbs.add((CheckBox) findViewById(R.id.search_chb_two));
        peopleChbs.add((CheckBox) findViewById(R.id.search_chb_three));
        peopleChbs.add((CheckBox) findViewById(R.id.search_chb_overfour));

        // 뒤로가기 버튼 설정
        findViewById(R.id.search_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.search_btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPeopleFilter();
            }
        });
    }

    private void searchPeopleFilter(){

        /**
         * 인원수별 필터 적용
         * 인원수 별 필터는 서버의 getStoreEmpty API를 사용
         * 빈 테이블 대비 인원수 검색
          */

        int peopleCount = 0;

        for (int i = 0; i < peopleChbs.size(); i++) {
            if(peopleChbs.get(i).isChecked()){
                peopleCount = i+1;
            }
        }

        if(peopleCount == 0){
            searchCategoryFilter(stores);
        } else {
            Retrofit retrofit = RetrofitConnector.createRetrofit();
            Call<StoreRes> call = retrofit.create(ServiceApi.class).getStoreEmpty(peopleCount);
            call.enqueue(new Callback<StoreRes>() {
                @Override
                public void onResponse(Call<StoreRes> call, Response<StoreRes> response) {
                    if(response.isSuccessful()){
                        StoreRes result = response.body();
                        if(result.isSuc()){
                            // 인원 검색 이후에 카테고리를 적용
                            // 서버 접근에 겹치지 않도록 한 부분
                            searchCategoryFilter(new ArrayList<>(result.getStore()));
                        } else {
                            searchCategoryFilter(stores);
                        }
                    }
                }

                @Override
                public void onFailure(Call<StoreRes> call, Throwable t) {
                    Log.e("ERROR: ", t.toString());
                }
            });
        }
    }

    private void searchCategoryFilter(ArrayList<StoreModel> stores){

        // 카테고리(업종) 검색
        ArrayList<String> categories = new ArrayList<>();

        ArrayList<StoreModel> searchResult = new ArrayList<>();

        for (int i = 0; i < categoryChbs.size(); i++) {
            if (categoryChbs.get(i).isChecked()){
                categories.add(categoryChbs.get(i).getText().toString());
            }
        }

        if(categories.size() == 0){
            searchResult.addAll(stores);
        }else {
            for (StoreModel item: stores) {
                if(categories.contains(item.getStoreCategory())){
                    searchResult.add(item);
                }
            }
        }

        if(searchResult.size() == 0){
            Toast.makeText(this, "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
        intent.putExtra("search_result", searchResult);ㄴ
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // 필터 체크 시 글자 색을 변경

        // 필터 디자인은 Custom Checkbox를 이용
        if(isChecked){
            buttonView.setTextColor(Color.WHITE);
        } else {
            buttonView.setTextColor(Color.BLACK);
        }
    }

}