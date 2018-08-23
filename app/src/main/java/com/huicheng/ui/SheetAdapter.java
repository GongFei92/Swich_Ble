package com.huicheng.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huicheng.R;

import java.util.List;


public class SheetAdapter extends RecyclerView.Adapter {

    private List<Book> list;

    public SheetAdapter(List<Book> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sheet_item_layout, parent, false);
        return new sheetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        sheetViewHolder vh = (sheetViewHolder) holder;

        vh.getTv_sheetRow1().setText(list.get(position).getName());
        vh.getTv_sheetRow2().setText(list.get(position).getPassword());
        vh.getTv_sheetRow3().setText(list.get(position).getPower());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class sheetViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final TextView tv_sheetRow1;
        public final TextView tv_sheetRow2;
        public final TextView tv_sheetRow3;

        public sheetViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            tv_sheetRow1 = (TextView) itemView.findViewById(R.id.tv_sheetRow1);
            tv_sheetRow2 = (TextView) itemView.findViewById(R.id.tv_sheetRow2);
            tv_sheetRow3 = (TextView) itemView.findViewById(R.id.tv_sheetRow3);
        }

        public TextView getTv_sheetRow1() {
            return tv_sheetRow1;
        }

        public TextView getTv_sheetRow2() {
            return tv_sheetRow2;
        }

        public TextView getTv_sheetRow3() {
            return tv_sheetRow3;
        }
    }
}

