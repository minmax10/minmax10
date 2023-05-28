package com.famous.eatsy.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.famous.eatsy.server.model.MenuModel;

import java.util.ArrayList;

public class MenuView extends LinearLayout {
    /**
     * 메뉴를 표시하기 위한 Custom View
     * MenuView는 큰 틀 하위의 대분류 -> 메뉴로 구성
     * MenuModel 리스트를 돌며 같은 대분류의 메뉴들을 묶는 방식으로 진행
     */

    ArrayList<MenuModel> menuModels = new ArrayList<>();
    ArrayList<TextView> catalogs = new ArrayList<>();

    private OnMenuClickListener onMenuClickListener;

    public MenuView(Context context) {
        super(context);
    }

    public ArrayList<MenuModel> getMenuModels() {
        return menuModels;
    }

    public void setMenuModels(ArrayList<MenuModel> menuModels) {
        this.menuModels = menuModels;
    }

    public void setLayoutParams(){
        this.setLayoutParams( new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setOrientation(VERTICAL);
    }

    public OnMenuClickListener getOnMenuClickListener() {
        return onMenuClickListener;
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    public void inflate(Context context){

        for (int i = 0; i < menuModels.size(); i++) {
            MenuModel item = menuModels.get(i);

            // 현재 메뉴 대분류가 이전 메뉴 모델과 다를 경우 메뉴 대분류를 생성
            if(catalogs.size() == 0  || !item.getMenuCatalog().equals(menuModels.get(i-1).getMenuCatalog())){
                TextView catalogView = new TextView(context);
                catalogView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                catalogView.setText(menuModels.get(i).getMenuCatalog());
                catalogView.setTextSize(20);
                catalogView.setPadding(30, 10,0,10);
                catalogView.setBackgroundColor(Color.LTGRAY);
                catalogView.setTextColor(Color.BLACK);
                catalogs.add(catalogView);

                this.addView(catalogView);
            }

            /**
             * 대분류 하위의 메뉴 뷰를 생성
             * 메뉴 뷰는 이름, 조리시간, 가격으로 구성
              */
            LinearLayout menuView = new LinearLayout(context);
            menuView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            menuView.setOrientation(HORIZONTAL);
            menuView.setPadding(35,10,35,10);

            TextView nameView = new TextView(context);
            nameView.setTextColor(Color.BLACK);
            nameView.setText(item.getMenuName());
            nameView.setTextSize(20);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            nameParams.weight = 3;
            nameView.setLayoutParams(nameParams);
            menuView.addView(nameView);

            TextView timeView = new TextView(context);
            timeView.setTextColor(Color.GRAY);
            timeView.setText(item.getMenuMakeTime() + "분");
            timeView.setTextSize(15);
            LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            timeParams.weight = 1;
            timeParams.setMarginEnd(10);
            timeView.setLayoutParams(timeParams);
            timeView.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
            menuView.addView(timeView);

            TextView priceView = new TextView(context);
            priceView.setTextColor(Color.BLACK);
            priceView.setText(item.getMenuPrice() + "원");
            priceView.setTextSize(20);
            LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            priceParams.weight = 1;
            nameView.setLayoutParams(priceParams);
            menuView.addView(priceView);

            menuView.setOnClickListener((v) -> {this.onMenuClickListener.OnMenuClick(v, item);});
            this.addView(menuView);
        }
    }

    public interface OnMenuClickListener{
        void OnMenuClick(View v, MenuModel menu);
    }
}
