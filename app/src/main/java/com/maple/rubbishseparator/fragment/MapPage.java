package com.maple.rubbishseparator.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.maple.rubbishseparator.R;
import com.maple.rubbishseparator.adapter.GoodsAdapter;
import com.maple.rubbishseparator.adapter.GoodsData;

import java.util.ArrayList;
import java.util.List;

public class MapPage extends Fragment{
    private View rootview;
    private ListView listView;
    private List<GoodsData> goodsData;
    private GoodsAdapter goodsAdapter;
    public static String name[]=new String[]{"牛肉粒","薄脆饼干","咸蛋黄拌面","猪肉条","坚果","葡萄干","山楂条","桃酥","瓜子仁","美式薯条","炒蚕豆","奥尔良鸡腿","香辣鱼块","泡椒凤爪"};
    public static String price[]=new String[]{"20积分","30积分","16积分","30积分","11积分","25积分","20积分","25积分","20积分","20积分","15积分","20积分","17积分","19积分"};
    public static int ImgUrl[]=new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j,R.drawable.k,R.drawable.l,R.drawable.m,R.drawable.n,};

    public void getGoods(){
        for(int i=0;i<14;i++){
            GoodsData data=new GoodsData();
            data.setGoodsName(name[i]);
            data.setGoodsPrice(price[i]);
            data.setGoodsImgUrl(ImgUrl[i]);
            goodsData.add(data);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_map_page, container, false);
            listView=rootview.findViewById(R.id.listview_map);
            goodsData=new ArrayList<GoodsData>();
            getGoods();
            goodsAdapter=new GoodsAdapter(this.getActivity(),goodsData);
            listView.setAdapter(goodsAdapter);
            goodsAdapter.notifyDataSetChanged();
        }
        return rootview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}