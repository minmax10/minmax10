package com.famous.eatsy.page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.famous.eatsy.R;
import com.famous.eatsy.server.model.MenuModel;
import com.famous.eatsy.server.model.StoreModel;
import com.famous.eatsy.server.model.TableModel;
import com.famous.eatsy.view.MenuView;
import com.famous.eatsy.view.SeatView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 주문 화면입니다.
     * 테이블 선택, 메뉴 선택 부분이 포함되어있습니다.
     */

    int peopleCount = 1;

    TableLayout tableView;
    StoreModel currentStore;

    ArrayList<SeatView> selectedSeatList = new ArrayList<>();
    ArrayList<MenuModel> selectedMenuList = new ArrayList<>();

    View confirmView;
    LinearLayout menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        currentStore = (StoreModel) getIntent().getSerializableExtra("current_store");

        initViews();
        initMenuView();
        initTableView(tableView);
    }

    private void initViews() {
        // 인원 설정을 위한 부분
        TextView peopleCountView = findViewById(R.id.order_tv_people);

        findViewById(R.id.order_btn_people_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (peopleCount >= 9) {
                    peopleCount = 9;
                } else {
                    peopleCount++;
                }
                peopleCountView.setText(peopleCount + "");
            }
        });

        findViewById(R.id.order_btn_people_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (peopleCount > 1) {
                    peopleCount--;
                }
                for (SeatView item: selectedSeatList) {
                    item.setSelected(getApplicationContext(), false);
                }
                selectedSeatList = new ArrayList<>();
                peopleCountView.setText(peopleCount + "");
            }
        });

        tableView = findViewById(R.id.order_table);

        findViewById(R.id.order_btn_back).setOnClickListener((v -> {finish();}));

        // 다음 버튼 클릭 시 메뉴, 좌석 선택여부를 확인
        findViewById(R.id.order_btn_next).setOnClickListener((v) -> {

            if(selectedMenuList.size() == 0){
                Toast.makeText(this, "메뉴를 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else if(selectedSeatList.size() == 0){
                Toast.makeText(this, "좌석을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }else {
                setConfirmView();
            }
        });

        confirmView = findViewById(R.id.order_confirm);
        menus = findViewById(R.id.confirm_menu);
    }

    private void initMenuView(){
        // 메뉴뷰를 동일하게 표시합니다. 선택 기능 포함
        LinearLayout menuLayout = (findViewById(R.id.order_menu));
        MenuView menuView = new MenuView(this);
        menuView.setLayoutParams();
        menuView.setMenuModels(new ArrayList<>(currentStore.getMenu()));

        // 선택 기능
        menuView.setOnMenuClickListener(new MenuView.OnMenuClickListener() {
            @Override
            public void OnMenuClick(View v, MenuModel menu) {
                if(selectedMenuList.contains(menu)){
                    selectedMenuList.remove(menu);
                    v.setBackgroundColor(Color.WHITE);
                } else {
                    selectedMenuList.add(menu);
                    v.setBackgroundColor(getResources().getColor(R.color.sub_color_orange));
                }
            }
        });
        menuView.inflate(this);
        menuLayout.addView(menuView);
    }

    private void initTableView(TableLayout tableView) {
        // 선택이 포함된 테이블을 표시
        // 선택기능을 표시하기 위해 SeatView 라는 CustomView를 만들었습니다.
        tableView.removeAllViews();
        List<TableModel> tableModels = currentStore.getTables();

        TableRow[] tableRows = new TableRow[tableModels.get(tableModels.size() - 1).getTableY() + 1];

        int currentRow = 0;
        int currentColumnCount = 0;
        for (int i = 0; i < tableModels.size(); i++) {
            TableModel tmp = tableModels.get(i);

            if (tmp.getTableY() == currentRow) {

                if (tableRows[currentRow] == null) {
                    tableRows[currentRow] = new TableRow(this);
                    currentColumnCount = 0;
                }

                SeatView tmpView = new SeatView(this);
                tmpView.setItem(tmp);
                tmpView.setSize();

                // 주문 최종 확인 화면 (하단 표시)의 테이블 표시라면 선택된 좌석을 보여줌
                if(tableView.getId() == R.id.confirm_table){
                    for (int j = 0; j < selectedSeatList.size(); j++) {
                        if(selectedSeatList.get(j).getItemId() == tmp.getTableId()){
                            tmpView.setSelected(this, true);
                            break;
                        }
                    }
                } else {
                    tmpView.setOnClickListener(this);
                }

                tmpView.setState(this);

                // 현재 테이블 뷰를 현재 가로줄에 추가
                tableRows[currentRow].addView(tmpView);
                currentColumnCount++;
            } else {
                // 해당 줄에 테이블이 없을 시 빈 줄로 띄우는 부분
                if (currentColumnCount == 0) {
                    tableRows[currentRow] = new TableRow(this);
                    final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                    tableRows[currentRow].setPadding(0, height, 0, 0);
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
        TableRow tableRow = new TableRow(this);
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        tableRow.setPadding(0, height, 0, 0);
        tableView.addView(tableRow);
    }

    private void setConfirmView(){
        // 최종 확인 화면을 표시
        confirmView.setVisibility(View.VISIBLE);
        menus.removeAllViews();

        ((TextView)findViewById(R.id.confirm_tv_people)).setText(peopleCount + "");

        String tableName =  selectedSeatList.get(0).getItem().getTableY()
                + "-"
                + selectedSeatList.get(0).getItem().getTableX();
        ((TextView) findViewById(R.id.confirm_tv_table)).setText(tableName + "...");

        ((TextView) findViewById(R.id.confirm_tv_count)).setText("총 " + selectedMenuList.size() + "개");

        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        int amount = 0;
        int cookTime = 0;

        // 선택한 메뉴를 표시
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (MenuModel item: selectedMenuList) {
            amount += item.getMenuPrice();

            LinearLayout menuView = new LinearLayout(this);
            menuView.setLayoutParams(layoutParams);
            menuView.setOrientation(LinearLayout.HORIZONTAL);

            TextView nameView = new TextView(this);
            nameView.setText(item.getMenuName() + " X 1");
            nameView.setTextColor(Color.GRAY);
            nameView.setTextSize(15);
            viewParams.weight = 1;
            nameView.setLayoutParams(viewParams);

            TextView priceView = new TextView(this);
            priceView.setText(decimalFormat.format(item.getMenuPrice()) + " 원");
            priceView.setTextSize(15);
            priceView.setTextColor(Color.BLACK);
            priceView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            viewParams.weight = 1;
            priceView.setLayoutParams(viewParams);

            menuView.addView(nameView);
            menuView.addView(priceView);

            menus.addView(menuView);

            cookTime += item.getMenuMakeTime();
        }
        // 최종 금액
        ((TextView) findViewById(R.id.confirm_tv_amount)).setText(decimalFormat.format(amount) + " 원");

        // 테이블 표시(선택된 테이블 포함)
        initTableView(findViewById(R.id.confirm_table));

        // 최종 시간
        ((TextView)findViewById(R.id.confirm_tv_cooktime)).setText(cookTime + "분");

        findViewById(R.id.confirm_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 결제 버튼 클릭시 최종 화면으로 이동
        findViewById(R.id.confirm_btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResultActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // 테이블 클릭 시 발생
        if(selectedSeatList.contains((SeatView)v)){
            selectedSeatList.remove((SeatView) v);
            ((SeatView)v).setSelected(this, false);
        } else {
            if(peopleCount == selectedSeatList.size()){
                return;
            }
            selectedSeatList.add((SeatView) v);
            ((SeatView)v).setSelected(this, true);
        }
    }

    @Override
    public void onBackPressed() {
        if(confirmView.getVisibility() == View.VISIBLE){
            confirmView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}