package com.famous.eatsy.page;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.famous.eatsy.R;
import com.famous.eatsy.fragment.RestaurantDetailFragment;
import com.famous.eatsy.server.RetrofitConnector;
import com.famous.eatsy.server.ServiceApi;
import com.famous.eatsy.server.model.StoreModel;
import com.famous.eatsy.server.model.TableModel;
import com.famous.eatsy.server.model.TransTableRes;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Projection;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnCameraIdleListener {

    /**
     * 지도 액티비티
     */

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    // 마커 사이즈 변경은 이 부분에서 진행.
    private static final int MARKER_SIZE = 150;

    private Activity context = this;
    // 검색 결과화면에서의 동작을 위해 Singleton 방식
    private static MapActivity mapActivity;
    public static MapActivity getInstance(){
        return mapActivity;
    }
    private ArrayList<StoreModel> stores;

    public FloatingActionButton qrButton;

    BottomSheetBehavior bottomSheetBehavior;
    private RestaurantDetailFragment detailFragment;
    private DrawerLayout drawerLayout;

    LatLng leftBottom, rightTop;
    Point size;

    NaverMap naverMap;
    Projection projection;
    private FusedLocationSource locationSource;
    ArrayList<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapActivity = this;

        // Splash 에서 가져온 매장 목록을 받아온다
        stores = (ArrayList<StoreModel>) getIntent().getSerializableExtra("stores");
        if(stores == null) {
            stores = new ArrayList<>();
        }

        // 현재 위치 표시를 위한 코드
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // 메모리 절약을 위해 현재 기기의 좌 우 화면 크기를 미리 지정해야 함
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        initViews();
        initMapView();
    }

    private void initMapView(){
        // xml에 작성한 Map Fragment를 사용하기 위해 설정
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    private void initViews(){
        // 화면에 있는 View 들을 설정하는 부분
        drawerLayout = findViewById(R.id.map_layout_drawer);

        // QR 코드 스캔 버튼 설정
        qrButton = findViewById(R.id.map_btn_qr);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭 시 QR코드 외부 모듈을 이용해 스캔시작
                new IntentIntegrator(context).initiateScan();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        // 검색 화면 이동 설정
        FloatingActionButton searchButton = findViewById(R.id.map_btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("stores", stores);
                startActivity(intent);
            }
        });

        // 우측 열리는 메뉴 설정
        FloatingActionButton menuButton = findViewById(R.id.map_btn_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        // 하단 매장 디테일 화면을 설정
        // 하단 끌어올리는 화면은 BottomSheetBehavior, Fragment 를 사용 >>> 수정시 둘 다해야함.
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.map_fragment_restaurantdetail));

        // 화면이 끌어올려질 시 상단의 버튼들을 숨기는 부분
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    qrButton.setEnabled(false);
                    searchButton.setEnabled(false);
                    menuButton.setEnabled(false);
                }
                else if(newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    qrButton.setEnabled(true);
                    searchButton.setEnabled(true);
                    menuButton.setEnabled(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // 투명도 값을 적용해 자연스럽게 fadeout 효과
                qrButton.setAlpha(1 - slideOffset);
                searchButton.setAlpha(1 - slideOffset);
                menuButton.setAlpha(1 - slideOffset);
            }
        });

        detailFragment = (RestaurantDetailFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_restaurantdetail);
    }

    @Override
    public void onCameraIdle() {
        // 카메라 이동이 멈추면 진행되는 함수

        // 기존 마커를 제거
        for (Marker marker :
                markers) {
            marker.setMap(null);
        }

        // 현재 화면에 표시된 영역을 가져옴
        leftBottom = projection.fromScreenLocation(new PointF(0,size.y));
        rightTop = projection.fromScreenLocation(new PointF(size.x,0));

        LatLngBounds latLngBounds = new LatLngBounds(leftBottom, rightTop);

        // 매장 목록을 반복문 처리해 현재 화면 영역에 포함되는지 확인>>> 안했을 때 화면 겹침
        for (StoreModel item : stores) {
            LatLng currentLocation = new LatLng(item.getStoreLatitude(), item.getStoreLongitude());

            // 포함될경우 새 마커를 생성해 맵에 표시
            if(latLngBounds.contains(currentLocation)){

                Marker marker = new Marker();
                marker.setPosition(currentLocation);
                marker.setIcon(OverlayImage.fromResource(R.drawable.map_pin));
                marker.setWidth(MARKER_SIZE + 15);
                marker.setHeight(MARKER_SIZE);

                // 마커의 수치 표시를 위한 부분
                marker.setCaptionText(getEmptyTable(item));
                marker.setCaptionAligns(Align.TopRight);
                marker.setCaptionTextSize(15f);
                marker.setCaptionOffset(-45);

                marker.setMap(naverMap);

                // 마커 클릭 시 하단 디테일에 정보를 표시
                marker.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        detailFragment.onChangedRestaurant(item.getStoreId(), false);
                        return true;
                    }
                });
                markers.add(marker);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        // 지도가 준비되면 실행

        this.naverMap = naverMap;

        projection = this.naverMap.getProjection();
        naverMap.setLocationSource(locationSource);

        naverMap.addOnCameraIdleListener(this);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

    }

    private void onGetQrScan(String qrData){
        // 하단의 onActivityResult 함수를 먼저 확인

        /**
         * QR코드의 데이터는 Text 값으로 설정해 JSON, map으로 작성하던거 다 걷어내기!
         */
        if(qrData.contains("table_id")){
            try {
                JSONObject jsonData = new JSONObject(qrData);
                int storeId = jsonData.getInt("store_id");
                int tableId = jsonData.getInt("table_id");
                TableModel tableModel = new TableModel(storeId,tableId );

                // Server에 QR코드 데이터를 전송하는 부분
                Retrofit retrofit = RetrofitConnector.createRetrofit();
                Call<TransTableRes> call = retrofit.create(ServiceApi.class).transTable(tableModel);
                call.enqueue(new Callback<TransTableRes>() {
                    @Override
                    public void onResponse(Call<TransTableRes> call, Response<TransTableRes> response) {
                        if(response.isSuccessful()){
                            TransTableRes result = response.body();

                            // Server 데이터 전송이 정상적으로 처리되었는지 확인하는 부분
                            if(result.isSuc()){
                                Toast.makeText(MapActivity.this, "테이블 이용이 처리되었습니다.", Toast.LENGTH_SHORT).show();
                                detailFragment.onChangedRestaurant(storeId, true);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            } else {
                                Toast.makeText(MapActivity.this, "잘못된 QR코드입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TransTableRes> call, Throwable t) {
                        Log.d("Server", "onFailure: " + t.toString());
                    }
                });
            } catch (JSONException e) {
                Log.e("error", e.getMessage());
            }

        } else {
            Toast.makeText(MapActivity.this, "잘못된 QR코드입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSearchResultClick(StoreModel storeModel){
        // 검색결과 클릭 시 하단 매장을 변경하고, 지도 위치를 이동시킴
        detailFragment.onChangedRestaurant(storeModel.getStoreId(), false);
        LatLng point = new LatLng(storeModel.getStoreLatitude(), storeModel.getStoreLongitude());
        LatLngBounds bounds = new LatLngBounds(point, point);
        CameraUpdate cameraUpdate = CameraUpdate.fitBounds(bounds)
                .animate(CameraAnimation.Fly, 1500);

        naverMap.moveCamera(cameraUpdate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // QR 스캔 후 원래 화면으로 돌아와 QR코드 값을 받는 부분
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null){
                //Cancelled
            }else {
                //Scanned
                String contents = result.getContents();
                onGetQrScan(contents);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private String getEmptyTable(StoreModel currentStore){
        // 잔여좌석 표시란에 빈 좌석을 측정하는 부분
        String emptyTable = "";

        int usingTable = 0;

        for (TableModel table: currentStore.getTables()) {
            if(table.getTabelState().equals("사용중")) usingTable++;
        }

        emptyTable = currentStore.getTables().size() - usingTable + "";

        return emptyTable;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 위치 권한을 사용하기 위한 부분
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            } else {
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼 클릭 시 매장 상세 화면이 올라와있으면 상세 화면을 내려줘야함!!!!
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            super.onBackPressed();
        } else if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
           ((NestedScrollView) detailFragment.getView().findViewById(R.id.restaurant_layout_scroll)).scrollTo(0,0);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}