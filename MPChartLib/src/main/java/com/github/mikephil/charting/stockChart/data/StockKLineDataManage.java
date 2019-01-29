package com.github.mikephil.charting.stockChart.data;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.R;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.stockChart.model.KLineBean;
import com.github.mikephil.charting.stockChart.model.bean.BOLLEntity;
import com.github.mikephil.charting.stockChart.model.bean.KDJEntity;
import com.github.mikephil.charting.stockChart.model.bean.KMAEntity;
import com.github.mikephil.charting.stockChart.model.bean.MACDEntity;
import com.github.mikephil.charting.stockChart.model.bean.RSIEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * K线数据解析
 */
public class StockKLineDataManage {
    private Context mContext;
    private ArrayList<KLineBean> kDatas = new ArrayList<>();
    private float offSet = 0.99f;//K线图最右边偏移量
    private String assetId;
    private boolean landscape = false;//横屏还是竖屏

    //MA参数
    public int N1 = 5;
    public int N2 = 10;
    public int N3 = 20;
    //EMA参数
    public int EMAN1 = 5;
    public int EMAN2 = 10;
    public int EMAN3 = 30;
    //SMA参数
    public int SMAN = 14;
    //BOLL参数
    public int BOLLN = 26;

    //MACD参数
    public int SHORT = 12;
    public int LONG = 26;
    public int M = 9;
    //KDJ参数
    public int KDJN = 9;
    public int KDJM1 = 3;
    public int KDJM2 = 3;
    //CCI参数
    public int CCIN = 14;
    //RSI参数
    public int RSIN1 = 6;
    public int RSIN2 = 12;
    public int RSIN3 = 24;

    //X轴数据
    private ArrayList<String> xVal = new ArrayList<>();

    private CandleDataSet candleDataSet;//蜡烛图集合
    private BarDataSet volumeDataSet;//成交量集合
    private BarDataSet barDataMACD;//MACD集合
    private CandleDataSet bollCandleDataSet;//BOLL蜡烛图集合

    private List<ILineDataSet> lineDataMA = new ArrayList<>();

    private List<ILineDataSet> lineDataMACD = new ArrayList<>();
    private ArrayList<BarEntry> macdData = new ArrayList<>();
    private ArrayList<Entry> deaData = new ArrayList<>();
    private ArrayList<Entry> difData = new ArrayList<>();

    private List<ILineDataSet> lineDataKDJ = new ArrayList<>();
    private ArrayList<Entry> kData = new ArrayList<>();
    private ArrayList<Entry> dData = new ArrayList<>();
    private ArrayList<Entry> jData = new ArrayList<>();

    private List<ILineDataSet> lineDataBOLL = new ArrayList<>();
    private ArrayList<Entry> bollDataUP = new ArrayList<>();
    private ArrayList<Entry> bollDataMB = new ArrayList<>();
    private ArrayList<Entry> bollDataDN = new ArrayList<>();

    private List<ILineDataSet> lineDataRSI = new ArrayList<>();
    private ArrayList<Entry> rsiData6 = new ArrayList<>();
    private ArrayList<Entry> rsiData12 = new ArrayList<>();
    private ArrayList<Entry> rsiData24 = new ArrayList<>();
    private double preClosePrice;//K线图昨收价
    // 当前最大成交量,决定成交量图的Y轴范围
    private float maxVolume;
    // 股票码
    private String stockCode = "sz000001";

    private String kLineType = "day";
    private KMAEntity kmaEntity5;
    private KMAEntity kmaEntity10;
    private KMAEntity kmaEntity20;

    public StockKLineDataManage(Context context) {
        mContext = context;
    }

    /**
     * 解析K线数据
     */
    public void parseKlineData(String result) {
        this.assetId = assetId;
        this.landscape = landscape;
        kDatas.clear();
        lineDataMA.clear();
        try {
//            JSONObject jsonObject = new JSONObject(result);
//            JSONArray list = jsonObject.optJSONObject("data").optJSONObject(stockCode).optJSONArray(kLineType);
//            preClosePrice = (float) jsonObject.optJSONObject("data").optJSONObject(stockCode).optJSONObject("qt").optJSONArray(stockCode).optDouble(4);
            String[] split = result.split(";");
            ArrayList<CandleEntry> candleEntries = new ArrayList<>();
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            ArrayList<Entry> line5Entries = new ArrayList<>();
            ArrayList<Entry> line10Entries = new ArrayList<>();
            ArrayList<Entry> line20Entries = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                String[] split1 = split[i].split(",");
                KLineBean kLineData = new KLineBean();
                kLineData.date = split1[6];
                kLineData.open = Float.parseFloat(split1[0]);
                kLineData.close = Float.parseFloat(split1[1]);
                kLineData.high = Float.parseFloat(split1[2]);
                kLineData.low = Float.parseFloat(split1[3]);
                kLineData.volume = Float.parseFloat(split1[4]);
                maxVolume = Math.max(kLineData.volume, maxVolume);
                // 填充X轴显示数据
//                    xValuesLabel.put(i, kLineData.date);
                // 存储K线数据
                kDatas.add(kLineData);
                // 填充X轴数据
                xVal.add(kLineData.date);
                // 蜡烛图数据填充
                candleEntries.add(new CandleEntry(i, i + offSet, kLineData.high, kLineData.low, kLineData.open, kLineData.close));
                float color = kLineData.open > kLineData.close ? 0f : 1f;
                barEntries.add(new BarEntry(i, i + offSet, kLineData.volume, color));
            }
            // 设置蜡烛图显示效果
            candleDataSet = setACandle(candleEntries);
            // BOOL图蜡烛图设置
            bollCandleDataSet = setBOLLCandle(candleEntries);
            // 成交量Bar设置
            volumeDataSet = setABar(barEntries, "成交量");
            // K线均线计算
            setKlineMa(line5Entries, line10Entries, line20Entries);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setKlineMa(ArrayList<Entry> line5Entries, ArrayList<Entry> line10Entries, ArrayList<Entry> line20Entries) {
        kmaEntity5 = new KMAEntity(getKLineDatas(), 5);
        kmaEntity10 = new KMAEntity(getKLineDatas(), 10);
        kmaEntity20 = new KMAEntity(getKLineDatas(), 20);
        for (int i = 0; i < kmaEntity5.getMAs().size(); i++) {
            if (i >= 5) {
                line5Entries.add(new Entry(i, i + offSet, kmaEntity5.getMAs().get(i)));
            }
            if (i >= 10) {
                line10Entries.add(new Entry(i, i + offSet, kmaEntity10.getMAs().get(i)));
            }
            if (i >= 20) {
                line20Entries.add(new Entry(i, i + offSet, kmaEntity20.getMAs().get(i)));
            }
        }
        lineDataMA.add(setALine(ColorType.blue, line5Entries, false));
        lineDataMA.add(setALine(ColorType.yellow, line10Entries, false));
        lineDataMA.add(setALine(ColorType.purple, line20Entries, false));
    }

    /**
     * 初始化自己计算MACD
     */
    public void initMACD() {
        MACDEntity macdEntity = new MACDEntity(getKLineDatas(), SHORT, LONG, M);

        macdData = new ArrayList<>();
        deaData = new ArrayList<>();
        difData = new ArrayList<>();
        for (int i = 0; i < macdEntity.getMACD().size(); i++) {
            macdData.add(new BarEntry(i, i + offSet, macdEntity.getMACD().get(i), macdEntity.getMACD().get(i)));
            deaData.add(new Entry(i, i + offSet, macdEntity.getDEA().get(i)));
            difData.add(new Entry(i, i + offSet, macdEntity.getDIF().get(i)));
        }
        barDataMACD = setABar(macdData);
        lineDataMACD.add(setALine(ColorType.blue, deaData));
        lineDataMACD.add(setALine(ColorType.yellow, difData));
    }

    /**
     * 初始化自己计算KDJ
     */
    public void initKDJ() {
        KDJEntity kdjEntity = new KDJEntity(getKLineDatas(), KDJN, KDJM1, KDJM2);

        kData = new ArrayList<>();
        dData = new ArrayList<>();
        jData = new ArrayList<>();
        for (int i = 0; i < kdjEntity.getD().size(); i++) {
            kData.add(new Entry(i, i + offSet, kdjEntity.getK().get(i)));
            dData.add(new Entry(i, i + offSet, kdjEntity.getD().get(i)));
            jData.add(new Entry(i, i + offSet, kdjEntity.getJ().get(i)));
        }
        lineDataKDJ.add(setALine(ColorType.blue, kData, "KDJ" + N1, false));
        lineDataKDJ.add(setALine(ColorType.yellow, dData, "KDJ" + N2, false));
        lineDataKDJ.add(setALine(ColorType.purple, jData, "KDJ" + N3, true));
    }

    /**
     * 初始化自己计算BOLL
     */
    public void initBOLL() {
        BOLLEntity bollEntity = new BOLLEntity(getKLineDatas(), BOLLN);
        bollDataUP = new ArrayList<>();
        bollDataMB = new ArrayList<>();
        bollDataDN = new ArrayList<>();
        for (int i = 0; i < bollEntity.getUPs().size(); i++) {
            Log.e("size", "" + bollEntity.getUPs().get(i));
            if (i >= 25) {
                bollDataUP.add(new Entry(i, i + offSet, bollEntity.getUPs().get(i)));
                bollDataMB.add(new Entry(i, i + offSet, bollEntity.getMBs().get(i)));
                bollDataDN.add(new Entry(i, i + offSet, bollEntity.getDNs().get(i)));
            }
        }
        lineDataBOLL.add(setALine(ColorType.blue, bollDataUP, false));
        lineDataBOLL.add(setALine(ColorType.yellow, bollDataMB, false));
        lineDataBOLL.add(setALine(ColorType.purple, bollDataDN, false));
    }

    /**
     * 初始化自己计算RSI
     */
    public void initRSI() {
        RSIEntity rsiEntity6 = new RSIEntity(getKLineDatas(), RSIN1);
        RSIEntity rsiEntity12 = new RSIEntity(getKLineDatas(), RSIN2);
        RSIEntity rsiEntity24 = new RSIEntity(getKLineDatas(), RSIN3);
        rsiData6 = new ArrayList<>();
        rsiData12 = new ArrayList<>();
        rsiData24 = new ArrayList<>();
        for (int i = 0; i < rsiEntity6.getRSIs().size(); i++) {
            if (i >= RSIN1) {
                rsiData6.add(new Entry(i, i + offSet, rsiEntity6.getRSIs().get(i)));
            }
            if (i >= RSIN2) {
                rsiData12.add(new Entry(i, i + offSet, rsiEntity12.getRSIs().get(i)));
            }
            if (i >= RSIN3) {
                rsiData24.add(new Entry(i, i + offSet, rsiEntity24.getRSIs().get(i)));
            }
        }
        lineDataRSI.add(setALine(ColorType.blue, rsiData6, "RSI" + RSIN1, true));
        lineDataRSI.add(setALine(ColorType.yellow, rsiData12, "RSI" + RSIN2, true));
        lineDataRSI.add(setALine(ColorType.purple, rsiData24, "RSI" + RSIN3, true));
    }

    private CandleDataSet setACandle(ArrayList<CandleEntry> candleEntries) {
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "KLine");
        candleDataSet.setDrawHorizontalHighlightIndicator(landscape);
        candleDataSet.setHighlightEnabled(landscape);
        candleDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        candleDataSet.setShadowColorSameAsCandle(true);
        candleDataSet.setValueTextSize(10);
        candleDataSet.setDrawValues(true);

        return candleDataSet;
    }

    private CandleDataSet setBOLLCandle(ArrayList<CandleEntry> candleEntries) {
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "KLine");
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setHighlightEnabled(landscape);
        candleDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        candleDataSet.setDrawValues(false);
        candleDataSet.setShowCandleBar(false);
        return candleDataSet;
    }

    private LineDataSet setALine(ColorType ma, ArrayList<Entry> lineEntries) {
        String label = "ma" + ma;
        return setALine(ma, lineEntries, label);
    }

    private LineDataSet setALine(ColorType ma, ArrayList<Entry> lineEntries, boolean highlightEnable) {
        String label = "ma" + ma;
        return setALine(ma, lineEntries, label, highlightEnable);
    }

    private LineDataSet setALine(ColorType ma, ArrayList<Entry> lineEntries, String label) {
        boolean highlightEnable = false;
        return setALine(ma, lineEntries, label, highlightEnable);
    }

    private LineDataSet setALine(ColorType colorType, ArrayList<Entry> lineEntries, String label, boolean highlightEnable) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, label);
        lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
        lineDataSetMa.setHighlightEnabled(landscape ? highlightEnable : landscape);
        lineDataSetMa.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        lineDataSetMa.setDrawValues(false);
        if (colorType == ColorType.blue) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma5));
        } else if (colorType == ColorType.yellow) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma10));
        } else if (colorType == ColorType.purple) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma20));
        }
        lineDataSetMa.setLineWidth(0.6f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSetMa;
    }

    private BarDataSet setABar(ArrayList<BarEntry> barEntries) {
        String label = "BarDataSet";
        return setABar(barEntries, label);
    }

    private BarDataSet setABar(ArrayList<BarEntry> barEntries, String label) {
        BarDataSet barDataSet = new BarDataSet(barEntries, label);
        barDataSet.setHighlightEnabled(landscape);
        barDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        barDataSet.setValueTextSize(10);
        barDataSet.setDrawValues(false);
        barDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        barDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        barDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        barDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        barDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        return barDataSet;
    }


    float sum = 0;

    private float getSum(Integer a, Integer b) {
        sum = 0;
        if (a < 0) {
            return 0;
        }
        for (int i = a; i <= b; i++) {
            sum += getKLineDatas().get(i).close;
        }
        return sum;
    }

    public void addAKLineData(KLineBean kLineData) {
        kDatas.add(kLineData);
    }

    public void addKLineDatas(List<KLineBean> kLineData) {
        kDatas.addAll(kLineData);
    }

    public synchronized ArrayList<KLineBean> getKLineDatas() {
        return kDatas;
    }

    public void resetKLineData() {
        kDatas.clear();
    }

    public void setKLineData(ArrayList<KLineBean> datas) {
        kDatas.clear();
        kDatas.addAll(datas);
    }

    public ArrayList<String> getxVals() {
        return xVal;
    }

    public List<ILineDataSet> getLineDataMA() {
        return lineDataMA;
    }

    public List<ILineDataSet> getLineDataBOLL() {
        return lineDataBOLL;
    }

    public List<ILineDataSet> getLineDataKDJ() {
        return lineDataKDJ;
    }

    public List<ILineDataSet> getLineDataRSI() {
        return lineDataRSI;
    }

    public List<ILineDataSet> getLineDataMACD() {
        return lineDataMACD;
    }

    public BarDataSet getBarDataMACD() {
        return barDataMACD;
    }

    public BarDataSet getVolumeDataSet() {
        return volumeDataSet;
    }

    public CandleDataSet getCandleDataSet() {
        return candleDataSet;
    }

    public CandleDataSet getBollCandleDataSet() {
        return bollCandleDataSet;
    }

    public float getOffSet() {
        return offSet;
    }

    public ArrayList<BarEntry> getMacdData() {
        return macdData;
    }

    public ArrayList<Entry> getDeaData() {
        return deaData;
    }

    public ArrayList<Entry> getDifData() {
        return difData;
    }

    public ArrayList<Entry> getkData() {
        return kData;
    }

    public ArrayList<Entry> getdData() {
        return dData;
    }

    public ArrayList<Entry> getjData() {
        return jData;
    }

    public ArrayList<Entry> getBollDataUP() {
        return bollDataUP;
    }

    public ArrayList<Entry> getBollDataMB() {
        return bollDataMB;
    }

    public ArrayList<Entry> getBollDataDN() {
        return bollDataDN;
    }

    public ArrayList<Entry> getRsiData6() {
        return rsiData6;
    }

    public ArrayList<Entry> getRsiData12() {
        return rsiData12;
    }

    public ArrayList<Entry> getRsiData24() {
        return rsiData24;
    }

    public void setOneMaValue(LineData lineData, int i) {
        for (int k = 0; k < lineData.getDataSets().size(); k++) {
            ILineDataSet lineDataSet = lineData.getDataSetByIndex(k);
            lineDataSet.removeEntryByXValue(i);
            if (k == 0) {
                if (i >= N1) {
                    sum = 0;
                    float all5 = getSum(i - (N1 - 1), i) / N1;
                    lineDataSet.addEntry(new Entry(i, i + offSet, all5));
                }
            } else if (k == 1) {
                if (i >= N2) {
                    sum = 0;
                    float all10 = getSum(i - (N2 - 1), i) / N2;
                    lineDataSet.addEntry(new Entry(i, i + offSet, all10));
                }
            } else if (k == 2) {
                if (i >= N3) {
                    sum = 0;
                    float all20 = getSum(i - (N3 - 1), i) / N3;
                    lineDataSet.addEntry(new Entry(i, i + offSet, all20));
                }
            }
        }
    }

    public KMAEntity getKmaEntity5() {
        return kmaEntity5;
    }

    public KMAEntity getKmaEntity10() {
        return kmaEntity10;
    }

    public KMAEntity getKmaEntity20() {
        return kmaEntity20;
    }

    enum ColorType {
        blue,
        yellow,
        purple
    }

    public String getAssetId() {
        return assetId;
    }

    public double getPreClosePrice() {
        return preClosePrice;
    }
}
