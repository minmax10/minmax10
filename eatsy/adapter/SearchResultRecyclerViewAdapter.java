package com.famous.eatsy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.famous.eatsy.R;
import com.famous.eatsy.server.model.StoreModel;
import com.famous.eatsy.server.model.TableModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SearchResultRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultRecyclerViewAdapter.ItemViewHolder> {

    /**
     * 검색 결과를 표시하기 위한 RecyclerView Adapter 
     * recycleritem_searchresult 레이아웃 구현부
     */
    private View view;
    public ArrayList<StoreModel> itemList = new ArrayList<StoreModel>();
    private Context context;

    public SearchResultRecyclerViewAdapter(ArrayList<StoreModel> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        view = LayoutInflater.from(context).inflate(R.layout.recycleritem_searchresult, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {
        itemViewHolder.onBind(itemList.get(position));
        itemViewHolder.itemViewHolder = itemViewHolder;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).getStoreId();
    }

    public interface OnItemClickListener {
        void onItemClick(StoreModel storeModel, int position);
    }

    // Item Click 시 이벤트 처리를 위해 리스너를 생성
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ItemViewHolder itemViewHolder;

        private ViewGroup layout;
        private ImageView image;
        private TextView nameView, starRateView, addressView, detailView, timeView, eatTimeView, emptyTableView;

        private String imageUrl;
        private MultiTransformation multiTransformation;

        ItemViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.result_item_layout);

            image = itemView.findViewById(R.id.result_item_iv_image);
            multiTransformation = new MultiTransformation(new CenterCrop());
            nameView = itemView.findViewById(R.id.result_item_tv_name);
            starRateView = itemView.findViewById(R.id.result_item_tv_starrate);
            addressView = itemView.findViewById(R.id.result_item_tv_address);
            detailView = itemView.findViewById(R.id.result_item_tv_introduce);
            timeView = itemView.findViewById(R.id.result_item_tv_time);
            eatTimeView = itemView.findViewById(R.id.result_item_tv_eattime);
            emptyTableView = itemView.findViewById(R.id.result_item_tv_emptytable);

        }

        void onBind(final StoreModel item) {
            // Set Views
            setViewOnBind(item);
        }

        // 생성된 View 에 데이터를 표시
        private void setViewOnBind(StoreModel item) {
            nameView.setText(item.getStoreName());
            starRateView.setText(item.getStoreStarRate()+"");
            addressView.setText(item.getStoreAddress());
            detailView.setText(item.getStoreIntroduce());
            timeView.setText(item.getStoreTimeSet());
            emptyTableView.setText(getEmptyTable(item));

            imageUrl = item.getStoreImgUrl();
            Glide.with(context).load(imageUrl).apply(RequestOptions.bitmapTransform(multiTransformation)).into(image);

            // 아이템 클릭 시 리스너 객체의 클릭 이벤트를 실행
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(item, getAdapterPosition());
                }
            });

        }

        private String getEmptyTable(StoreModel currentStore){
            // 잔여좌석 표시란에 빈 좌석을 측정하는 부분
            String emptyTable = "";

            int usingTable = 0;

            for (TableModel table: currentStore.getTables()) {
                if(table.getTabelState().equals("사용중")) usingTable++;
            }

            emptyTable = currentStore.getTables().size() - usingTable + "/" + currentStore.getTables().size();

            return emptyTable;
        }
    }
}
