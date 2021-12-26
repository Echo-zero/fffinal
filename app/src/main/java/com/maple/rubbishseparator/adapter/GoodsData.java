package com.maple.rubbishseparator.adapter;

public class GoodsData {
    private int goodsImgUrl;
    private String goodsName;
    private String goodsPrice;

    public String getGoodsName(){
        return this.goodsName;
    }
    public void setGoodsName(String s){
        this.goodsName=s;
    }
    public String getGoodsPrice(){
        return this.goodsPrice;
    }
    public void setGoodsPrice(String s){
        this.goodsPrice=s;
    }
    public int getGoodsImgUrl(){
        return this.goodsImgUrl;
    }
    public void setGoodsImgUrl(int s){
        this.goodsImgUrl=s;
    }
}
