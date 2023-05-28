package com.famous.eatsy.page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.famous.eatsy.R;
import com.famous.eatsy.adapter.SearchResultRecyclerViewAdapter;
import com.famous.eatsy.page.MapActivity;
import com.famous.eatsy.page.SearchActivity;
import com.famous.eatsy.server.model.StoreModel;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    /**
     * 검색 결과를 표시해주는 Activity
     * RecyclerView 를 사용해 표시
     */

    ArrayList<StoreModel> searchResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        searchResult.addAll((ArrayList<StoreModel>)getIntent().getSerializableExtra("search_result"));

        initView();
        initRecyclerView();
    }

    private void initView(){
        ((TextView)findViewById(R.id.searchresult_tv_count)).setText("총 " + searchResult.size()+"건");
        findViewById(R.id.searchresult_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initRecyclerView(){
        //RecyclerView 설정 부분
        RecyclerView recyclerView = findViewById(R.id.searchresult_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SearchResultRecyclerViewAdapter adapter = new SearchResultRecyclerViewAdapter(searchResult);
        adapter.setOnItemClickListener(new SearchResultRecyclerViewAdapter.OnItemClickListener() {
            //Adapter 에서 생성한 아이템 클릭 리스너 설정
            @Override
            public void onItemClick(StoreModel storeModel, int position) {
                //Item 클릭 시 이전 검색화면을 종료하고, 맵 액티비티의 onSearchResultClick 함수를 호출
                SearchActivity.getInstance().finish();
                MapActivity.getInstance().onSearchResultClick(storeModel);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);
    }
}