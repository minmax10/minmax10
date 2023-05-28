package com.famous.eatsy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.famous.eatsy.R;
import com.famous.eatsy.page.OrderActivity;
import com.famous.eatsy.page.SearchActivity;
import com.famous.eatsy.server.RetrofitConnector;
import com.famous.eatsy.server.ServiceApi;
import com.famous.eatsy.server.model.StoreModel;
import com.famous.eatsy.server.model.StoreRes;
import com.famous.eatsy.server.model.TableModel;
import com.famous.eatsy.view.MenuView;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RestaurantDetailFragment extends Fragment {

    /**
     * 하단에서 끌어올릴 수 있는 매장 디테일
     */

    private StoreModel currentStore;

    private View rootView;
    private TableLayout tableView;
    LinearLayout menuLayout;

    private Context context;
    private RequestManager glideRequestManager;

    // 테이블 실시간 확인을 위한 리프레시 타임>>> 5000면 일단 충분
    private final static int REFRESH_TIME = 5000;
    Timer timer;

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 외부 웹서버의 이미지를 가져오기 위한 부분
        // 초기에 설정해두지 않으면 앱을 꺼도 이미지를 가져오려는 문제가 발생해 초기 설정함 2021.04.01
        glideRequestManager = Glide.with(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

        context = rootView.getContext();

        tableView = rootView.findViewById(R.id.restaurant_layout_table);

        initViews();
        return rootView;
    }

    private void initViews(){
        // QR 버튼, 매장 이용 버튼 처리 부분
        rootView.findViewById(R.id.restaurant_btn_qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(getActivity()).initiateScan();
            }
        });

        rootView.findViewById(R.id.restaurant_btn_visit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentStore == null) return;

                Intent intent = new Intent(context.getApplicationContext(), OrderActivity.class);
                intent.putExtra("current_store", currentStore);
                startActivity(intent);
            }
        });
    }

    public void onChangedRestaurant(int storeId, boolean isQrScan){
        // MapActivity에서 지도의 핀을 클릭했을때 접근하는 함수
        // 다른 매장을 클릭시 리프레시 해줘야함.
        if(currentStore == null || storeId != currentStore.getStoreId() || isQrScan){
            if(timer != null) timer.cancel();

            // 주기적으로 리프레시하도록 설정하는 부분
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    getServerData(storeId);
                }
            };

            timer.schedule(timerTask, 0, REFRESH_TIME);
        }
    }

    private void getServerData(int storeId){
        //서버에 매장 Id로 해당 매장 정보와 테이블 정보를 받아오는 부분
        Retrofit retrofit = RetrofitConnector.createRetrofit();
        Call<StoreRes> call = retrofit.create(ServiceApi.class).getStore(storeId);
        call.enqueue(new Callback<StoreRes>() {
            @Override
            public void onResponse(Call<StoreRes> call, Response<StoreRes> response) {
                if(response.isSuccessful()){
                    StoreRes result = response.body();
                    if(result.isSuc()){
                        //서버에서 데이터를 받아오면 View 설정 시작
                        currentStore = result.getStore().get(0);
                        setDetailViews();
                        setTableView();
                    }
                }
            }

            @Override
            public void onFailure(Call<StoreRes> call, Throwable t) {
                Log.d("Server", "onFailure: " + t.toString());
            }
        });
    }

    private void setDetailViews(){
        // 매장 정보 TextView 설정
        ((TextView)rootView.findViewById(R.id.restaurant_tv_name)).setText(currentStore.getStoreName());
        ((TextView)rootView.findViewById(R.id.restaurant_tv_address)).setText(currentStore.getStoreAddress());
        ((TextView)rootView.findViewById(R.id.restaurant_tv_introduce)).setText(currentStore.getStoreIntroduce());
        ((TextView)rootView.findViewById(R.id.restaurant_tv_time)).setText(currentStore.getStoreTimeSet());
        ((TextView)rootView.findViewById(R.id.restaurant_tv_starrate)).setText(currentStore.getStoreStarRate() + "");
        ((TextView)rootView.findViewById(R.id.restaurant_tv_emptytable)).setText(getEmptyTable());
        ((TextView)rootView.findViewById(R.id.restaurant_tv_waittime)).setText(currentStore.getStoreWaitTime() + "분");

        ImageView imageView = ((ImageView) rootView.findViewById(R.id.restaurant_iv_image));
        glideRequestManager.load(currentStore.getStoreImgUrl()).into(imageView);

        // 메뉴를 표시하기 위한 부분
        if(menuLayout == null){
            menuLayout = (rootView.findViewById(R.id.restaurant_menu));
        } else {
            menuLayout.removeAllViews();
        }
        MenuView menuView = new MenuView(context);
        menuView.setLayoutParams();
        menuView.setMenuModels(new ArrayList<>(currentStore.getMenu()));
        menuView.inflate(context);
        menuLayout.addView(menuView);

        // 매장 정보 미선택 시 표시되는 안내 문구를 가리는 부분
        rootView.findViewById(R.id.restaurant_label_hide).setVisibility(View.GONE);

    }

    private void setTableView(){
        // 매장 테이블 뷰를 설정하는 부분

        // 기존 테이블 뷰 초기화
        tableView.removeAllViews();
        if(currentStore.getTables() == null || currentStore.getTables().size() <=0){
            Toast.makeText(context, "테이블 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<TableModel> tableModels = currentStore.getTables();

        /**
         * 1. 테이블위치는 2차원 X와 Y 값으로 구성되며 서버에서 받아올 때 Y값의 오름차순>>> 반대로 하면 매장그림 다시 그려야했다...
         */
        TableRow[] tableRows = new TableRow[tableModels.get(tableModels.size()-1).getTableY() + 1];

        // 현재 Row(가로줄)와 해당 Row의 Column(가로줄의 테이블) 개수
        int currentRow = 0;
        int currentColumnCount = 0;
        for (int i = 0; i < tableModels.size(); i++) {
            TableModel tmp = tableModels.get(i);

            // 현재 가로줄과 현재 테이블 객체의 Y를 비교
            // 같지 않다면 현재 테이블 객체는 현재 가로줄에 존재하지 않음으로 다음 줄로 넘어감
            if(tmp.getTableY() == currentRow){

                // 새 가로줄에서 시작했을 때 실행
                if(tableRows[currentRow] == null) {
                    tableRows[currentRow] = new TableRow(context);
                    currentColumnCount = 0;
                }

                // 테이블 박스 뷰를 생성하는 부분
                View tmpView = new View(context);

                // 테이블 상태에 따라 다른 색으로 적용
                if(tmp.getTabelState().equals("사용중")) {
                    tmpView.setBackground(ContextCompat.getDrawable(context, R.drawable.table_item_using));
                } else if(tmp.getTabelState().equals("대기중")) {
                    tmpView.setBackground(ContextCompat.getDrawable(context, R.drawable.table_item_canuse));
                } else {
                    tmpView.setBackground(ContextCompat.getDrawable(context, R.drawable.box_table_recommand));
                }

                // 화면 크기에 맞춰 크기를 설정
                final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

                // getTableX()로 현재 테이블의 X 좌표를 받아와 설정
                // TableLayout의 layout_column 속성을 사용하기 위한 코드
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(tmp.getTableX());
                layoutParams.width = width;
                layoutParams.height = height;

                tmpView.setLayoutParams(layoutParams);

                // 현재 테이블 뷰를 현재 가로줄에 추가
                tableRows[currentRow].addView(tmpView);
                currentColumnCount ++;
            }else {
                // 해당 줄에 테이블이 없을 시 빈 줄로 띄우는 부분
                if(currentColumnCount == 0){
                    tableRows[currentRow] = new TableRow(context);
                    final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                    tableRows[currentRow].setPadding(0,height,0,0);
                }
                currentRow++;
                i--;
                currentColumnCount = 0;
            }
        }

        // 만들어진 테이블 모습을 뷰에 적용하는 부분
        for (int j = 0; j < tableRows.length; j++) {
            tableView.addView(tableRows[j]);
        }

        // 맨 아래줄 빈 칸 만들어주는 부분
        TableRow tableRow = new TableRow(context);
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        tableRow.setPadding(0,height,0,0);
        tableView.addView(tableRow);
    }

    private String getEmptyTable(){
        // 잔여좌석 표시란에 빈 좌석을 측정하는 부분
        String emptyTable = "";

        int usingTable = 0;

        for (TableModel table: currentStore.getTables()) {
            if(table.getTabelState().equals("사용중")) usingTable++;
        }

        emptyTable = currentStore.getTables().size() - usingTable + "/" + currentStore.getTables().size();

        return emptyTable;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}