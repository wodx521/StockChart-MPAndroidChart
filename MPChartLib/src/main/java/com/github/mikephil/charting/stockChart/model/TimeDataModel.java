package com.github.mikephil.charting.stockChart.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/8.
 */
public class TimeDataModel implements Serializable {
    // 合约ID
    public String m_szInstrumentID;//16

    //时间戳
    private String timeMills;
    //现价
    private float nowPrice;
    //均价
    private float averagePrice;
    //分钟成交量
    private float volume;
    //今开
    private float open;
    //昨收
    private float preClose;
    private float per;
    private float cha;
    private float total;
    private float totalTransactionVolume;
    private float transactionVolume;
    private float transactionAmount;
    private float transactionPrice;
    private float totalTransactionAmount;
    private int color = 0xff000000;

    public String getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(String timeMills) {
        this.timeMills = timeMills;
    }

    public float getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(float nowPrice) {
        this.nowPrice = nowPrice;
    }

    public float getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(float averagePrice) {
        this.averagePrice = averagePrice;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getPreClose() {
        return preClose;
    }

    public void setPreClose(float preClose) {
        this.preClose = preClose;
    }

    public float getPer() {
        return per;
    }

    public void setPer(float per) {
        this.per = per;
    }

    public float getCha() {
        return cha;
    }

    public float getTotalTransactionVolume() {
        return totalTransactionVolume;
    }

    public void setTotalTransactionVolume(float totalTransactionVolume) {
        this.totalTransactionVolume = totalTransactionVolume;
    }

    public void setCha(float cha) {
        this.cha = cha;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getTransactionVolume() {
        return transactionVolume;
    }

    public void setTransactionVolume(float transactionVolume) {
        this.transactionVolume = transactionVolume;
    }

    public float getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(float transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public float getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(float transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    public float getTotalTransactionAmount() {
        return totalTransactionAmount;
    }

    public void setTotalTransactionAmount(float totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
    }

    @Override
    public boolean equals(Object obj) {
        TimeDataModel model = (TimeDataModel) obj;
        return getTimeMills().equals(model.getTimeMills());
    }
}
