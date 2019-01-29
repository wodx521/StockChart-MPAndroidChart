package com.github.mikephil.charting.stockChart.model.bean;


import android.util.Log;

import com.github.mikephil.charting.stockChart.model.KLineBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by loro on 2017/3/7.
 */

public class RSIEntity {

    private static final String TAG = "rsi";
    private ArrayList<Float> RSIs;

    /**
     * @param kLineBeens
     * @param n          几日
     */
    public RSIEntity(ArrayList<KLineBean> kLineBeens, int n) {
        this(kLineBeens, n, 100);
    }

    /**
     * @param kLineBeens
     * @param n          几日
     * @param defult     不足N日时的默认值
     */
    public RSIEntity(ArrayList<KLineBean> kLineBeens, int n, float defult) {
//        getRsi(n, kLineBeens, defult);
        countRSIdatas(kLineBeens, n);
    }

    private Float[] getAAndB(Integer start, Integer end, ArrayList<KLineBean> kLineBeens) {
        if (start < 0) {
            start = 0;
        }
        float sum = 0.0f;
        float dif = 0.0f;
        float closeT, closeY;
        Float[] abs = new Float[2];
        for (int i = start; i <= end; i++) {
            if (i > start) {
                closeT = kLineBeens.get(i).close;
                closeY = kLineBeens.get(i - 1).close;

                float c = closeT - closeY;
                if (c > 0) {
                    sum = sum + c;
                } else {
                    dif = dif + c;
                }
                dif = Math.abs(dif);
            }
        }

        abs[0] = sum;
        abs[1] = dif;
        return abs;
    }


    List<Float> diffList = new ArrayList<>();

    // 假如取6天的rsi值
    private void getRsi(int n, ArrayList<KLineBean> kLineBeens, float defult) {
        RSIs = new ArrayList<>();
        diffList.clear();
        for (int i = kLineBeens.size() - 1; i > 0; i--) {
            // 当天收盘价减去前一天收盘价
            float diff = kLineBeens.get(i).close - kLineBeens.get(i - 1).close;
            diffList.add(diff);
        }

        for (int i = 0; i < diffList.size(); i++) {
            float sumSum = 0.0f;
            float diffSum = 0.0f;
            int end;
            if (i + n > diffList.size()) {
                diffSum = 0;
            } else {
                end = i + n;
                for (int i1 = i; i1 < end; i1++) {
                    Float aFloat = diffList.get(i1);
                    if (aFloat >= 0) {
                        sumSum += aFloat;
                    } else {
                        diffSum += aFloat;
                    }
                }
            }


            float rsi;
            if (diffSum == 0) {
                rsi = 100;
            } else {
                rsi = 100 * (sumSum / Math.abs(diffSum)) / (1 + sumSum / Math.abs(diffSum));
            }
            RSIs.add(rsi);
        }
        Collections.reverse(RSIs);
    }

    public ArrayList<Float> getRSIs() {
        return RSIs;
    }

    /**
     *      * SMA(C,N,M) = (M*C+(N-M)*Y')/N
     *      * LC := REF(CLOSE,1);
     *      * RSI$1:SMA(MAX(CLOSE-LC,0),N1,1)/SMA(ABS(CLOSE-LC),N1,1)*100;
     *      
     */
    public List<Float> countRSIdatas(ArrayList<KLineBean> kLineBeens, int days) {
//        List rsiList = new ArrayList();
        RSIs = new ArrayList<>();
        if (kLineBeens == null) {
            return null;
        }
        if (days > kLineBeens.size()) {
            return null;
        }
        float smaMax = 0, smaAbs = 0;//默认0
        float lc = 0;//默认0
        float close = 0;
        float rsi = 0;

        for (int i = 0; i < kLineBeens.size(); i++) {
            if (i > 0) {
                KLineBean entity = kLineBeens.get(i);
                lc = kLineBeens.get(i - 1).close;
                close = entity.close;
                smaMax = countSMA(Math.max(close - lc, 0f), days, 1, smaMax);
                smaAbs = countSMA(Math.abs(close - lc), days, 1, smaAbs);
                rsi = smaMax / smaAbs * 100;
                RSIs.add(rsi);
            } else {
                RSIs.add(100f);
            }

            Log.e(TAG, "" + rsi);
        }
        Log.v(TAG, "rsiList.size()=" + RSIs.size());

        int size = kLineBeens.size() - RSIs.size();
        for (int i = 0; i < size; i++) {
//            rsiList.add(0, new KCandleObj());
            RSIs.add(100f);
        }

        return RSIs;
    }

    /**
     *      * SMA(C,N,M) = (M*C+(N-M)*Y')/N
     *      * C=今天收盘价－昨天收盘价    N＝就是周期比如 6或者12或者24， M＝权重，其实就是1
     *      *
     *      * @param c   今天收盘价－昨天收盘价
     *      * @param n   周期
     *      * @param m   1
     *      * @param sma 上一个周期的sma
     *      * @return
     *      
     */
    public float countSMA(float c, float n, float m, float sma) {
        return (m * c + (n - m) * sma) / n;
    }
}
