package com.maple.rubbishseparator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.maple.rubbishseparator.R;

import java.util.ArrayList;
import java.util.List;

public class GoodsAdapter extends BaseAdapter{
    private List<GoodsData> datas=new ArrayList<GoodsData>();
    private Context context;
    private LayoutInflater layoutInflater;

    public GoodsAdapter(Context context,List<GoodsData> datas){
        this.context=context;
        this.datas=datas;
        this.layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public GoodsData getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            //找到布局文件
            view=layoutInflater.inflate(R.layout.goods_item, null);
            view.setTag(new GoodsAdapter.ViewHolder(view));
        }
        initViews(getItem(i),(GoodsAdapter.ViewHolder) view.getTag());
        return view;
    }

    private void initViews(GoodsData data, GoodsAdapter.ViewHolder holder){
        holder.goods_img.setImageResource(data.getGoodsImgUrl());
        holder.goods_name.setText(data.getGoodsName());
        holder.goods_price.setText(data.getGoodsPrice());
        holder.goods_button.setText("兑换");
    }

    protected class ViewHolder {
        private ImageView goods_img;
        private TextView goods_name;
        private TextView goods_price;
        private Button goods_button;
        public ViewHolder(View view) {
            goods_img = (ImageView) view.findViewById(R.id.goods_img);
            goods_name = (TextView) view.findViewById(R.id.goods_name);
            goods_price = (TextView) view.findViewById(R.id.goods_price);
            goods_button=(Button) view.findViewById(R.id.goods_button);
        }
    }
}
