package com.famous.eatsy.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableRow;

import androidx.core.content.ContextCompat;

import com.famous.eatsy.R;
import com.famous.eatsy.server.model.TableModel;

public class SeatView extends View {
    /**
     * 좌석 선택을 위한 CustomView
     */
    private TableModel item;
    private boolean isSelected = false;

    public SeatView(Context context) {
        super(context);
    }

    public TableModel getItem() {
        return item;
    }

    public void setItem(TableModel item) {
        this.item = item;
    }

    public int getItemId(){
        return item.getTableId();
    }

    public void setSize(){
        // 화면 크기에 맞춰 크기를 설정
        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        // getTableX()로 현재 테이블의 X 좌표를 받아와 설정
        // TableLayout의 layout_column 속성을 사용하기 위한 부분
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(item.getTableX());
        layoutParams.width = width;
        layoutParams.height = height;

        this.setLayoutParams(layoutParams);
    }

    public void setState(Context context){
        if(isSelected) return;

        if (item.getTabelState().equals("사용중")) {
            this.setBackground(ContextCompat.getDrawable(context, R.drawable.table_item_using));
        } else if (item.getTabelState().equals("대기중")) {
            this.setBackground(ContextCompat.getDrawable(context, R.drawable.table_item_canuse));
        } else {
            this.setBackground(ContextCompat.getDrawable(context, R.drawable.box_table_recommand));
        }
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(Context context, boolean selected) {
        // 좌석 선택시 체크 표시를 진행
        if(item.getTabelState().equals("사용중")) {
            return;
        }

        isSelected = selected;

        if(isSelected){
            this.setBackground(ContextCompat.getDrawable(context, R.drawable.box_table_selected));
        } else {
            this.setBackground(ContextCompat.getDrawable(context, R.drawable.table_item_canuse));
        }
    }
}
