package com.android.stockapp.ui.market.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.stockapp.R;
import com.android.stockapp.common.data.ChartData;
import com.android.stockapp.ui.base.BaseFragment;
import com.android.stockapp.ui.market.activity.StockDetailLandActivity;
import com.github.mikephil.charting.stockChart.CoupleChartGestureListener;
import com.github.mikephil.charting.stockChart.data.FeatureKLineDataManage;
import com.github.mikephil.charting.stockChart.data.StockKLineDataManage;
import com.github.mikephil.charting.stockChart.view.KLineView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * K线
 */
public class ChartKLineFragment extends BaseFragment {


    @BindView(R.id.combinedchart)
    KLineView combinedchart;
    Unbinder unbinder;

    private int mType;//日K：1；周K：7；月K：30
    private boolean land;//是否横屏
    private StockKLineDataManage kLineData;
    private int indexType = 1;

    public static ChartKLineFragment newInstance(int type, boolean land) {
        ChartKLineFragment fragment = new ChartKLineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putBoolean("landscape", land);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_kline;
    }

    @Override
    protected void initBase(View view) {
        kLineData = new StockKLineDataManage(getActivity());

//        FeatureKLineDataManage kLineData = new FeatureKLineDataManage(getActivity());

        combinedchart.initChart(land);
//        try {
//            if (mType == 1) {
//                object = new JSONObject(ChartData.KLINEDATA);
//            } else if (mType == 7) {
//                object = new JSONObject(ChartData.KLINEWEEKDATA);
//            } else if (mType == 30) {
//                object = new JSONObject(ChartData.KLINEMONTHDATA);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        kLineData.parseKlineData(ChartData.FEATUREKLINEDATA);
//        kLineData.parseKlineData(ChartData.FEATUREKLINEDATA);


        combinedchart.setDataToChart(kLineData);

        combinedchart.getGestureListenerCandle().setCoupleClick(new CoupleChartGestureListener.CoupleClick() {
            @Override
            public void singleClickListener() {
                if (!land) {
                    Intent intent = new Intent(getActivity(), StockDetailLandActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        combinedchart.getGestureListenerBar().setCoupleClick(new CoupleChartGestureListener.CoupleClick() {
            @Override
            public void singleClickListener() {
                if (land) {
                    loadIndexData(indexType < 5 ? ++indexType : 1);
                } else {
                    Intent intent = new Intent(getActivity(), StockDetailLandActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt("type");
        land = getArguments().getBoolean("landscape");
    }

    private void loadIndexData(int type) {
        indexType = type;
        switch (indexType) {
            case 1://成交量
                combinedchart.doBarChartSwitch(indexType);
                break;
            case 2://请求MACD
                kLineData.initMACD();
                combinedchart.doBarChartSwitch(indexType);
                break;
            case 3://请求KDJ
                kLineData.initKDJ();
                combinedchart.doBarChartSwitch(indexType);
                break;
            case 4://请求BOLL
                kLineData.initBOLL();
                combinedchart.doBarChartSwitch(indexType);
                break;
            case 5://请求RSI
                kLineData.initRSI();
                combinedchart.doBarChartSwitch(indexType);
                break;
            default:
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
